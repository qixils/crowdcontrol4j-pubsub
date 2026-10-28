package live.crowdcontrol.cc4j.websocket.payload;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public final class ApplicationAuthCodeRedeemedPayload {
	private final @NotNull String code;

	@JsonCreator
	public ApplicationAuthCodeRedeemedPayload(
		@JsonProperty("code") @NotNull String code
	) {
		this.code = code;
	}

	public @NotNull String code() {
		return code;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) return true;
		if (obj == null || obj.getClass() != this.getClass()) return false;
		ApplicationAuthCodeRedeemedPayload that = (ApplicationAuthCodeRedeemedPayload) obj;
		return Objects.equals(this.code, that.code);
	}

	@Override
	public int hashCode() {
		return Objects.hash(code);
	}

	@Override
	public String toString() {
		return "ApplicationAuthCodeRedeemedPayload[" +
			"code=" + code + ']';
	}

}
