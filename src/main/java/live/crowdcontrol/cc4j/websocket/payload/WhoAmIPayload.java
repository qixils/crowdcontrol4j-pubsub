package live.crowdcontrol.cc4j.websocket.payload;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.jetbrains.annotations.NotNull;

/**
 * Denotes the information returned by the server when asking {@code whoami}.
 */
public class WhoAmIPayload {
	private final @NotNull String connectionID;

	public WhoAmIPayload(@JsonProperty("connectionID") @NotNull String connectionId) {
		this.connectionID = connectionId;
	}

	/**
	 * Gets the ID of this connection.
	 *
	 * @return connectionID
	 */
	public @NotNull String getConnectionId() {
		return connectionID;
	}
}
