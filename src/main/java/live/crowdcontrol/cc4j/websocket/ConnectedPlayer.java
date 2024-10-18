package live.crowdcontrol.cc4j.websocket;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import live.crowdcontrol.cc4j.CCEventType;
import live.crowdcontrol.cc4j.CCPlayer;
import live.crowdcontrol.cc4j.CrowdControl;
import live.crowdcontrol.cc4j.util.CloseData;
import live.crowdcontrol.cc4j.util.EventManager;
import live.crowdcontrol.cc4j.util.TokenUtils;
import live.crowdcontrol.cc4j.websocket.data.*;
import live.crowdcontrol.cc4j.websocket.http.GameSessionStartData;
import live.crowdcontrol.cc4j.websocket.http.GameSessionStartPayload;
import live.crowdcontrol.cc4j.websocket.http.GameSessionStopData;
import live.crowdcontrol.cc4j.websocket.http.GameSessionStopPayload;
import live.crowdcontrol.cc4j.websocket.payload.*;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.CompletableFuture;

/**
 * A player connected to the server.
 */
@ApiStatus.Internal
public class ConnectedPlayer extends WebSocketClient implements CCPlayer {
	public static final @NotNull ObjectMapper JACKSON;
	protected static final @NotNull Logger log = LoggerFactory.getLogger(ConnectedPlayer.class);
	protected final @NotNull Set<String> subscriptions = new HashSet<>();
	protected final @NotNull EventManager eventManager;
	protected final @NotNull UUID uuid;
	protected final @NotNull Path tokenPath;
	protected final @NotNull CrowdControl parent;
	protected @Nullable String connectionID;
	protected @Nullable String token;
	protected @Nullable UserToken userToken;
	protected @Nullable String gameSessionID;
	protected boolean privateAvailable;
	protected int sleep = 1;

	static {
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
		SimpleModule module = new SimpleModule("CrowdControlSerializers");
		module.addDeserializer(CCName.class, new CCName.CCNameAdapter());
		mapper.registerModule(module);
		JACKSON = mapper;
	}

	// WebSocket Impl

	public ConnectedPlayer(@NotNull UUID uuid, @NotNull CrowdControl parent) {
		super(URI.create("wss://m9xw37fv0b.execute-api.us-east-1.amazonaws.com/lexikiq"));

		this.parent = parent;
		this.uuid = uuid;
		this.tokenPath = parent.getDataFolder().resolve(uuid + ".token");
		this.eventManager = new EventManager(parent);

		this.eventManager.registerEventConsumer(CCEventType.CONNECTED, handshake -> {
			send(new SocketRequest("whoami"));
			subscribe();
		});
		this.eventManager.registerEventConsumer(CCEventType.DISCONNECTED, data -> {
			this.connectionID = null;
			if (this.parent.getPlayer(uuid) != this) return;
			try {
				Thread.sleep(sleep * 1000L);
			} catch (InterruptedException ignored) {
			}
			sleep *= 2;
			reconnect(); // reconnect!
		});
		this.eventManager.registerEventConsumer(CCEventType.IDENTIFIED, payload -> this.connectionID = payload.getConnectionId());
		this.eventManager.registerEventRunnable(CCEventType.AUTHENTICATED, this::subscribe);
		this.eventManager.registerEventConsumer(CCEventType.SUBSCRIBED, payload -> {
			assert this.userToken != null : "Subscribed before authenticating";
			this.subscriptions.addAll(payload.getSuccess());
			privateAvailable = this.subscriptions.contains("prv/" + this.userToken.getId());
		});
		this.eventManager.registerEventConsumer(CCEventType.EFFECT_REQUEST, payload -> this.parent.executeEffect(payload, this));

		loadToken();
	}

	@Override
	public void onOpen(ServerHandshake handshake) {
		eventManager.dispatch(CCEventType.CONNECTED, handshake);
	}

	@Override
	public void onMessage(String message) {
		try {
			SocketEvent event = JACKSON.readValue(message, SocketEvent.class);
			switch (event.type) {
				case "whoami":
					eventManager.dispatch(CCEventType.IDENTIFIED, JACKSON.treeToValue(event.payload, WhoAmIPayload.class));
					break;
				case "login-success":
					setToken(JACKSON.treeToValue(event.payload, LoginSuccessPayload.class).getToken());
					saveToken();
					break;
				case "subscription-result":
					eventManager.dispatch(CCEventType.SUBSCRIBED, JACKSON.treeToValue(event.payload, SubscriptionResultPayload.class));
					break;
				case "effect-request":
					if (!event.domain.equals("pub") && !event.domain.equals("prv")) return;
					PublicEffectPayload payload = JACKSON.treeToValue(event.payload, PublicEffectPayload.class);
					if (!"game".equals(payload.getEffect().getType())) return;
					eventManager.dispatch(event.domain.equals("pub") ? CCEventType.PUB_EFFECT_REQUEST : CCEventType.PRV_EFFECT_REQUEST, payload);
					if (privateAvailable && event.domain.equals("pub")) return;
					eventManager.dispatch(CCEventType.EFFECT_REQUEST, payload);
					break;
				case "game-session-start":
					eventManager.dispatch(CCEventType.SESSION_STARTED, JACKSON.treeToValue(event.payload, GameSessionStartPayload.class));
					break;
				case "game-session-stop":
					eventManager.dispatch(CCEventType.SESSION_STOPPED, JACKSON.treeToValue(event.payload, GameSessionStopPayload.class));
					break;
				// TODO: catch refund and cancel effect
				default:
					log.debug("Ignoring unknown event {} on domain {}", event.type, event.domain);
			}
		} catch (Exception e) {
			log.warn("Failed to handle incoming message {}", message, e);
		}
	}

	@Override
	public void onClose(int code, String reason, boolean remote) {
		eventManager.dispatch(CCEventType.DISCONNECTED, new CloseData(code, reason, remote));
	}

	@Override
	public void onError(Exception ex) {
		log.error("An unknown WebSocket error has occurred", ex);
	}

	// Semi Boilerplate

	void send(SocketRequest request) {
		String message;
		try {
			message = JACKSON.writeValueAsString(request);
		} catch (JsonProcessingException e) {
			log.warn("Failed to encode message {}", request, e);
			return;
		}

		if (!isOpen()) {
			log.warn("Attempted to send message {} before connecting", message);
			return;
		}

		try {
			send(message);
			log.info("Sent message {}", message);
		} catch (Exception e) {
			log.warn("Failed to send message {}", message, e);
		}
	}

	public boolean sendRPC(CallData<?> call) {
		if (token == null) return false;

		send(new SocketRequest(
			"rpc",
			new RemoteProcedureCallData(
				token,
				call
			)
		));

		return true;
	}

	public boolean sendResponse(@NotNull CCEffectResponse response) {
		if (response.getStatus() == ResponseStatus.DELAY_ESTIMATED) return false; // unused
		eventManager.dispatch(CCEventType.EFFECT_RESPONSE, response);
		return sendRPC(new CallData<>(
			CallDataMethod.EFFECT_RESPONSE,
			Collections.singletonList(response)
		));
	}

	@Override
	public boolean sendReport(@NotNull CCEffectReport @NotNull ... reports) {
		if (reports.length == 0) return false;
		// TODO: don't send redundant reports
		return sendRPC(new CallData<>(
			CallDataMethod.EFFECT_REPORT,
			Arrays.asList(reports)
		));
	}

	@Override
	public @NotNull CompletableFuture<?> startSession(@NotNull CCEffectReport @NotNull ... reports) {
		if (this.gameSessionID != null) return CompletableFuture.completedFuture(null);
		if (this.token == null) return CompletableFuture.completedFuture(null);
		return parent.getHttpUtil().apiPost("/game-session/start", GameSessionStartPayload.class, this.token, new GameSessionStartData(
			parent.getGamePackId(),
			Arrays.asList(reports)
		)).handle((payload, e) -> {
			if (e != null) {
				log.warn("Failed to query URL", e);
				return null;
			}
			if (payload == null) {
				log.warn("Got bad payload");
				return null;
			}
			this.gameSessionID = payload.getGameSessionId();
			return null;
		});
	}

	@Override
	public @NotNull CompletableFuture<?> stopSession() {
		if (this.gameSessionID == null) return CompletableFuture.completedFuture(null);
		if (this.token == null) return CompletableFuture.completedFuture(null);
		return parent.getHttpUtil().apiPost("/game-session/stop", this.token, new GameSessionStopData(this.gameSessionID)).handle((payload, e) -> {
			if (e != null) {
				log.warn("Failed to query URL", e);
				return null;
			}
			this.gameSessionID = null;
			return null;
		});
	}

	public boolean setToken(String token) {
		try {
			this.userToken = JACKSON.readValue(TokenUtils.decodePayload(token), UserToken.class);

			if (Instant.ofEpochSecond(this.userToken.getExp()).plus(1, ChronoUnit.DAYS).isBefore(Instant.now())) {
				log.warn("User {}'s auth token has expired", uuid);
				this.userToken = null;
				eventManager.dispatch(CCEventType.AUTH_EXPIRED);
				return false;
			}
		} catch (Exception e) {
			log.warn("Failed to set token {}", token, e);
			return false;
		}
		this.token = token;
		eventManager.dispatch(CCEventType.AUTHENTICATED);
		return true;
	}

	protected boolean loadToken() {
		if (!Files.exists(tokenPath))
			return false;

		try {
			try (BufferedReader reader = Files.newBufferedReader(tokenPath)) {
				return setToken(reader.readLine());
			}
		} catch (Exception e) {
			log.warn("Failed to read user {} token", uuid, e);
			return false;
		}
	}

	protected void saveToken() {
		if (this.token == null)
			return;

		try {
			Files.write(tokenPath, token.getBytes(StandardCharsets.UTF_8));
		} catch (Exception e) {
			log.warn("Failed to write user {} token", uuid, e);
		}
	}

	protected void subscribe() {
		if (this.token == null || this.userToken == null)
			return;

		Set<String> subscribeTo = new HashSet<>(Arrays.asList(
			"pub/" + this.userToken.getId(),
			"prv/" + this.userToken.getId()
		));

		subscribeTo.removeAll(subscriptions);

		if (subscribeTo.isEmpty()) return;

		send(new SocketRequest(
			"subscribe",
			new SubscriptionData(
				subscribeTo,
				this.token
			)
		));
	}

	// True Boilerplate

	@NotNull
	public UUID getUuid() {
		return uuid;
	}

	@Nullable
	public String getConnectionId() {
		return connectionID;
	}

	@Nullable
	public String getToken() {
		return token;
	}

	@Nullable
	public UserToken getUserToken() {
		return userToken;
	}

	@Override
	public @NotNull EventManager getEventManager() {
		return eventManager;
	}

	@Override
	public @Nullable String getGameSessionId() {
		return gameSessionID;
	}
}
