package live.crowdcontrol.cc4j;

import io.leangen.geantyref.TypeToken;
import live.crowdcontrol.cc4j.util.CloseData;
import live.crowdcontrol.cc4j.websocket.data.CCEffectResponse;
import live.crowdcontrol.cc4j.websocket.http.GameSessionStartPayload;
import live.crowdcontrol.cc4j.websocket.http.GameSessionStop;
import live.crowdcontrol.cc4j.websocket.payload.PublicEffectPayload;
import live.crowdcontrol.cc4j.websocket.payload.SubscriptionResultPayload;
import live.crowdcontrol.cc4j.websocket.payload.WhoAmIPayload;
import org.java_websocket.handshake.ServerHandshake;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

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

	/**
	 * Called when a player's WebSocket initially establishes its connection.
	 * The connectionID will be unavailable at this point.
	 */
	public static final CCEventType<ServerHandshake> CONNECTED = new CCEventType<>("connected", ServerHandshake.class);

	/**
	 * Called when a player's WebSocket is closed.
	 */
	public static final CCEventType<CloseData> DISCONNECTED = new CCEventType<>("disconnected", CloseData.class);

	/**
	 * Called when a player's WebSocket connectionID becomes known.
	 */
	public static final CCEventType<WhoAmIPayload> IDENTIFIED = new CCEventType<>("identified", WhoAmIPayload.class);

	/**
	 * Called when a player's WebSocket becomes authenticated.
	 * The connectionID may be unavailable at this point.
	 */
	public static final CCEventType<Void> AUTHENTICATED = ofVoid("authenticated");

	/**
	 * Called when a player's authentication token expires.
	 * This may happen if they have not re-authenticated in about 6 months.
	 */
	public static final CCEventType<Void> AUTH_EXPIRED = ofVoid("auth_expired");

	/**
	 * Called when a player's auth token is removed for any reason.
	 * Shares overlap with {@link #AUTH_EXPIRED}.
	 */
	public static final CCEventType<Void> UNAUTHENTICATED = ofVoid("unauthenticated");

	/**
	 * Called when an {@code effect-request} comes in from the player.
	 * This event filters out duplicates if both the {@code pub} and {@code prv} domains are available.
	 * Note that the traditional way to receive this information is via {@link CCEffect#onTrigger(PublicEffectPayload, CCPlayer)}.
	 */
	public static final CCEventType<PublicEffectPayload> EFFECT_REQUEST = new CCEventType<>("effect_request", PublicEffectPayload.class);

	/**
	 * Called when an {@code effect-request} on the {@code pub} domain comes in from the player.
	 * To get notified of only one {@code effect-request} ({@code prv} if available, else {@code pub}), see #EFFECT_REQUEST.
	 * Note that the traditional way to receive this information is via {@link CCEffect#onTrigger(PublicEffectPayload, CCPlayer)}.
	 */
	public static final CCEventType<PublicEffectPayload> PUB_EFFECT_REQUEST = new CCEventType<>("pub_effect_request", PublicEffectPayload.class);

	/**
	 * Called when an {@code effect-request} on the {@code prv} domain comes in from the player.
	 * To get notified of only one {@code effect-request} ({@code prv} if available, else {@code pub}), see #EFFECT_REQUEST.
	 * Note that the traditional way to receive this information is via {@link CCEffect#onTrigger(PublicEffectPayload, CCPlayer)}.
	 */
	public static final CCEventType<PublicEffectPayload> PRV_EFFECT_REQUEST = new CCEventType<>("prv_effect_request", PublicEffectPayload.class);

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
	public static final CCEventType<GameSessionStop> SESSION_STOPPED = new CCEventType<>("game_session_stop", GameSessionStop.class);
}
