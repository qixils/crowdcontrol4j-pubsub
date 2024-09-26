package live.crowdcontrol.cc4j.websocket.http;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.jetbrains.annotations.NotNull;

public class GamePackFramework {
	private final @NotNull String id;
	private final @NotNull String name;

	@JsonCreator
	public GamePackFramework(@JsonProperty("id") @NotNull String id,
							 @JsonProperty("name") @NotNull String name) {
		this.id = id;
		this.name = name;
	}

	public @NotNull String getId() {
		return id;
	}

	public @NotNull String getName() {
		return name;
	}
}
