package live.crowdcontrol.cc4j.websocket.payload;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public final class ApplicationAuthCodePayload {
	private final @NotNull String code;
	private final @NotNull String url;
	private final @Nullable String qrCode;

	@JsonCreator
	public ApplicationAuthCodePayload(
		@JsonProperty("code") @NotNull String code,
		@JsonProperty("url") @NotNull String url,
		@JsonProperty("qrCode") @Nullable String qrCode
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
