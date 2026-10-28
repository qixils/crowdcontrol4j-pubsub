package live.crowdcontrol.cc4j.websocket.http;

import org.jetbrains.annotations.NotNull;

/**
 * Data returned by the server when trying to stop a session.
 */
public class GameSessionStopPayload {
	private final @NotNull String gameSessionID;

	/**
	 * Creates data.
	 *
	 * @param gameSessionID ID of the game session
	 */
	public GameSessionStopPayload(@NotNull String gameSessionID) {
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
