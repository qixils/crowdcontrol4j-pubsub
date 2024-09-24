package live.crowdcontrol.cc4j.websocket.data;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import org.jetbrains.annotations.NotNull;

/**
 * A method to be called on the remote server.
 */
public class CallDataMethod<A> {
	private final @NotNull String value;

	@JsonCreator
	public CallDataMethod(@NotNull String value) {
		this.value = value;
	}

	/**
	 * Returns the string value of this method.
	 *
	 * @return method id
	 */
	@JsonValue
	public @NotNull String getValue() {
		return value;
	}

	/**
	 * Informs the server of whether a purchased effect was executed successfully or not.
	 */
	public static final CallDataMethod<CCEffectResponse> EFFECT_RESPONSE = new CallDataMethod<>("effectResponse");

	/**
	 * Instructs the server to adjust the visibility or usability of an effect in the menu.
	 */
	public static final CallDataMethod<CCEffectReport> EFFECT_REPORT = new CallDataMethod<>("effectReport");
}
