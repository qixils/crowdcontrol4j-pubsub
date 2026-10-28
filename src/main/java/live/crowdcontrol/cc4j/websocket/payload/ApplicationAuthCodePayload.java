package live.crowdcontrol.cc4j.websocket.payload;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public final class ApplicationAuthCodePayload {
	private final @NotNull String code;
	private final @NotNull String url;
	private final @Nullable String qrCode;

	public ApplicationAuthCodePayload(
		@NotNull String code,
		@NotNull String url,
		@Nullable String qrCode
	) {
		this.code = code;
		this.url = url;
		this.qrCode = qrCode;
	}

	public @NotNull String code() {
		return code;
	}

	public @NotNull String url() {
		return url;
	}

	public @Nullable String qrCode() {
		return qrCode;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) return true;
		if (obj == null || obj.getClass() != this.getClass()) return false;
		ApplicationAuthCodePayload that = (ApplicationAuthCodePayload) obj;
		return Objects.equals(this.code, that.code) &&
			Objects.equals(this.url, that.url) &&
			Objects.equals(this.qrCode, that.qrCode);
	}

	@Override
	public int hashCode() {
		return Objects.hash(code, url, qrCode);
	}

	@Override
	public String toString() {
		return "ApplicationAuthCodePayload[" +
			"code=" + code + ", " +
			"url=" + url + ", " +
			"qrCode=" + qrCode + ']';
	}

}
