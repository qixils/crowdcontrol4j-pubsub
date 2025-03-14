package live.crowdcontrol.cc4j.websocket.payload;

import org.jetbrains.annotations.NotNull;

public record ApplicationAuthCodeRedeemedPayload(
	@NotNull String code
) {
}
