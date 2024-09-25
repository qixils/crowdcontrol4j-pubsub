package live.crowdcontrol.cc4j;

import live.crowdcontrol.cc4j.websocket.payload.PublicEffectPayload;
import org.jetbrains.annotations.NotNull;

public interface CCTimedEffect extends CCEffect {

	/**
	 * Called when an effect is paused.
	 * No response is expected of you.
	 */
	default void onPause(@NotNull PublicEffectPayload request, @NotNull CCPlayer source) {
	}

	/**
	 * Called when an effect is resumed.
	 * No response is expected of you.
	 */
	default void onResume(@NotNull PublicEffectPayload request, @NotNull CCPlayer source) {
	}

	/**
	 * Called when an effect ends.
	 * No response is expected of you.
	 */
	default void onEnd(@NotNull PublicEffectPayload request, @NotNull CCPlayer source) {
	}
}
