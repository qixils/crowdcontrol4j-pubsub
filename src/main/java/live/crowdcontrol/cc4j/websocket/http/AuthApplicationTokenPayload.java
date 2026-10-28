package live.crowdcontrol.cc4j.websocket.http;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public final class AuthApplicationTokenPayload {
	private final @NotNull String token;

	public AuthApplicationTokenPayload(
		@NotNull String token
	) {
		this.token = token;
	}

	public @NotNull String token() {
		return token;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) return true;
		if (obj == null || obj.getClass() != this.getClass()) return false;
		AuthApplicationTokenPayload that = (AuthApplicationTokenPayload) obj;
		return Objects.equals(this.token, that.token);
	}

	@Override
	public int hashCode() {
		return Objects.hash(token);
	}

	@Override
	public String toString() {
		return "AuthApplicationTokenPayload[" +
			"token=" + token + ']';
	}

}
