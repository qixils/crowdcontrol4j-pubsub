package live.crowdcontrol.cc4j.websocket.http;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public record PutCustomEffectsData(
	@NotNull String gamePackID,
	@NotNull List<CustomEffectsOperation> operations
) {
}
