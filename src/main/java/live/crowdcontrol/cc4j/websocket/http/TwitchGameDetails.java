package live.crowdcontrol.cc4j.websocket.http;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.jetbrains.annotations.NotNull;

public class TwitchGameDetails {
	private final @NotNull String id;

	@JsonCreator
	public TwitchGameDetails(@JsonProperty("id") @NotNull String id) {
		this.id = id;
	}

	public @NotNull String getId() {
		return id;
	}
}
