package live.crowdcontrol.cc4j.websocket.http;

import org.intellij.lang.annotations.Pattern;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public record CustomEffectsOperation(
	@NotNull @Pattern("merge|replace-all|replace-partial") String mode,
	@NotNull Map<String, CustomEffect> effects
) {
}
