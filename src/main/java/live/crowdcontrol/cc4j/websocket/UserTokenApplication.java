package live.crowdcontrol.cc4j.websocket;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public record UserTokenApplication(
	@NotNull String appID,
	@NotNull List<@NotNull String> scopes,
	@Nullable List<@NotNull String> packs
) {
}
