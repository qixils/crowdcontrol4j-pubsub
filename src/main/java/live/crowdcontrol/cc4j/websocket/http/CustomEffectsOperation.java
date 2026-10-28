package live.crowdcontrol.cc4j.websocket.http;

import org.intellij.lang.annotations.Pattern;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Objects;

public final class CustomEffectsOperation {
	@Pattern("merge|replace-all|replace-partial")
	private final @NotNull String mode;
	private final @NotNull Map<String, CustomEffect> effects;

	public CustomEffectsOperation(
		@NotNull @Pattern("merge|replace-all|replace-partial") String mode,
		@NotNull Map<String, CustomEffect> effects
	) {
		this.mode = mode;
		this.effects = effects;
	}

	@Pattern("merge|replace-all|replace-partial")
	public @NotNull String mode() {
		return mode;
	}

	public @NotNull Map<String, CustomEffect> effects() {
		return effects;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) return true;
		if (obj == null || obj.getClass() != this.getClass()) return false;
		CustomEffectsOperation that = (CustomEffectsOperation) obj;
		return Objects.equals(this.mode, that.mode) &&
			Objects.equals(this.effects, that.effects);
	}

	@Override
	public int hashCode() {
		return Objects.hash(mode, effects);
	}

	@Override
	public String toString() {
		return "CustomEffectsOperation[" +
			"mode=" + mode + ", " +
			"effects=" + effects + ']';
	}

}
