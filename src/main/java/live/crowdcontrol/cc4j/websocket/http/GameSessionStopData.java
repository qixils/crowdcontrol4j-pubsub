package live.crowdcontrol.cc4j.websocket.http;

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
	public GameSessionStopData(@Nullable String gameSessionID) {
		this.gameSessionID = gameSessionID;
	}

	/**
	 * Gets the ID of the game session.
	 *
	 * @return gameSessionID
	 */
	public @Nullable String getGameSessionId() {
		return gameSessionID;
	}
}
