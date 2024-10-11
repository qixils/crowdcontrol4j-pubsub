package live.crowdcontrol.cc4j.websocket.data;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * Informs the server of whether a purchased timed effect was executed successfully or not.
 */
public class CCTimedEffectResponse extends CCEffectResponse {
	protected long timeRemaining;

	@JsonCreator
	CCTimedEffectResponse(@JsonProperty("id") @NotNull UUID id,
						  @JsonProperty("stamp") int stamp,
						  @JsonProperty("request") @NotNull UUID request,
						  @JsonProperty("message") @NotNull String message,
						  @JsonProperty("status") @NotNull ResponseStatus status,
						  long timeRemaining) {
		super(id, stamp, request, message, status);
		this.timeRemaining = timeRemaining;
	}

	/**
	 * Creates a response.
	 *
	 * @param requestID the ID of the request
	 * @param status    the status of the request
	 * @param message   the reasoning to display to the viewer
	 * @param timeRemaining the time remaining on the effect in milliseconds
	 */
	public CCTimedEffectResponse(@NotNull UUID requestID,
								 @NotNull ResponseStatus status,
								 @NotNull String message,
								 long timeRemaining) {
		super(requestID, status, message);
		this.timeRemaining = timeRemaining;

		if (!status.isTimed()) {
			throw new IllegalArgumentException("Expected a timed status, received instant status " + status);
		}
	}

	/**
	 * Creates a response.
	 *
	 * @param requestID the ID of the request
	 * @param status    the status of the request
	 * @param timeRemaining the time remaining on the effect in milliseconds
	 */
	public CCTimedEffectResponse(@NotNull UUID requestID,
								 @NotNull ResponseStatus status,
								 long timeRemaining) {
		this(requestID, status, "", timeRemaining);
	}

	/**
	 * Gets the time remaining on the effect in milliseconds.
	 *
	 * @return milliseconds
	 */
	public long getTimeRemaining() {
		return timeRemaining;
	}
}
