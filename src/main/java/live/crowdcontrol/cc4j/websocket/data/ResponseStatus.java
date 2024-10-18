package live.crowdcontrol.cc4j.websocket.data;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonEnumDefaultValue;
import com.fasterxml.jackson.annotation.JsonValue;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Describes the outcome of an {@code effect-request}.
 */
public enum ResponseStatus {
	/**
	 * The non-timed effect executed successfully.
	 */
	SUCCESS("success", false, true),
	/**
	 * The effect failed to execute but may work another time.
	 */
	FAIL_TEMPORARY("failTemporary", false, true),
	/**
	 * The effect failed to execute and should <i>never be requested again</i>
	 * (at least for this session).
	 */
	FAIL_PERMANENT("failPermanent", false, true),
	/**
	 * The effect could not be applied instantly but is expecting to apply soon.
	 * This is currently unused.
	 */
	DELAY_ESTIMATED("delayEstimated", true, false),
	/**
	 * The timed effect began successfully.
	 */
	TIMED_BEGIN("timedBegin", true, true),
	/**
	 * The timed effect has been paused.
	 */
	TIMED_PAUSE("timedPause", true, false),
	/**
	 * The timed effect has been resumed.
	 */
	TIMED_RESUME("timedResume", true, false),
	/**
	 * The timed effect has ended.
	 */
	TIMED_END("timedEnd", false, false),
	/**
	 * The JSON-specified value could not be decoded.
	 */
	@JsonEnumDefaultValue
	UNKNOWN("", false, true), // ig terminating?
	;

	// Static

	private static final Map<String, ResponseStatus> BY_VALUE;

	static {
		Map<String, ResponseStatus> byValue = new HashMap<>();
		for (ResponseStatus status : values()) {
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
	public static @NotNull ResponseStatus fromValue(@NotNull String value) {
		return BY_VALUE.getOrDefault(value, UNKNOWN);
	}

	// Instance

	private final @NotNull String value;
	private final boolean timed;
	private final boolean terminating;

	ResponseStatus(@NotNull String value, boolean timed, boolean terminating) {
		this.value = value;
		this.timed = timed;
		this.terminating = terminating;
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

	/**
	 * Gets whether this status represents a timed status.
	 * A timed status is required to use {@link CCTimedEffectResponse CCTimedEffectResponse},
	 * while non-timed statuses are required to use {@link CCInstantEffectResponse CCInstantEffectResponse}.
	 *
	 * @return is timed status
	 */
	public boolean isTimed() {
		return timed;
	}

	/**
	 * Gets whether this status represents a terminating status.
	 * A terminating status is one that finalizes whether an effect should consume or refund coins.
	 *
	 * @return is terminating status
	 */
	public boolean isTerminating() {
		return terminating;
	}

	@Override
	public String toString() {
		return getValue();
	}
}
