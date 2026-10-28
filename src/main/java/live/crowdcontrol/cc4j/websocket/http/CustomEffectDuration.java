package live.crowdcontrol.cc4j.websocket.http;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.Objects;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public final class CustomEffectDuration {
	private final double value;
	private final @Nullable Boolean immutable;

	@JsonCreator
	public CustomEffectDuration(
		@JsonProperty("value") double value,
		@JsonProperty("immutable") @Nullable Boolean immutable
	) {
		this.value = value;
		this.immutable = immutable;
	}

	public CustomEffectDuration(double value) {
		this(value, null);
	}

	public double value() {
		return value;
	}

	public @Nullable Boolean immutable() {
		return immutable;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) return true;
		if (obj == null || obj.getClass() != this.getClass()) return false;
		CustomEffectDuration that = (CustomEffectDuration) obj;
		return Double.doubleToLongBits(this.value) == Double.doubleToLongBits(that.value) &&
			Objects.equals(this.immutable, that.immutable);
	}

	@Override
	public int hashCode() {
		return Objects.hash(value, immutable);
	}

	@Override
	public String toString() {
		return "CustomEffectDuration[" +
			"value=" + value + ", " +
			"immutable=" + immutable + ']';
	}


	@ApiStatus.Internal
	public static class CustomEffectDurationAdapter extends StdDeserializer<CustomEffectDuration> {
		public CustomEffectDurationAdapter() {
			super(CustomEffectDuration.class);
		}

		@Override
		public CustomEffectDuration deserialize(JsonParser parser, DeserializationContext context) throws IOException, JacksonException {
			double value;
			Boolean immutable = null;
			if (parser.isExpectedStartObjectToken()) {
				ObjectCodec codec = parser.getCodec();
				JsonNode node = codec.readTree(parser);

				value = node.get("value").asDouble();
				JsonNode immutableNode = node.get("immutable");
				immutable = immutableNode != null && !immutableNode.isNull() ? immutableNode.asBoolean(false) : null; // idk why the node can be null
			} else {
				value = parser.getDoubleValue();
			}
			return new CustomEffectDuration(value, immutable);
		}
	}
}
