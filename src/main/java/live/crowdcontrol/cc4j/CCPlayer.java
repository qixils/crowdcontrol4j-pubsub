package live.crowdcontrol.cc4j;

import live.crowdcontrol.cc4j.util.EventManager;
import live.crowdcontrol.cc4j.websocket.UserToken;
import live.crowdcontrol.cc4j.websocket.data.CCEffectReport;
import live.crowdcontrol.cc4j.websocket.data.CCEffectResponse;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface CCPlayer {

	/**
	 * Gets the unique ID that represents an in-game player.
	 *
	 * @return unique ID
	 */
	@NotNull
	UUID getUuid();

	/**
	 * Gets the pending auth code of this connection.
	 *
	 * @return auth code
	 */
	@Nullable
	String getAuthCode();

	/**
	 * Gets the user's token used for making authenticated requests.
	 *
	 * @return auth token
	 */
	@Nullable
	String getToken();

	/**
	 * Gets the user's profile as decoded from their auth token.
	 *
	 * @return user profile
	 */
	@Nullable
	UserToken getUserToken();

	/**
	 * Gets the URL for the player to sign in with, if available.
	 */
	@Nullable
	String getAuthUrl();

	/**
	 * Gets the ID of the player's active game session.
	 *
	 * @return gameSessionID or null
	 */
	@Nullable
	String getGameSessionId();

	/**
	 * Gets the manager which handles distributing events.
	 */
	@NotNull
	EventManager getEventManager();

	/**
	 * Sends an effect response to the WebSocket.
	 *
	 * @param response effect response
	 * @return whether the response could be sent
	 */
	boolean sendResponse(@NotNull CCEffectResponse response);

	/**
	 * Sends one or more effect reports to the WebSocket.
	 *
	 * @param reports effect reports
	 * @return whether the report could be sent
	 */
	boolean sendReport(@NotNull CCEffectReport @NotNull ... reports);

	/**
	 * Attempts to start the streamer's session.
	 * May fail if they have not yet authenticated; check {@link #getGameSessionId()} for outcome.
	 *
	 * @return future to complete when finished
	 */
	@NotNull
	CompletableFuture<?> startSession(@NotNull CCEffectReport @NotNull ... reports);

	/**
	 * Attempts to stop the streamer's session.
	 *
	 * @return future to complete when finished
	 */
	@NotNull
	CompletableFuture<?> stopSession();

	/**
	 * Regenerates the user's auth code.
	 * May fail if the user is already authenticated.
	 *
	 * @return future to complete when the auth code has regenerated
	 */
	@NotNull
	CompletableFuture<?> regenerateAuthCode();
}
