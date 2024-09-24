package live.crowdcontrol.cc4j.websocket.data;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * Informs the server of whether a purchased instant effect was executed successfully or not.
 */
public class CCInstantEffectResponse extends CCEffectResponse {

	@JsonCreator
	CCInstantEffectResponse(@JsonProperty("id") @NotNull UUID id,
							@JsonProperty("stamp") int stamp,
							@JsonProperty("request") @NotNull UUID request,
							@JsonProperty("message") @NotNull String message,
							@JsonProperty("status") @NotNull ResponseStatus status) {
		super(id, stamp, request, message, status);
	}

	/**
	 * Creates a response.
	 *
	 * @param requestID the ID of the request
	 * @param status    the status of the request
	 * @param message   the reasoning to display to the viewer
	 */
	public CCInstantEffectResponse(@NotNull UUID requestID,
								   @NotNull ResponseStatus status,
								   @NotNull String message) {
		super(requestID, status, message);

		if (status.isTimed()) {
			throw new IllegalArgumentException("Expected an instant status, received timed status " + status);
		}
	}
}
