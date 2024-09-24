package live.crowdcontrol.cc4j.websocket.data;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonEnumDefaultValue;
import com.fasterxml.jackson.annotation.JsonValue;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Identifies ways in which groups of effects can be referenced.
 */
public enum IdentifierType {
	/**
	 * References individual effects.
	 */
	EFFECT("effect"),
	/**
	 * References categories of effects, which are user-facing collections of effects.
	 */
	CATEGORY("category"),
	/**
	 * References groups of effects, which are exclusively developer-facing collections to effects.
	 */
	GROUP("group"),
	/**
	 * The JSON-specified value could not be decoded.
	 */
	@JsonEnumDefaultValue
	UNKNOWN(""),
	;

	// Static

	private static final Map<String, IdentifierType> BY_VALUE;

	static {
		Map<String, IdentifierType> byValue = new HashMap<>();
		for (IdentifierType status : values()) {
			if (status == UNKNOWN) continue;
			byValue.put(status.value, status);
		}
		BY_VALUE = Collections.unmodifiableMap(byValue);
	}

	/**
	 * Gets an identifier from its JSON string value.
	 * If an identifier by the provided name could not be found, returns {@link #UNKNOWN}.
	 *
	 * @param value JSON string value
	 * @return result status value
	 */
	@JsonCreator
	public static @NotNull IdentifierType fromValue(@NotNull String value) {
		return BY_VALUE.getOrDefault(value, UNKNOWN);
	}

	// Instance

	private final @NotNull String value;

	IdentifierType(@NotNull String value) {
		this.value = value;
	}

	/**
	 * Gets the encoded string value of this identifier.
	 *
	 * @return json value
	 */
	@JsonValue
	public @NotNull String getValue() {
		return value;
	}

	@Override
	public String toString() {
		return getValue();
	}
}
