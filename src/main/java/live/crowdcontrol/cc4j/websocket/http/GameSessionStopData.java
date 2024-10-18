package live.crowdcontrol.cc4j.websocket.http;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.jetbrains.annotations.Nullable;

/**
 * Data sent to the server when trying to stop a session.
 */
public class GameSessionStopData {
	private final @Nullable String gameSessionID;

	/**
	 * Creates data.
	 *
	 * @param gameSessionID ID of the game session
	 */
	@JsonCreator
	public GameSessionStopData(@JsonProperty("gameSessionID") @Nullable String gameSessionID) {
		this.gameSessionID = gameSessionID;
	}

	/**
	 * Gets the ID of the game session.
	 *
	 * @return gameSessionID
	 */
	@JsonProperty("gameSessionID")
	public @Nullable String getGameSessionId() {
		return gameSessionID;
	}
}
