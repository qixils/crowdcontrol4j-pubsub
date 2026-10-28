package live.crowdcontrol.cc4j.websocket.http;

import org.jetbrains.annotations.NotNull;

/**
 * Describes a Crowd Control Game Pack.
 */
public class GamePack {
	private final @NotNull String gamePackID;
	private final @NotNull GamePayload game;
	private final @NotNull GamePackMeta meta;
	private final @NotNull GamePackEffects effects;

	public GamePack(@NotNull String gamePackID,
					@NotNull GamePayload game,
					@NotNull GamePackMeta meta,
					@NotNull GamePackEffects effects) {
		this.gamePackID = gamePackID;
		this.game = game;
		this.meta = meta;
		this.effects = effects;
	}

	public @NotNull String getGamePackId() {
		return gamePackID;
	}

	public @NotNull GamePayload getGame() {
		return game;
	}

	public @NotNull GamePackMeta getMeta() {
		return meta;
	}

	public @NotNull GamePackEffects getEffects() {
		return effects;
	}
}
