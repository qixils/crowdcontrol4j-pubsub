package live.crowdcontrol.cc4j.websocket.data;

import org.jetbrains.annotations.NotNull;

/**
 * Data holder for initiating a remote procedure call.
 */
public class RemoteProcedureCallData {
	private final @NotNull String token;
	private final @NotNull CallData<?> call;

	/**
	 * Creates a call.
	 *
	 * @param token the token used to authenticate
	 * @param call the data of the call
	 */
	public RemoteProcedureCallData(@NotNull String token,
								   @NotNull CallData<?> call) {
		this.token = token;
		this.call = call;
	}

	/**
	 * Gets the token used to authenticate this call.
	 *
	 * @return JWT token
	 */
	public @NotNull String getToken() {
		return token;
	}

	/**
	 * Gets the data of this call.
	 *
	 * @return call data
	 */
	public @NotNull CallData<?> getCall() {
		return call;
	}
}
