package live.crowdcontrol.cc4j.websocket.http;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public final class CustomEffectParameterOption {
	private final @NotNull String name;

	public CustomEffectParameterOption(
		@NotNull String name
	) {
		this.name = name;
	}

	public @NotNull String name() {
		return name;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) return true;
		if (obj == null || obj.getClass() != this.getClass()) return false;
		CustomEffectParameterOption that = (CustomEffectParameterOption) obj;
		return Objects.equals(this.name, that.name);
	}

	@Override
	public int hashCode() {
		return Objects.hash(name);
	}

	@Override
	public String toString() {
		return "CustomEffectParameterOption[" +
			"name=" + name + ']';
	}

}
