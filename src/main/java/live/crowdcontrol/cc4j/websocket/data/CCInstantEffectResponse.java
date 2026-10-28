package live.crowdcontrol.cc4j.websocket.data;

import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * Informs the server of whether a purchased instant effect was executed successfully or not.
 */
public class CCInstantEffectResponse extends CCEffectResponse {

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

	/**
	 * Creates a response.
	 *
	 * @param requestID the ID of the request
	 * @param status    the status of the request
	 */
	public CCInstantEffectResponse(@NotNull UUID requestID,
								   @NotNull ResponseStatus status) {
		this(requestID, status, "");
	}
}
