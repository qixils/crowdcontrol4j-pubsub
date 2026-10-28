package live.crowdcontrol.cc4j.websocket.http;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Objects;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public final class CustomEffectQuantity {
	private final int min;
	private final int max;

	@JsonCreator
	public CustomEffectQuantity(
		@JsonProperty("min") int min,
		@JsonProperty("max") int max
	) {
		this.min = min;
		this.max = max;
	}

	public CustomEffectQuantity(int max) {
		this(1, max);
	}

	public int min() {
		return min;
	}

	public int max() {
		return max;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) return true;
		if (obj == null || obj.getClass() != this.getClass()) return false;
		CustomEffectQuantity that = (CustomEffectQuantity) obj;
		return this.min == that.min &&
			this.max == that.max;
	}

	@Override
	public int hashCode() {
		return Objects.hash(min, max);
	}

	@Override
	public String toString() {
		return "CustomEffectQuantity[" +
			"min=" + min + ", " +
			"max=" + max + ']';
	}

}
