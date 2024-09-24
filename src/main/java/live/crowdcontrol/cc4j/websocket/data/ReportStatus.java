package live.crowdcontrol.cc4j.websocket.data;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonEnumDefaultValue;
import com.fasterxml.jackson.annotation.JsonValue;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Dictates the status of effects in the menu.
 */
public enum ReportStatus {
	/**
     * The antithesis of {@link #MENU_UNAVAILABLE}; indicates that an effect should now be purchasable from the menu.
	 * Note that this does not indicate that an effect should be visible; for that see {@link #MENU_VISIBLE}.
	 */
	MENU_AVAILABLE("menuAvailable"),
	/**
	 * The antithesis of {@link #MENU_AVAILABLE}; indicates that an effect should no longer be purchasable from the menu.
	 */
	MENU_UNAVAILABLE("menuUnavailable"),
	/**
	 * The antithesis of {@link #MENU_HIDDEN}; indicates that an effect should be visible in the menu.
	 * Note that this does not indicate that an effect should be purchasable; for that see {@link #MENU_AVAILABLE}.
	 */
	MENU_VISIBLE("menuVisible"),
	/**
	 * The antithesis of {@link #MENU_VISIBLE}; indicates that an effect should no longer be visible in the menu.
	 */
	MENU_HIDDEN("menuHidden"),
	/**
	 * The JSON-specified value could not be decoded.
	 */
	@JsonEnumDefaultValue
	UNKNOWN(""),
	;

	// Static

	private static final Map<String, ReportStatus> BY_VALUE;

	static {
		Map<String, ReportStatus> byValue = new HashMap<>();
		for (ReportStatus status : values()) {
			if (status == UNKNOWN) continue;
			byValue.put(status.value, status);
		}
		BY_VALUE = Collections.unmodifiableMap(byValue);
	}

	/**
	 * Gets a status from its JSON string value.
	 * If a status by the provided name could not be found, returns {@link #UNKNOWN}.
	 *
	 * @param value JSON string value
	 * @return result status value
	 */
	@JsonCreator
	public static @NotNull ReportStatus fromValue(@NotNull String value) {
		return BY_VALUE.getOrDefault(value, UNKNOWN);
	}

	// Instance

	private final @NotNull String value;

	ReportStatus(@NotNull String value) {
		this.value = value;
	}

	/**
	 * Gets the encoded string value of this status.
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
