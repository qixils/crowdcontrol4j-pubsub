package live.crowdcontrol.cc4j.websocket.data;

import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * Informs the server of whether a purchased effect was executed successfully or not.
 *
 * @see CCInstantEffectResponse
 * @see CCTimedEffectResponse
 */
public abstract class CCEffectResponse {
	protected final @NotNull UUID id;
	protected final int stamp;
	protected final @NotNull UUID request;
	protected final @NotNull String message;
	protected final @NotNull ResponseStatus status;

	/**
	 * Creates a response.
	 *
	 * @param requestID the ID of the request
	 * @param status    the status of the request
	 * @param message   the reasoning to display to the viewer
	 */
	protected CCEffectResponse(@NotNull UUID requestID,
							   @NotNull ResponseStatus status,
							   @NotNull String message) {
		this.id = UUID.randomUUID();
		this.stamp = (int) (System.currentTimeMillis() / 1000L);
		this.request = requestID;
		this.status = status;
		this.message = message;
	}

	/**
	 * Gets the randomly generated ID of the argument.
	 *
	 * @return arg ID
	 */
	public @NotNull UUID getId() {
		return id;
	}

	/**
	 * Gets the ID of the request that this result is in response to.
	 *
	 * @return request ID
	 */
	public @NotNull UUID getRequestId() {
		return request;
	}

	/**
	 * Gets the timestamp in seconds since Unix epoch that this result was generated.
	 *
	 * @return unix epoch seconds timestamp
	 */
	public int getTimestamp() {
		return stamp;
	}

	/**
	 * Gets the message to be displayed to the viewer about the status.
	 * May not be displayed if the status is successful.
	 * String may be empty if nothing is to be displayed.
	 *
	 * @return message
	 */
	public @NotNull String getMessage() {
		return message;
	}

	/**
	 * The status value of the result.
	 *
	 * @return result status
	 */
	public @NotNull ResponseStatus getStatus() {
		return status;
	}
}
