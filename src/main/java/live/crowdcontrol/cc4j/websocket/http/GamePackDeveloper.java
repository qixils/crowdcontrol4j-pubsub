package live.crowdcontrol.cc4j.websocket.http;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class GamePackDeveloper {
	private final @NotNull String name;
	private final @Nullable String url;

	public GamePackDeveloper(@JsonProperty("name") @NotNull String name,
							 @JsonProperty("url") @Nullable String url) {
		this.name = name;
		this.url = url;
	}

	public @NotNull String getName() {
		return name;
	}

	public @Nullable String getUrl() {
		return url;
	}
}
