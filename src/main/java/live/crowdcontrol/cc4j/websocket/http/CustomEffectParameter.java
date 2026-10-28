package live.crowdcontrol.cc4j.websocket.http;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Objects;

public final class CustomEffectParameter {
	private final @NotNull String type;
	private final @NotNull String name;
	private final @Nullable Map<String, CustomEffectParameterOption> options;

	public CustomEffectParameter(
		@NotNull String type,
		@NotNull String name,
		@Nullable Map<String, CustomEffectParameterOption> options
	) {
		this.type = type;
		this.name = name;
		this.options = options;
	}

	public static CustomEffectParameter options(@NotNull String name, @NotNull Map<String, CustomEffectParameterOption> options) {
		return new CustomEffectParameter("options", name, options);
	}

	public static CustomEffectParameter hexColor(@NotNull String name) {
		return new CustomEffectParameter("hex-color", name, null);
	}

	public @NotNull String type() {
		return type;
	}

	public @NotNull String name() {
		return name;
	}

	public @Nullable Map<String, CustomEffectParameterOption> options() {
		return options;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) return true;
		if (obj == null || obj.getClass() != this.getClass()) return false;
		CustomEffectParameter that = (CustomEffectParameter) obj;
		return Objects.equals(this.type, that.type) &&
			Objects.equals(this.name, that.name) &&
			Objects.equals(this.options, that.options);
	}

	@Override
	public int hashCode() {
		return Objects.hash(type, name, options);
	}

	@Override
	public String toString() {
		return "CustomEffectParameter[" +
			"type=" + type + ", " +
			"name=" + name + ", " +
			"options=" + options + ']';
	}

}
