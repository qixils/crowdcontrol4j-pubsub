package live.crowdcontrol.cc4j.websocket.http;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Instant;

/**
 * Data returned by the server when trying to start a session.
 */
public class GameSessionStartPayload {
	private final @NotNull String gameSessionID;
	private final @NotNull String createdAt;

	/**
	 * Creates data.
	 *
	 * @param gameSessionID ID of the game session
	 * @param createdAt when the session was started
	 */
	@JsonCreator
	public GameSessionStartPayload(@JsonProperty("gameSessionID") @NotNull String gameSessionID,
								@JsonProperty("createdAt") @Nullable String createdAt) {
		this.gameSessionID = gameSessionID;
		this.createdAt = createdAt == null ? Instant.now().toString() : createdAt;
	}

	/**
	 * Creates data.
	 *
	 * @param gameSessionID ID of the game session
	 */
	public GameSessionStartPayload(@NotNull String gameSessionID) {
		this(gameSessionID, null);
	}

	/**
	 * Gets the ID of the game session.
	 *
	 * @return gameSessionID
	 */
	public @NotNull String getGameSessionId() {
		return gameSessionID;
	}

	/**
	 * Gets the time the session started at.
	 *
	 * @return createdAt
	 */
	public @NotNull String getCreatedAt() {
		return createdAt;
	}
}
