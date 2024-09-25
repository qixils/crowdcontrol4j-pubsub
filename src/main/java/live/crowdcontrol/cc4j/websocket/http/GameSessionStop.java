package live.crowdcontrol.cc4j.websocket.http;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.jetbrains.annotations.NotNull;

/**
 * Data sent to or returned by the server when trying to stop a session.
 */
public class GameSessionStop {
	private final @NotNull String gameSessionID;

	/**
	 * Creates data.
	 *
	 * @param gameSessionID ID of the game session
	 */
	@JsonCreator
	public GameSessionStop(@JsonProperty("gameSessionID") @NotNull String gameSessionID) {
		this.gameSessionID = gameSessionID;
	}

	/**
	 * Gets the ID of the game session.
	 *
	 * @return gameSessionID
	 */
	public @NotNull String getGameSessionId() {
		return gameSessionID;
	}
}