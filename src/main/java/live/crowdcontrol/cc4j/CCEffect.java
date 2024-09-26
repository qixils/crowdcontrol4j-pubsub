package live.crowdcontrol.cc4j;

import live.crowdcontrol.cc4j.websocket.data.CCEffectResponse;
import live.crowdcontrol.cc4j.websocket.payload.PublicEffectPayload;
import org.intellij.lang.annotations.RegExp;
import org.jetbrains.annotations.NotNull;

/**
 * An effect which a viewer can purchase to perform an action.
 */
public interface CCEffect {

	@RegExp
	String EFFECT_ID_PATTERN = "^(?!__cc)[a-zA-Z_][a-zA-Z0-9_]*$";

//	/**
//	 * Gets the ID of the effect.
//	 * Used to determine which effect object to execute.
//	 *
//	 * @return effect ID
//	 */
//	@Pattern(EFFECT_ID_PATTERN)
//	String effectID();

	/**
	 * Triggers this effect.
	 * To respond to it, call {@link CCPlayer#sendResponse(CCEffectResponse)}.
	 * Please note that if you fail to emit a terminating response within {@value CrowdControl#QUEUE_DURATION} seconds
	 * then a failure response will be produced for you.
	 *
	 * @param request the request responsible for invoking this effect
	 * @param source the player whose connection originated this request
	 */
	void onTrigger(@NotNull PublicEffectPayload request, @NotNull CCPlayer source);
}
