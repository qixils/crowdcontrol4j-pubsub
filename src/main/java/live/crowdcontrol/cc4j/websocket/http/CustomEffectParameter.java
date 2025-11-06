package live.crowdcontrol.cc4j.websocket.http;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public record CustomEffectParameter(
	@NotNull String type,
	@NotNull String name,
	@Nullable Map<String, CustomEffectParameterOption> options
) {
	public static CustomEffectParameter options(@NotNull String name, @NotNull Map<String, CustomEffectParameterOption> options) {
		return new CustomEffectParameter("options", name, options);
	}

	public static CustomEffectParameter hexColor(@NotNull String name) {
		return new CustomEffectParameter("hex-color", name, null);
	}
}
