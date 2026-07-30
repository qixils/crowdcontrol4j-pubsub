package live.crowdcontrol.cc4j.websocket.http;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Body of the {@code POST /auth/application/token} exchange.
 * Exactly one of {@link #secret()} or {@link #codeVerifier()} must be set.
 */
public record AuthApplicationTokenData(
	@NotNull String appID,
	@NotNull String code,
	@Nullable String secret,
	@Nullable String codeVerifier
) {
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
}
