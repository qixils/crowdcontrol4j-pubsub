package live.crowdcontrol.cc4j.websocket.http;

import org.jetbrains.annotations.NotNull;

public class GameClip {
	private final @NotNull String name;
	private final @NotNull String platform;
	private final @NotNull String url;

	public GameClip(@NotNull String name, @NotNull String platform, @NotNull String url) {
		this.name = name;
		this.platform = platform;
		this.url = url;
	}

	public @NotNull String getName() {
		return name;
	}

	public @NotNull String getPlatform() {
		return platform;
	}

	public @NotNull String getUrl() {
		return url;
	}
}
