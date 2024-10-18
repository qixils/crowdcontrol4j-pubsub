package live.crowdcontrol.cc4j.websocket.http;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.jetbrains.annotations.NotNull;

/**
 * Describes a Crowd Control Game Pack.
 */
public class GamePack {
	private final @NotNull String gamePackID;
	private final @NotNull GamePayload game;
	private final @NotNull GamePackMeta meta;
	private final @NotNull GamePackEffects effects;

	public GamePack(@JsonProperty("gamePackID") @NotNull String gamePackID,
					@JsonProperty("game") @NotNull GamePayload game,
					@JsonProperty("meta") @NotNull GamePackMeta meta,
					@JsonProperty("effects") @NotNull GamePackEffects effects) {
		this.gamePackID = gamePackID;
		this.game = game;
		this.meta = meta;
		this.effects = effects;
	}

	@JsonProperty("gamePackID")
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
