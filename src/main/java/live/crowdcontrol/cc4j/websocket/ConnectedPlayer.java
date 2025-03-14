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
import live.crowdcontrol.cc4j.websocket.http.*;
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
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * A player connected to the server.
 */
@ApiStatus.Internal
public class ConnectedPlayer extends WebSocketClient implements CCPlayer {
	public static final @NotNull ObjectMapper JACKSON;
	protected static final @NotNull Logger log = LoggerFactory.getLogger(ConnectedPlayer.class);
	protected final @NotNull Set<String> subscriptions = new HashSet<>();
	protected final Map<String, Boolean> visible = new HashMap<>();
	protected final Map<String, Boolean> available = new HashMap<>();
	protected final @NotNull EventManager eventManager;
	protected final @NotNull UUID uuid;
	protected final @NotNull Path tokenPath;
	protected final @NotNull CrowdControl parent;
	protected @Nullable String authCode;
	protected @Nullable String token;
	protected @Nullable UserToken userToken;
	protected @Nullable String gameSessionID;
	protected @Nullable String lastGameSessionID;
	protected @Nullable CompletableFuture<Void> pendingAuthCode;
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
			subscribe(); // since token load may have triggered already
			regenerateAuthCode();
		});
		this.eventManager.registerEventConsumer(CCEventType.DISCONNECTED, data -> {
			if (this.parent.getPlayer(uuid) != this) return;
			try {
				Thread.sleep(sleep * 1000L);
			} catch (InterruptedException ignored) {
			}
			sleep *= 2;
			reconnect(); // reconnect!
		});
		this.eventManager.registerEventConsumer(CCEventType.GENERATED_AUTH_CODE, payload -> {
			this.authCode = payload.code();
			if (pendingAuthCode != null) {
				pendingAuthCode.complete(null);
				pendingAuthCode = null;
			}
		});
		this.eventManager.registerEventConsumer(CCEventType.REDEEMED_AUTH_CODE, payload -> {
			parent.getHttpUtil().apiPost(
				"/auth/application/token",
				AuthApplicationTokenPayload.class,
				null,
				new AuthApplicationTokenData(parent.getAppID(), payload.code(), parent.getAppSecret())
			).handle((tokenPayload, e) -> {
				if (e != null) {
					log.warn("Failed to query URL", e);
					return null;
				}
				authCode = null;
				setToken(tokenPayload.token());
				return null;
			});
		});
		this.eventManager.registerEventConsumer(CCEventType.ERRORED_AUTH_CODE, payload -> {
			log.warn("Failed to redeem auth code for reason {}, generating new one", payload.message());
			send(new SocketRequest(GenerateAuthCodeData.ACTION, new GenerateAuthCodeData(parent.getAppID())));
		});
		this.eventManager.registerEventRunnable(CCEventType.AUTHENTICATED, this::subscribe);
		this.eventManager.registerEventConsumer(CCEventType.SUBSCRIBED, payload -> {
			assert this.userToken != null : "Subscribed before authenticating";
			this.subscriptions.addAll(payload.getSuccess());
		});
		this.eventManager.registerEventConsumer(CCEventType.EFFECT_REQUEST, payload -> this.parent.executeEffect(payload, this));
		this.eventManager.registerEventConsumer(CCEventType.EFFECT_FAILURE, payload -> parent.cancelByRequestId(payload.getRequestId()));

		loadToken();
	}

	@Override
	public void onOpen(ServerHandshake handshake) {
		eventManager.dispatch(CCEventType.CONNECTED, handshake);
	}

	@Override
	public void onMessage(String message) {
		try {
			log.info("Received message {}", message);
			SocketEvent event = JACKSON.readValue(message, SocketEvent.class);
			switch (event.type) {
				case "login-progress":
					eventManager.dispatch(CCEventType.AUTH_PROGRESS);
					break;
				case "login-success":
					setToken(JACKSON.treeToValue(event.payload, LoginSuccessPayload.class).getToken());
					saveToken();
					break;
				case "application-auth-code":
					eventManager.dispatch(CCEventType.GENERATED_AUTH_CODE, JACKSON.treeToValue(event.payload, ApplicationAuthCodePayload.class));
					break;
				case "application-auth-code-error":
					eventManager.dispatch(CCEventType.ERRORED_AUTH_CODE, JACKSON.treeToValue(event.payload, ApplicationAuthCodeErrorPayload.class));
					break;
				case "application-auth-code-redeemed":
					eventManager.dispatch(CCEventType.REDEEMED_AUTH_CODE, JACKSON.treeToValue(event.payload, ApplicationAuthCodeRedeemedPayload.class));
					break;
				case "subscription-result":
					SubscriptionResultPayload subscriptionPayload = JACKSON.treeToValue(event.payload, SubscriptionResultPayload.class);
					if (subscriptionPayload == null) break;
					subscriptionPayload.getSuccess().removeIf(Objects::isNull);
					subscriptionPayload.getFailure().removeIf(Objects::isNull);
					eventManager.dispatch(CCEventType.SUBSCRIBED, subscriptionPayload);
					break;
				case "effect-request":
					if (!event.domain.equals("pub")) return;
					PublicEffectPayload requestPayload = JACKSON.treeToValue(event.payload, PublicEffectPayload.class);
					if (!"game".equals(requestPayload.getEffect().getType())) return;
					eventManager.dispatch(CCEventType.EFFECT_REQUEST, requestPayload);
					break;
				case "effect-failure":
					if (!event.domain.equals("pub")) return;
					PublicEffectPayload failurePayload = JACKSON.treeToValue(event.payload, PublicEffectPayload.class);
					if (!"game".equals(failurePayload.getEffect().getType())) return;
					eventManager.dispatch(CCEventType.EFFECT_FAILURE, failurePayload);
					break;
				case "game-session-start":
					eventManager.dispatch(CCEventType.SESSION_STARTED, JACKSON.treeToValue(event.payload, GameSessionStartPayload.class));
					break;
				case "game-session-stop":
					eventManager.dispatch(CCEventType.SESSION_STOPPED, JACKSON.treeToValue(event.payload, GameSessionStopPayload.class));
					break;
				// TODO: handle effect menu sync
				// TODO: handle errors
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

	public boolean canSend() {
		return isOpen();
	}

	public boolean canSendRPC() {
		return canSend() && token != null;
	}

	// TODO: this needs to be threaded. how does http do it?
	void send(SocketRequest request) {
		String message;
		try {
			message = JACKSON.writeValueAsString(request);
		} catch (JsonProcessingException e) {
			log.warn("Failed to encode message {}", request, e);
			return;
		}

		if (!canSend()) {
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
		if (!canSendRPC()) return false;
		assert token != null;

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
		//noinspection ConstantValue
		if (response == null) return false;
		if (response.getStatus() == ResponseStatus.DELAY_ESTIMATED) return false; // unused
		eventManager.dispatch(CCEventType.EFFECT_RESPONSE, response);
		return sendRPC(new CallData<>(
			CallDataMethod.EFFECT_RESPONSE,
			Collections.singletonList(response)
		));
	}

	@Override
	public @NotNull CompletableFuture<?> regenerateAuthCode() {
		if (token != null) return CompletableFuture.completedFuture(null);
		if (pendingAuthCode != null) {
			if (pendingAuthCode.isDone()) pendingAuthCode = null;
			else return pendingAuthCode;
		}
		pendingAuthCode = new CompletableFuture<Void>().orTimeout(10, TimeUnit.SECONDS).handle((unused, throwable) -> null);
		send(new SocketRequest(GenerateAuthCodeData.ACTION, new GenerateAuthCodeData(
			parent.getAppID(),
			List.of("session:write", "session:control"),
			List.of(parent.getGamePackID()),
			false
		)));
		return pendingAuthCode;
	}

	private List<CCEffectReport> filterReports(boolean force, @NotNull CCEffectReport ... reports) {
		return Stream.of(reports).map(report -> {
				List<String> ids;
				IdentifierType idType;
				switch (report.getIdentifierType()) {
					case CATEGORY:
					case GROUP:
						GamePack gamePack = parent.getGamePack();
						if (gamePack != null) {
							Map<String, CCBaseEffectDescription> effects = gamePack.getEffects().getGame();
							if (effects != null) {
								// unpack to effect id list
								List<String> newIds = effects.entrySet().stream()
									.filter(entry -> {
										CCBaseEffectDescription effect = entry.getValue();
										List<String> values = report.getIdentifierType() == IdentifierType.CATEGORY ? effect.getCategories() : effect.getGroups();
										if (values == null) return false;
										return report.getIds().stream().anyMatch(values::contains);
									})
									.map(Map.Entry::getKey)
									.collect(Collectors.toList());
								if (!newIds.isEmpty()) {
									ids = newIds;
									idType = IdentifierType.EFFECT;
									break;
								}
							}
						}
					default:
						ids = report.getIds();
						idType = report.getIdentifierType();
				}
				int idSize = ids.size();

				ReportStatus status = report.getStatus();
				Boolean value = status == ReportStatus.MENU_AVAILABLE || status == ReportStatus.MENU_VISIBLE;
				Map<String, Boolean> map = (status == ReportStatus.MENU_AVAILABLE || status == ReportStatus.MENU_UNAVAILABLE) ? available : visible;
				ids = ids.stream().filter(id -> (map.put(idType.getValue() + ":" + id, value) != value) || force).collect(Collectors.toList());

				if (ids.size() == idSize) {
					// all this unpacking junk was for nothing, we filtered nothing
					// to save bandwidth let's just use the original packet
					return report;
				}

				// use trimmed down id list
				return new CCEffectReport(idType, status, ids);
			})
			.filter(report -> !report.getIds().isEmpty()) // filtered down to nothing
			.collect(Collectors.toList());
	}

	@Override
	public boolean sendReport(@NotNull CCEffectReport @NotNull ... reports) {
		if (!canSendRPC()) return false; // let's not update reports if we can't!!!

		List<CCEffectReport> reportList = filterReports(false, reports);
		if (reportList.isEmpty()) return true; // really filtered down to nothing! so, it succeeded, i guess?

		return sendRPC(new CallData<>(
			CallDataMethod.EFFECT_REPORT,
			reportList
		));
	}

	@Override
	public @NotNull CompletableFuture<?> startSession(@NotNull CCEffectReport @NotNull ... reports) {
		if (this.gameSessionID != null) return CompletableFuture.completedFuture(null);
		if (this.token == null) return CompletableFuture.completedFuture(null);
		return parent.getHttpUtil().apiPost("/game-session/start", GameSessionStartPayload.class, this.token, new GameSessionStartData(
			parent.getGamePackID(),
			filterReports(true, reports) // `true` updates the state without
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
			// if this is a brand-new session, let's clear old report data
			if (lastGameSessionID != null && !gameSessionID.equals(lastGameSessionID)) {
				visible.clear();
				available.clear();
				filterReports(true, reports);
			}
			lastGameSessionID = gameSessionID;
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
		String[] split = token.split(" ");
		if (split.length > 2) {
			log.warn("Invalid token length {} for {}", split.length, token);
			return false;
		}
		if (split.length == 2) {
			if (!Objects.equals(split[0], "cc-auth-token")) {
				log.warn("Unknown auth token type {} for {}", split[0], token);
				return false;
			}
			token = split[1];
		}
		try {
			this.userToken = JACKSON.readValue(TokenUtils.decodePayload(token), UserToken.class);

			if (Instant.ofEpochSecond(this.userToken.getExp()).minus(6, ChronoUnit.HOURS).isBefore(Instant.now())) {
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
			Files.writeString(tokenPath, token);
		} catch (Exception e) {
			log.warn("Failed to write user {} token", uuid, e);
		}
	}

	protected void subscribe() {
		if (this.token == null || this.userToken == null)
			return;

		Set<String> subscribeTo = new HashSet<>(Set.of(
			"pub/" + this.userToken.getId()
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

	@Override
	public @Nullable String getAuthUrl() {
		if (authCode == null) return null;
		return String.format(
			"https://auth.crowdcontrol.live/code/%s",
			authCode
		);
	}

	// True Boilerplate

	@NotNull
	public UUID getUuid() {
		return uuid;
	}

	@Nullable
	public String getAuthCode() {
		return authCode;
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
