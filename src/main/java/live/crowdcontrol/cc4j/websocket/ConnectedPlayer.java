package live.crowdcontrol.cc4j.websocket;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import live.crowdcontrol.cc4j.*;
import live.crowdcontrol.cc4j.util.CloseData;
import live.crowdcontrol.cc4j.util.EventManager;
import live.crowdcontrol.cc4j.util.GsonAdapters;
import live.crowdcontrol.cc4j.util.TokenUtils;
import live.crowdcontrol.cc4j.websocket.data.*;
import live.crowdcontrol.cc4j.websocket.http.*;
import live.crowdcontrol.cc4j.websocket.payload.*;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft_6455;
import org.java_websocket.framing.CloseFrame;
import org.java_websocket.framing.Framedata;
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
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * A player connected to the server.
 */
@ApiStatus.Internal
public class ConnectedPlayer implements CCPlayer {
	public static final @NotNull Gson GSON;
	protected static final @NotNull Logger log = LoggerFactory.getLogger("CrowdControl/ConnectedPlayer");
	protected final ReentrantLock lock = new ReentrantLock();
	protected final @NotNull Set<String> subscriptions = new HashSet<>();
	protected final Map<String, Boolean> visible = new HashMap<>();
	protected final Map<String, Boolean> available = new HashMap<>();
	protected final @NotNull EventManager eventManager;
	protected final @NotNull UUID uuid;
	protected final @NotNull Path tokenPath;
	protected final @NotNull CrowdControl parent;
	protected @Nullable WebSocketClient ws;
	protected @Nullable String authCode;
	protected @Nullable String pkceVerifier;
	protected @Nullable String token;
	protected @Nullable UserToken userToken;
	protected @Nullable String gameSessionID;
	protected @Nullable String lastGameSessionID;
	protected @Nullable CompletableFuture<Void> pendingAuthCode;
	protected int sleep = 1;
	protected long disconnectTriggeredAt = 0L;
	protected ScheduledFuture<?> keepAlive = null;
	protected ScheduledFuture<?> timeout = null;

	static {
		GSON = new GsonBuilder()
			.disableHtmlEscaping()
			.registerTypeAdapter(CCName.class, new CCName.CCNameAdapter())
			.registerTypeAdapter(CustomEffectDuration.class, new CustomEffectDuration.CustomEffectDurationAdapter())
			.registerTypeAdapter(IdentifierType.class, GsonAdapters.stringValued(IdentifierType::getValue, IdentifierType::fromValue))
			.registerTypeAdapter(ReportStatus.class, GsonAdapters.stringValued(ReportStatus::getValue, ReportStatus::fromValue))
			.registerTypeAdapter(ResponseStatus.class, GsonAdapters.stringValued(ResponseStatus::getValue, ResponseStatus::fromValue))
			.registerTypeAdapter(ProfileType.class, GsonAdapters.stringValued(ProfileType::getValue, ProfileType::fromValue))
			.registerTypeHierarchyAdapter(CallDataMethod.class, GsonAdapters.<CallDataMethod<?>>stringValued(CallDataMethod::getValue, CallDataMethod::new))
			.registerTypeAdapterFactory(GsonAdapters.rebuilding(PublicEffectPayload.class, p -> new PublicEffectPayload(
				p.getRequestId(), p.getTimestamp(), p.getEffect(), p.getTarget(), p.getOrigin(), p.getRequester(), p.isAnonymous(), p.getQuantity()
			)))
			.registerTypeAdapterFactory(GsonAdapters.rebuilding(GameSessionStartPayload.class, p -> new GameSessionStartPayload(
				p.getGameSessionId(), p.getCreatedAt()
			)))
			.registerTypeAdapterFactory(GsonAdapters.rebuilding(SubscriptionResultPayload.class, p -> new SubscriptionResultPayload(
				p.getSuccess() == null ? Collections.emptySet() : p.getSuccess(),
				p.getFailure() == null ? Collections.emptySet() : p.getFailure()
			)))
			.create();
	}

	// WebSocket Impl

	public ConnectedPlayer(@NotNull UUID uuid, @NotNull CrowdControl parent) {
		this.parent = parent;
		this.uuid = uuid;
		this.tokenPath = parent.getDataFolder().resolve(uuid + ".token");
		this.eventManager = new EventManager(parent);

		this.eventManager.registerEventConsumer(CCEventType.CONNECTED, handshake -> {
//			log.info("Connected event");
			// Sleep for a bit to workaround issue where socket appears to still be opening
			long wait = 1L;
			while (!canSend() && wait <= 5) {
				long sleepFor = wait * 500L;
//				log.info("Waiting {}ms for socket to finish opening...", sleepFor);
				try {
					Thread.sleep(sleepFor);
				} catch (InterruptedException ignored) {
				} finally {
					wait *= 2;
				}
			}
			if (!canSend()) {
				log.warn("Socket is not opening, session start may fail");
			}
			subscribe(); // since token load may have triggered already
			regenerateAuthCode();
			resetKeepAlive();
		});
		this.eventManager.registerEventConsumer(CCEventType.DISCONNECTED, data -> {
			clearKeepAlive();
			if (timeout != null) {
				timeout.cancel(false);
				timeout = null;
			}
			this.ws = null;
			this.subscriptions.clear();
			// check that the player hasn't just left
			if (this.parent.getPlayer(uuid) != this) return;
			this.eventManager.dispatch(CCEventType.MESSAGE, new CCMessage(CCMessage.Level.WARN, "Reconnecting to socket in " + sleep + " second(s)"));
			try {
				Thread.sleep(sleep * 1000L);
			} catch (InterruptedException ignored) {
			}
			sleep *= 2;
			connect(); // reconnect!
		});
		this.eventManager.registerEventConsumer(CCEventType.GENERATED_AUTH_CODE, payload -> {
			// we're successfully connected!
			onConnectionSuccessful();
			this.authCode = payload.code();
			if (pendingAuthCode != null) {
				pendingAuthCode.complete(null);
				pendingAuthCode = null;
			}
		});
		this.eventManager.registerEventConsumer(CCEventType.REDEEMED_AUTH_CODE, payload -> {
			AuthApplicationTokenData tokenData;
			if (parent.isPublicClient()) {
				String verifier = this.pkceVerifier;
				if (verifier == null) {
					log.warn("Auth code was redeemed but no PKCE verifier is held; requesting a new code");
					this.eventManager.dispatch(CCEventType.MESSAGE, new CCMessage(CCMessage.Level.ERROR, "Failed to authenticate account"));
					send(new SocketRequest(GenerateAuthCodeData.ACTION, createAuthCodeData(null, null, null)));
					return;
				}
				tokenData = AuthApplicationTokenData.forPkce(parent.getAppID(), payload.code(), verifier);
			} else {
				String appSecret = parent.getAppSecret();
				if (appSecret == null) {
					log.warn("Auth code was redeemed but for an authenticated client; requesting a new code");
					this.eventManager.dispatch(CCEventType.MESSAGE, new CCMessage(CCMessage.Level.ERROR, "Failed to authenticate account"));
					send(new SocketRequest(GenerateAuthCodeData.ACTION, createAuthCodeData(null, null, null)));
					return;
				}
				tokenData = new AuthApplicationTokenData(parent.getAppID(), payload.code(), appSecret);
			}
			parent.getHttpUtil().apiPost(
				"/auth/application/token",
				AuthApplicationTokenPayload.class,
				null,
				tokenData
			).handle((tokenPayload, e) -> {
				if (e != null) {
					log.warn("Failed to query URL", e);
					this.eventManager.dispatch(CCEventType.MESSAGE, new CCMessage(CCMessage.Level.ERROR, "Failed to authenticate account"));
					return null;
				}
				//noinspection ConstantValue
				if (tokenPayload == null || tokenPayload.token() == null) {
					log.warn("Failed to read token");
					this.eventManager.dispatch(CCEventType.MESSAGE, new CCMessage(CCMessage.Level.ERROR, "Failed to verify account"));
					return null;
				}
				authCode = null;
				setToken(tokenPayload.token());
				saveToken();
				return null;
			});
		});
		this.eventManager.registerEventConsumer(CCEventType.ERRORED_AUTH_CODE, payload -> {
			log.warn("Failed to redeem auth code for reason {}, generating new one", payload.message());
			this.eventManager.dispatch(CCEventType.MESSAGE, new CCMessage(CCMessage.Level.ERROR, "Failed to redeem auth code"));
			send(new SocketRequest(GenerateAuthCodeData.ACTION, createAuthCodeData(null, null, null)));
		});
		this.eventManager.registerEventRunnable(CCEventType.AUTHENTICATED, this::subscribe);
		this.eventManager.registerEventConsumer(CCEventType.SUBSCRIBED, payload -> {
			// we're successfully connected!
			onConnectionSuccessful();

			assert this.userToken != null : "Subscribed before authenticating";
			this.subscriptions.addAll(payload.getSuccess());
		});
		this.eventManager.registerEventConsumer(CCEventType.EFFECT_REQUEST, payload -> this.parent.executeEffect(payload, this));
		this.eventManager.registerEventConsumer(CCEventType.EFFECT_FAILURE, payload -> parent.cancelByRequestId(payload.getRequestId()));

		loadToken();
	}

	private void onConnectionSuccessful() {
		// we're successfully connected! reset attempt count...
		sleep = 1;
		// ...stop initial timeout...
		if (timeout != null) {
			timeout.cancel(false);
			timeout = null;
		}
		// ... and start keepalive
		resetKeepAlive();
	}

	private void emitDisconnect(CloseData data) {
		long now = System.currentTimeMillis();
		if ((now - disconnectTriggeredAt) <= 900L) return;
		disconnectTriggeredAt = now;
		this.eventManager.dispatch(CCEventType.DISCONNECTED, data);
	}

	public void connect() {
		log.info("Connecting WebSocket");

		timeout = parent.getTimedEffectPool().schedule(() -> {
			this.eventManager.dispatch(CCEventType.MESSAGE, new CCMessage(CCMessage.Level.WARN, "Timed out connecting to socket"));
			close();
		}, 60, TimeUnit.SECONDS);

		Client client = new Client();
		this.ws = client;
		client.connect();
	}

	public CompletableFuture<?> close() {
		WebSocketClient ws = this.ws;
		if (ws == null) return CompletableFuture.completedFuture(null);
		if (!ws.isOpen()) {
			// abort a pending connection attempt (i.e. a stalled opening handshake); this triggers onClose
			if (!ws.isClosed()) ws.closeConnection(CloseFrame.NEVER_CONNECTED, "Connection attempt aborted");
			return CompletableFuture.completedFuture(null);
		}
		return CompletableFuture.runAsync(() -> ws.close(CloseFrame.NORMAL))
			.whenComplete(($1, $2) -> emitDisconnect(new CloseData(CloseFrame.NORMAL, null, false)));
	}

	public void clearKeepAlive() {
		if (keepAlive == null) return;
		keepAlive.cancel(false);
		keepAlive = null;
	}

	public void resetKeepAlive() {
		if (!canSend()) return;
		assert ws != null;
		clearKeepAlive();

		// we don't need to start keeping alive until the initial timeout is finished
		if (timeout != null) return;

		// wait a bit before sending another ping
		keepAlive = parent.getTimedEffectPool().schedule(() -> {
			// prepare the keepalive kill task
			keepAlive = parent.getTimedEffectPool().schedule(() -> {
				log.info("KeepAlive failed, reconnecting");
				clearKeepAlive();
				close();
			}, 15, TimeUnit.SECONDS);

			WebSocketClient ws = this.ws;
			if (ws != null && ws.isOpen()) ws.sendPing();
		}, 15, TimeUnit.SECONDS);
	}

	protected void onPong() {
//		log.info("pong");
		resetKeepAlive();
	}

	protected void onOpen() {
//		log.info("Emitting connected event");
		eventManager.dispatch(CCEventType.CONNECTED);
	}

	protected void onText(String message) {
		try {
//			log.info("Received message {}", message);
			SocketEvent event = GSON.fromJson(message, SocketEvent.class);
			switch (event.type) {
				case "application-auth-code":
					eventManager.dispatch(CCEventType.GENERATED_AUTH_CODE, GSON.fromJson(event.payload, ApplicationAuthCodePayload.class));
					break;
				case "application-auth-code-error":
					eventManager.dispatch(CCEventType.ERRORED_AUTH_CODE, GSON.fromJson(event.payload, ApplicationAuthCodeErrorPayload.class));
					break;
				case "application-auth-code-redeemed":
					eventManager.dispatch(CCEventType.REDEEMED_AUTH_CODE, GSON.fromJson(event.payload, ApplicationAuthCodeRedeemedPayload.class));
					break;
				case "subscription-result":
					SubscriptionResultPayload subscriptionPayload = GSON.fromJson(event.payload, SubscriptionResultPayload.class);
					if (subscriptionPayload == null) break;
					//noinspection ConstantValue
					subscriptionPayload = new SubscriptionResultPayload(
						subscriptionPayload.getSuccess().stream().filter(Objects::nonNull).collect(Collectors.toSet()),
						subscriptionPayload.getFailure().stream().filter(Objects::nonNull).collect(Collectors.toSet())
					);
					eventManager.dispatch(CCEventType.SUBSCRIBED, subscriptionPayload);
					break;
				case "effect-request":
					if (!event.domain.equals("pub")) return;
					PublicEffectPayload requestPayload = GSON.fromJson(event.payload, PublicEffectPayload.class);
					if (!"game".equals(requestPayload.getEffect().getType())) return;
					eventManager.dispatch(CCEventType.EFFECT_REQUEST, requestPayload);
					break;
				case "effect-failure":
					if (!event.domain.equals("pub")) return;
					PublicEffectPayload failurePayload = GSON.fromJson(event.payload, PublicEffectPayload.class);
					if (!"game".equals(failurePayload.getEffect().getType())) return;
					eventManager.dispatch(CCEventType.EFFECT_FAILURE, failurePayload);
					break;
				case "game-session-start":
//					eventManager.dispatch(CCEventType.SESSION_STARTED, GSON.fromJson(event.payload, GameSessionStartPayload.class));
					break;
				case "game-session-stop":
//					eventManager.dispatch(CCEventType.SESSION_STOPPED, GSON.fromJson(event.payload, GameSessionStopPayload.class));
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

	protected void onClose(int statusCode, String reason, boolean remote) {
		log.warn("Close initiated {} {}", statusCode, reason);
		emitDisconnect(new CloseData(statusCode, reason, remote));
	}

	protected void onError(Throwable error) {
		log.error("An unknown WebSocket error has occurred", error);
		this.eventManager.dispatch(CCEventType.MESSAGE, new CCMessage(CCMessage.Level.WARN, "An unknown socket error occurred"));
	}

	// Semi Boilerplate

	public boolean canSend() {
		return ws != null && ws.isOpen();
	}

	public boolean canSendRPC() {
		return canSend() && token != null;
	}

	@NotNull
	private CompletableFuture<String> send(SocketRequest request) {
		return CompletableFuture.supplyAsync(() -> {
			String message;
			try {
				message = GSON.toJson(request);
			} catch (JsonIOException e) {
				throw new IllegalArgumentException("Could not encode message", e);
			}

			lock.lock();
			try {
				if (!canSend()) {
					throw new IllegalStateException("Attempted to send message before connecting " + message.substring(0, 20));
				}

				assert ws != null;

				try {
					ws.send(message);
				} catch (Exception e) {
					throw new IllegalStateException("WebSocket failed to send message " + message.substring(0, 20), e);
				}
//				log.info("Sent WS message {}", message);
				return message;
			} finally {
				lock.unlock();
			}
		}).whenComplete((message, e) -> {
			if (e != null) log.warn("Failed to send message", e);
//			else log.info("Sent message {}", message);
		});
	}

	public CompletableFuture<Boolean> sendRPC(CallData<?> call) {
		if (!canSendRPC()) return CompletableFuture.completedFuture(false);
		assert token != null;

		return send(new SocketRequest(
			"rpc",
			new RemoteProcedureCallData(
				token,
				call
			)
		)).handle(($, e) -> e == null);
	}

	@Override
	public @NotNull CompletableFuture<Boolean> sendResponse(@NotNull CCEffectResponse response) {
		//noinspection ConstantValue
		if (response == null) return CompletableFuture.completedFuture(false);
		if (response.getStatus() == ResponseStatus.DELAY_ESTIMATED) return CompletableFuture.completedFuture(false); // unused
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
		CompletableFuture<Void> authCodeFuture = new CompletableFuture<>();
		parent.getTimedEffectPool().schedule(() -> authCodeFuture.complete(null), 10, TimeUnit.SECONDS);
		pendingAuthCode = authCodeFuture;
		send(new SocketRequest(GenerateAuthCodeData.ACTION, createAuthCodeData(
			Arrays.asList("profile:read", "session:write", "session:control", "custom-effects:write"),
			Collections.singletonList(parent.getGamePackID()),
			false
		)));
		return pendingAuthCode;
	}

	/**
	 * Builds a {@link GenerateAuthCodeData}, attaching a fresh PKCE S256 code
	 * challenge when the parent is a public (secret-less) client. The matching
	 * {@code code_verifier} is held until the code is redeemed and then used
	 * in place of the app secret on the token exchange (RFC 7636).
	 */
	protected @NotNull GenerateAuthCodeData createAuthCodeData(@Nullable List<String> scopes, @Nullable List<String> packs, @Nullable Boolean qrCode) {
		if (!parent.isPublicClient()) {
			return new GenerateAuthCodeData(parent.getAppID(), scopes, packs, qrCode);
		}
		byte[] randomBytes = new byte[32];
		new SecureRandom().nextBytes(randomBytes);
		String verifier = Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
		String challenge;
		try {
			byte[] digest = MessageDigest.getInstance("SHA-256").digest(verifier.getBytes(StandardCharsets.US_ASCII));
			challenge = Base64.getUrlEncoder().withoutPadding().encodeToString(digest);
		} catch (NoSuchAlgorithmException e) {
			throw new IllegalStateException("JVM does not provide SHA-256", e);
		}
		this.pkceVerifier = verifier;
		return new GenerateAuthCodeData(parent.getAppID(), scopes, packs, qrCode, challenge, "S256");
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
	public @NotNull CompletableFuture<Boolean> sendReport(@NotNull CCEffectReport @NotNull ... reports) {
		if (!canSendRPC()) return CompletableFuture.completedFuture(false); // let's not update reports if we can't!!!

		List<CCEffectReport> reportList = filterReports(false, reports);
		if (reportList.isEmpty()) return CompletableFuture.completedFuture(false); // really filtered down to nothing! so, it succeeded, i guess?

		return sendRPC(new CallData<>(
			CallDataMethod.EFFECT_REPORT,
			reportList
		));
	}

	@Override
	public @NotNull CompletableFuture<Boolean> sendGameEvent(@NotNull CCGameEvent @NotNull ... events) {
		if (!canSendRPC()) return CompletableFuture.completedFuture(false);
		if (events.length == 0) return CompletableFuture.completedFuture(false);

		List<CCGameEvent> list = Collections.unmodifiableList(Arrays.asList(events.clone()));
		return sendRPC(new CallData<>(
			CallDataMethod.GAME_EVENT,
			list
		));
	}

	@Override
	public @NotNull CompletableFuture<?> setCustomEffects(@NotNull List<CustomEffectsOperation> operations) {
		if (this.token == null) return CompletableFuture.completedFuture(null);
		if (this.userToken == null) return CompletableFuture.completedFuture(null);
		if (this.userToken.getApp() == null) return CompletableFuture.completedFuture(null);
		if (!this.userToken.getApp().scopes().contains("custom-effects:write")) return CompletableFuture.completedFuture(null);
		if (operations.isEmpty()) return CompletableFuture.completedFuture(null);

		return parent.getHttpUtil().apiPut("/menu/custom-effects", this.token, new PutCustomEffectsData(
			parent.getGamePackID(),
			operations
		)).handle((payload, e) -> {
			if (e != null) {
				log.warn("Failed to put custom effects", e);
				return null;
			}
			return null;
		});
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
			eventManager.dispatch(CCEventType.SESSION_STARTED, payload);
			return null;
		});
	}

	@Override
	public @NotNull CompletableFuture<?> stopSession() {
		// clean up in case someone switches to an older mod version ig?
		setCustomEffects(Collections.singletonList(new CustomEffectsOperation("replace-all", Collections.emptyMap())));

		if (this.gameSessionID == null) return CompletableFuture.completedFuture(null);
		if (this.token == null) return CompletableFuture.completedFuture(null);
		return parent.getHttpUtil().apiPost("/game-session/stop", GameSessionStopPayload.class, this.token, new GameSessionStopData(this.gameSessionID)).handle((payload, e) -> {
			if (e != null) {
				log.warn("Failed to query URL", e);
				return null;
			}
			this.gameSessionID = null;
			eventManager.dispatch(CCEventType.SESSION_STOPPED, payload);
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
			this.userToken = GSON.fromJson(TokenUtils.decodePayload(token), UserToken.class);

			if (Instant.ofEpochSecond(this.userToken.getExp()).minus(6, ChronoUnit.HOURS).isBefore(Instant.now())) {
				log.warn("User {}'s auth token has expired", uuid);
				this.userToken = null;
				eventManager.dispatch(CCEventType.AUTH_EXPIRED);
				eventManager.dispatch(CCEventType.UNAUTHENTICATED);
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
		try {
			if (!Files.exists(tokenPath))
				return false;

			try (BufferedReader reader = Files.newBufferedReader(tokenPath)) {
				return setToken(reader.readLine());
			}
		} catch (Exception e) {
			log.warn("Failed to read user {} token", uuid, e);
			return false;
		}
	}

	protected void saveToken() {
		try {
			if (this.token == null) {
				Files.deleteIfExists(tokenPath);
			} else {
				Files.write(tokenPath, token.getBytes(StandardCharsets.UTF_8));
			}
		} catch (Exception e) {
			log.warn("Failed to write user {} token", uuid, e);
		}
	}

	protected void subscribe() {
		if (!canSendRPC())
			return;

		assert this.userToken != null;

		Set<String> subscribeTo = new HashSet<>(Collections.singleton(
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
			"%s/code/%s?showAllPlatforms=true",
			ServerURLs.AUTH,
			authCode
		);
	}

	@Override
	public void clearToken() {
		this.token = null;
		this.userToken = null;
		saveToken();
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

	/**
	 * WebSocket client which forwards events to its parent {@link ConnectedPlayer}.
	 */
	protected class Client extends WebSocketClient {
		public Client() {
			super(URI.create(ServerURLs.PUBSUB), new Draft_6455(), Collections.singletonMap("User-Agent", "crowdcontrol4j"), 55000);
		}

		@Override
		public void onOpen(ServerHandshake handshake) {
			log.info("Established initial connection");
			ConnectedPlayer.this.onOpen();
		}

		@Override
		public void onMessage(String message) {
			onText(message);
		}

		@Override
		public void onWebsocketPong(org.java_websocket.WebSocket conn, Framedata f) {
			onPong();
		}

		@Override
		public void onClose(int code, String reason, boolean remote) {
			ConnectedPlayer.this.onClose(code, reason, remote);
		}

		@Override
		public void onError(Exception ex) {
			ConnectedPlayer.this.onError(ex);
		}
	}
}
