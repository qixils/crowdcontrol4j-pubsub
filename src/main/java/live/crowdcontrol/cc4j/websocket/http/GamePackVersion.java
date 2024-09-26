package live.crowdcontrol.cc4j.websocket.http;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.jetbrains.annotations.NotNull;

public class GamePackVersion {
	private final @NotNull String id;
	private final int rev;

	public GamePackVersion(@JsonProperty("id") @NotNull String id,
						   @JsonProperty("rev") int rev) {
		this.id = id;
		this.rev = rev;
	}

	public @NotNull String getId() {
		return id;
	}

	public int getRev() {
		return rev;
	}
}
