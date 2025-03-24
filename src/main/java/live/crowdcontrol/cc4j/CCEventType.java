package live.crowdcontrol.cc4j;

import io.leangen.geantyref.TypeToken;
import live.crowdcontrol.cc4j.util.CloseData;
import live.crowdcontrol.cc4j.websocket.data.CCEffectResponse;
import live.crowdcontrol.cc4j.websocket.http.GameSessionStartPayload;
import live.crowdcontrol.cc4j.websocket.http.GameSessionStopPayload;
import live.crowdcontrol.cc4j.websocket.payload.*;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

// TODO: completely refactor this to be a standalone object
public class CCEventType<T> {
	private final @NotNull String listenerId;
	private final @NotNull TypeToken<T> typeToken;

	public CCEventType(@NotNull String listenerId, @NotNull TypeToken<T> typeToken) {
		this.listenerId = listenerId;
		this.typeToken = typeToken;
	}

	public CCEventType(@NotNull String listenerId, @NotNull Class<T> clazz) {
		this(listenerId, TypeToken.get(clazz));
	}

	public static CCEventType<Void> ofVoid(@NotNull String listenerId) {
		return new CCEventType<>(listenerId, Void.class);
	}

	public @NotNull String getListenerId() {
		return listenerId;
	}

	public @NotNull TypeToken<T> getTypeToken() {
		return typeToken;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		CCEventType<?> that = (CCEventType<?>) o;
		return Objects.equals(listenerId, that.listenerId) && Objects.equals(typeToken, that.typeToken);
	}

	@Override
	public int hashCode() {
		return Objects.hash(listenerId, typeToken);
	}

	@Override
	public String toString() {
		return "CCEventType{" +
			"listenerId='" + listenerId + '\'' +
			", typeToken=" + typeToken +
			'}';
	}

	/**
	 * Called when a player's WebSocket initially establishes its connection.
	 * The connectionID will be unavailable at this point.
	 */
	public static final CCEventType<Void> CONNECTED = ofVoid("connected");

	/**
	 * Called when a player's WebSocket is closed.
	 */
	public static final CCEventType<CloseData> DISCONNECTED = new CCEventType<>("disconnected", CloseData.class);

	/**
	 * Called when an auth code is generated.
	 */
	public static final CCEventType<ApplicationAuthCodePayload> GENERATED_AUTH_CODE = new CCEventType<>("generated_auth_code", ApplicationAuthCodePayload.class);

	/**
	 * Called when an auth code is redeemed.
	 */
	public static final CCEventType<ApplicationAuthCodeRedeemedPayload> REDEEMED_AUTH_CODE = new CCEventType<>("redeemed_auth_code", ApplicationAuthCodeRedeemedPayload.class);

	/**
	 * Called when an auth code has an error.
	 */
	public static final CCEventType<ApplicationAuthCodeErrorPayload> ERRORED_AUTH_CODE = new CCEventType<>("errored_auth_code", ApplicationAuthCodeErrorPayload.class);

	/**
	 * Called when a player's WebSocket becomes authenticated.
	 * The connectionID may be unavailable at this point.
	 */
	public static final CCEventType<Void> AUTHENTICATED = ofVoid("authenticated");

	/**
	 * Called when a player's authentication token expires.
	 * This will happen 24 hours after creation.
	 */
	public static final CCEventType<Void> AUTH_EXPIRED = ofVoid("auth_expired");

	/**
	 * Called when a player's auth token is removed for any reason.
	 * Shares overlap with {@link #AUTH_EXPIRED}.
	 */
	public static final CCEventType<Void> UNAUTHENTICATED = ofVoid("unauthenticated");

	/**
	 * Called when an {@code effect-request} comes in from the player.
	 * Note that the traditional way to receive this information is via {@link CCEffect#onTrigger(PublicEffectPayload, CCPlayer)}.
	 */
	public static final CCEventType<PublicEffectPayload> EFFECT_REQUEST = new CCEventType<>("effect_request", PublicEffectPayload.class);

	public static final CCEventType<PublicEffectPayload> EFFECT_FAILURE = new CCEventType<>("effect_failure", PublicEffectPayload.class);

	/**
	 * Called when a connection has attempted to subscribe to some topics.
	 */
	public static final CCEventType<SubscriptionResultPayload> SUBSCRIBED = new CCEventType<>("subscribed", SubscriptionResultPayload.class);

	/**
	 * Called when {@link CCPlayer#sendResponse(CCEffectResponse)} has been called.
	 */
	public static final CCEventType<CCEffectResponse> EFFECT_RESPONSE = new CCEventType<>("effect_result", CCEffectResponse.class);

	/**
	 * Called when a session has started.
	 */
	public static final CCEventType<GameSessionStartPayload> SESSION_STARTED = new CCEventType<>("game_session_start", GameSessionStartPayload.class);

	/**
	 * Called when a session has stopped.
	 */
	public static final CCEventType<GameSessionStopPayload> SESSION_STOPPED = new CCEventType<>("game_session_stop", GameSessionStopPayload.class);
}
