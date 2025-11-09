package live.crowdcontrol.cc4j.websocket.http;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;

public record CustomEffectDuration(
	double value,
	@Nullable Boolean immutable
) {
	public CustomEffectDuration(double value) {
		this(value, null);
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
