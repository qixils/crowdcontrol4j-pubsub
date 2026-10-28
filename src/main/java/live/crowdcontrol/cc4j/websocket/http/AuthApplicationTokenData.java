package live.crowdcontrol.cc4j.websocket.http;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * Body of the {@code POST /auth/application/token} exchange.
 * Exactly one of {@link #secret()} or {@link #codeVerifier()} must be set.
 */
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public final class AuthApplicationTokenData {
	private final @NotNull String appID;
	private final @NotNull String code;
	private final @Nullable String secret;
	private final @Nullable String codeVerifier;

	/**
	 *
	 */
	@JsonCreator
	public AuthApplicationTokenData(
		@JsonProperty("appID") @NotNull String appID,
		@JsonProperty("code") @NotNull String code,
		@JsonProperty("secret") @Nullable String secret,
		@JsonProperty("codeVerifier") @Nullable String codeVerifier
	) {
		this.appID = appID;
		this.code = code;
		this.secret = secret;
		this.codeVerifier = codeVerifier;
	}

	/**
	 * Confidential-client exchange using the application secret.
	 */
	public AuthApplicationTokenData(@NotNull String appID, @NotNull String code, @NotNull String secret) {
		this(appID, code, secret, null);
	}

	/**
	 * Public-client (PKCE) exchange using the {@code code_verifier} whose
	 * S256 challenge was attached to the auth code request.
	 */
	public static @NotNull AuthApplicationTokenData forPkce(@NotNull String appID, @NotNull String code, @NotNull String codeVerifier) {
		return new AuthApplicationTokenData(appID, code, null, codeVerifier);
	}

	public @NotNull String appID() {
		return appID;
	}

	public @NotNull String code() {
		return code;
	}

	public @Nullable String secret() {
		return secret;
	}

	public @Nullable String codeVerifier() {
		return codeVerifier;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) return true;
		if (obj == null || obj.getClass() != this.getClass()) return false;
		AuthApplicationTokenData that = (AuthApplicationTokenData) obj;
		return Objects.equals(this.appID, that.appID) &&
			Objects.equals(this.code, that.code) &&
			Objects.equals(this.secret, that.secret) &&
			Objects.equals(this.codeVerifier, that.codeVerifier);
	}

	@Override
	public int hashCode() {
		return Objects.hash(appID, code, secret, codeVerifier);
	}

	@Override
	public String toString() {
		return "AuthApplicationTokenData[" +
			"appID=" + appID + ", " +
			"code=" + code + ", " +
			"secret=" + secret + ", " +
			"codeVerifier=" + codeVerifier + ']';
	}

}
