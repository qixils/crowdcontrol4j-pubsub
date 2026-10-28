package live.crowdcontrol.cc4j.util;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.function.Function;
import java.util.function.UnaryOperator;

/**
 * {@link Gson} helpers
 */
@ApiStatus.Internal
public final class GsonAdapters {
	private GsonAdapters() {
	}

	/**
	 * Creates an adapter which (de)serializes a value (such as an enum) as a plain string.
	 *
	 * @param toValue   converts an object to its JSON string
	 * @param fromValue converts a JSON string to its object
	 * @param <T>       type of object
	 * @return null-safe type adapter
	 */
	public static <T> @NotNull TypeAdapter<T> stringValued(@NotNull Function<T, String> toValue, @NotNull Function<String, T> fromValue) {
		return new TypeAdapter<T>() {
			@Override
			public void write(JsonWriter out, T value) throws IOException {
				out.value(toValue.apply(value));
			}

			@Override
			public T read(JsonReader in) throws IOException {
				return fromValue.apply(in.nextString());
			}
		}.nullSafe();
	}

	/**
	 * Creates a factory which deserializes {@code type} reflectively and then passes the result through {@code rebuild}.
	 * Gson does not invoke constructors, so this allows constructor logic
	 * (such as defaults and normalization) to be re-applied.
	 *
	 * @param type    exact class to handle
	 * @param rebuild function which re-creates the object via its constructor
	 * @param <T>     type of object
	 * @return type adapter factory
	 */
	public static <T> @NotNull TypeAdapterFactory rebuilding(@NotNull Class<T> type, @NotNull UnaryOperator<T> rebuild) {
		return new TypeAdapterFactory() {
			@Override
			@SuppressWarnings("unchecked")
			public <R> TypeAdapter<R> create(Gson gson, TypeToken<R> typeToken) {
				if (typeToken.getRawType() != type) return null;
				TypeAdapter<T> delegate = (TypeAdapter<T>) gson.getDelegateAdapter(this, typeToken);
				return (TypeAdapter<R>) new TypeAdapter<T>() {
					@Override
					public void write(JsonWriter out, T value) throws IOException {
						delegate.write(out, value);
					}

					@Override
					public T read(JsonReader in) throws IOException {
						T value = delegate.read(in);
						return value == null ? null : rebuild.apply(value);
					}
				};
			}
		};
	}
}
