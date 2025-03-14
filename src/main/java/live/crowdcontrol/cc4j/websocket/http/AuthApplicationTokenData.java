package live.crowdcontrol.cc4j.websocket.http;

import org.jetbrains.annotations.NotNull;

public record AuthApplicationTokenData(
	@NotNull String appID,
	@NotNull String code,
	@NotNull String secret
) {
}
