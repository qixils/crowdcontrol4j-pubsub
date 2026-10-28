package live.crowdcontrol.cc4j.websocket.http;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public final class CustomEffectParameterOption {
	private final @NotNull String name;

	@JsonCreator
	public CustomEffectParameterOption(
		@JsonProperty("name") @NotNull String name
	) {
		this.name = name;
	}

	public @NotNull String name() {
		return name;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) return true;
		if (obj == null || obj.getClass() != this.getClass()) return false;
		CustomEffectParameterOption that = (CustomEffectParameterOption) obj;
		return Objects.equals(this.name, that.name);
	}

	@Override
	public int hashCode() {
		return Objects.hash(name);
	}

	@Override
	public String toString() {
		return "CustomEffectParameterOption[" +
			"name=" + name + ']';
	}

}
