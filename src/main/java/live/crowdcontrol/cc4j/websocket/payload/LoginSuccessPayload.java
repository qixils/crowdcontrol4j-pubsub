package live.crowdcontrol.cc4j.websocket.payload;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
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
	@JsonCreator
	public LoginSuccessPayload(@JsonProperty("token") @NotNull String token) {
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
