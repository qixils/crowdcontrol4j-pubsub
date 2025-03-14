package live.crowdcontrol.cc4j.websocket.http;

import org.jetbrains.annotations.NotNull;

public record AuthApplicationTokenPayload(
	@NotNull String token
) {
}
