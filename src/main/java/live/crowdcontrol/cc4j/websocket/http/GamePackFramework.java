package live.crowdcontrol.cc4j.websocket.http;

import org.jetbrains.annotations.NotNull;

public class GamePackFramework {
	private final @NotNull String id;
	private final @NotNull String name;

	public GamePackFramework(@NotNull String id,
							 @NotNull String name) {
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
