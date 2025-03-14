package live.crowdcontrol.cc4j.websocket.data;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public record GenerateAuthCodeData(
	@NotNull String appID,
	@Nullable List<String> scopes,
	@Nullable List<String> packs,
	@Nullable Boolean qrCode
) {
	public GenerateAuthCodeData(@NotNull String appID) {
		this(appID, null, null, null);
	}

	public static @NotNull String ACTION = "generate-auth-code";
}
