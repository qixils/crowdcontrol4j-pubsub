package live.crowdcontrol.cc4j.websocket.http;

import com.google.gson.*;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Type;
import java.util.Objects;

public final class CustomEffectDuration {
	private final double value;
	private final @Nullable Boolean immutable;

	public CustomEffectDuration(
		double value,
		@Nullable Boolean immutable
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
	public static class CustomEffectDurationAdapter implements JsonDeserializer<CustomEffectDuration> {
		@Override
		public CustomEffectDuration deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
			double value;
			Boolean immutable = null;
			if (json.isJsonObject()) {
				JsonObject node = json.getAsJsonObject();

				value = node.get("value").getAsDouble();
				JsonElement immutableNode = node.get("immutable");
				immutable = immutableNode != null && !immutableNode.isJsonNull() ? immutableNode.getAsBoolean() : null;
			} else {
				value = json.getAsDouble();
			}
			return new CustomEffectDuration(value, immutable);
		}
	}
}
