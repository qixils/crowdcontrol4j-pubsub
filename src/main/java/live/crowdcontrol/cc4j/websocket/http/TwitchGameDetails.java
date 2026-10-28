package live.crowdcontrol.cc4j.websocket.http;

import org.jetbrains.annotations.NotNull;

public class TwitchGameDetails {
	private final @NotNull String id;

	public TwitchGameDetails(@NotNull String id) {
		this.id = id;
	}

	public @NotNull String getId() {
		return id;
	}
}
