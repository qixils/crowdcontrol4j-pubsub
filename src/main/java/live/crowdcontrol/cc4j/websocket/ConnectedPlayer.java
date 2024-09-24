package live.crowdcontrol.cc4j.websocket;

import com.auth0.jwt.JWT;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import live.crowdcontrol.cc4j.CCEventType;
import live.crowdcontrol.cc4j.CCPlayer;
import live.crowdcontrol.cc4j.CrowdControl;
import live.crowdcontrol.cc4j.util.CloseData;
import live.crowdcontrol.cc4j.util.EventManager;
import live.crowdcontrol.cc4j.websocket.data.*;
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

/**
 * A player connected to the server.
 */
@ApiStatus.Internal
public class ConnectedPlayer extends WebSocketClient implements CCPlayer {
	protected static final ObjectMapper JACKSON;
	protected static final Logger log = LoggerFactory.getLogger(ConnectedPlayer.class);
	protected final @NotNull Set<String> subscriptions = new HashSet<>();
	protected final @NotNull EventManager eventManager;
	protected final @NotNull UUID uuid;
	protected final @NotNull Path tokenPath;
	protected final @NotNull CrowdControl parent;
	protected @Nullable String connectionID;
	protected @Nullable String token;
	protected @Nullable UserToken userToken;
	protected boolean privateAvailable;

	static {
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		SimpleModule module = new SimpleModule("CrowdControlSerializers");
		module.addDeserializer(CCName.class, new CCName.CCNameAdapter());
		mapper.registerModule(module);
		JACKSON = mapper;
	}

	// WebSocket Impl

	public ConnectedPlayer(@NotNull UUID uuid, @NotNull CrowdControl parent) {
		super(URI.create("wss://pubsub.crowdcontrol.live/"));

		this.parent = parent;
		this.uuid = uuid;
		this.tokenPath = parent.getDataFolder().resolve(uuid + ".token");
		this.eventManager = new EventManager(parent);

		this.eventManager.registerEventConsumer(CCEventType.CONNECTED, handshake -> send(new SocketRequest("whoami")));
		this.eventManager.registerEventConsumer(CCEventType.DISCONNECTED, data -> {
			this.connectionID = null;
			if (this.parent.getPlayer(uuid) != this) return;
			connect(); // reconnect!
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
		try {
			send(JACKSON.writeValueAsString(request));
		} catch (JsonProcessingException e) {
			log.warn("Failed to send message {}", request, e);
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
		return sendRPC(new CallData<>(
			CallDataMethod.EFFECT_RESPONSE,
			Collections.singletonList(response)
		));
	}

	@Override
	public boolean sendReport(@NotNull CCEffectReport @NotNull ... reports) {
		if (reports.length == 0) return false;
		return sendRPC(new CallData<>(
			CallDataMethod.EFFECT_REPORT,
			Arrays.asList(reports)
		));
	}

	public boolean setToken(String token) {
		try {
			this.userToken = JACKSON.readValue(JWT.decode(token).getPayload(), UserToken.class);

			if (Instant.ofEpochSecond(this.userToken.getExp()).plus(1, ChronoUnit.DAYS).isBefore(Instant.now())) {
				log.warn("User {}'s auth token has expired", uuid);
				this.userToken = null;
				eventManager.dispatch(CCEventType.AUTH_EXPIRED);
				return false;
			}
		} catch (JsonProcessingException e) {
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
	public String getConnectionID() {
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
}
