package live.crowdcontrol.cc4j.websocket.payload;

import org.jetbrains.annotations.NotNull;

/**
 * The payload denoting a successful login.
 */
public class LoginSuccessPayload {
	private final @NotNull String token;

	/**
	 * Creates a payload.
	 *
	 * @param token JWT auth token
	 */
	public LoginSuccessPayload(@NotNull String token) {
		this.token = token;
	}

	/**
	 * Gets the user's auth token.
	 *
	 * @return JWT token
	 */
	public @NotNull String getToken() {
		return token;
	}
}
