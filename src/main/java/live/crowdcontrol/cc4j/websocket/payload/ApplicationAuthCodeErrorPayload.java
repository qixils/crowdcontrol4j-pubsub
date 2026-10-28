package live.crowdcontrol.cc4j.websocket.payload;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public final class ApplicationAuthCodeErrorPayload {
	private final @NotNull String message;

	@JsonCreator
	public ApplicationAuthCodeErrorPayload(
		@JsonProperty("message") @NotNull String message
	) {
		this.message = message;
	}

	public @NotNull String message() {
		return message;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) return true;
		if (obj == null || obj.getClass() != this.getClass()) return false;
		ApplicationAuthCodeErrorPayload that = (ApplicationAuthCodeErrorPayload) obj;
		return Objects.equals(this.message, that.message);
	}

	@Override
	public int hashCode() {
		return Objects.hash(message);
	}

	@Override
	public String toString() {
		return "ApplicationAuthCodeErrorPayload[" +
			"message=" + message + ']';
	}

}
