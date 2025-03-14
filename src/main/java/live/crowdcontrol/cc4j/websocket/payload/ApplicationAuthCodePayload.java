package live.crowdcontrol.cc4j.websocket.payload;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public record ApplicationAuthCodePayload(
	@NotNull String code,
	@NotNull String url,
	@Nullable String qrCode
) {
}
