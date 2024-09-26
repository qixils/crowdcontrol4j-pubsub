package live.crowdcontrol.cc4j.websocket.http;

import com.fasterxml.jackson.annotation.JsonProperty;
import live.crowdcontrol.cc4j.websocket.payload.CCBaseEffectDescription;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class GamePackEffects {
	private final @Nullable Map<String, CCBaseEffectDescription> game;
	// private final @Nullable Map<String, OverlayEffect> overlay;
	// private final @Nullable Map<String, SfxEffect> sfx;

	public GamePackEffects(@JsonProperty("game") @Nullable Map<String, CCBaseEffectDescription> game) {
		this.game = game;
	}

	public @Nullable Map<String, CCBaseEffectDescription> getGame() {
		return game;
	}
}
