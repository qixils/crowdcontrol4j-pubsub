package live.crowdcontrol.cc4j.util;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * Captures a generic type at compile time by subclassing this anonymously,
 * like {@code new TypeToken<List<String>>() {}},
 * or by calling {@link #get(Class)} for non-generic types.
 *
 * @param <T> the captured type
 */
public abstract class TypeToken<T> {
	private final @NotNull Type type;

	/**
	 * Captures the type argument of the anonymous subclass.
	 */
	protected TypeToken() {
		Type superclass = getClass().getGenericSuperclass();
		if (!(superclass instanceof ParameterizedType))
			throw new IllegalStateException("TypeToken must be created with a type argument, i.e. new TypeToken<List<String>>() {}");
		this.type = ((ParameterizedType) superclass).getActualTypeArguments()[0];
	}

	private TypeToken(@NotNull Type type) {
		this.type = type;
	}

	/**
	 * Creates a token for a non-generic class.
	 *
	 * @param type class to wrap
	 * @param <T>  the class's type
	 * @return type token
	 */
	public static <T> @NotNull TypeToken<T> get(@NotNull Class<T> type) {
		return new TypeToken<T>(type) {
		};
	}

	/**
	 * Gets the captured type.
	 *
	 * @return captured type
	 */
	public @NotNull Type getType() {
		return type;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof TypeToken)) return false;
		return type.equals(((TypeToken<?>) o).type);
	}

	@Override
	public int hashCode() {
		return type.hashCode();
	}

	@Override
	public String toString() {
		return type.getTypeName();
	}
}
