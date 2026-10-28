package live.crowdcontrol.cc4j.websocket.data;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public final class GenerateAuthCodeData {
	public GenerateAuthCodeData(@NotNull String appID) {
		this(appID, null, null, null, null, null);
	}

	public GenerateAuthCodeData(@NotNull String appID,
								@Nullable List<String> scopes,
								@Nullable List<String> packs,
								@Nullable Boolean qrCode) {
		this(appID, scopes, packs, qrCode, null, null);
	}

	public @NotNull String appID() {
		return appID;
	}

	public @Nullable List<String> scopes() {
		return scopes;
	}

	public @Nullable List<String> packs() {
		return packs;
	}

	public @Nullable Boolean qrCode() {
		return qrCode;
	}

	public @Nullable String codeChallenge() {
		return codeChallenge;
	}

	public @Nullable String codeChallengeMethod() {
		return codeChallengeMethod;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) return true;
		if (obj == null || obj.getClass() != this.getClass()) return false;
		GenerateAuthCodeData that = (GenerateAuthCodeData) obj;
		return Objects.equals(this.appID, that.appID) &&
			Objects.equals(this.scopes, that.scopes) &&
			Objects.equals(this.packs, that.packs) &&
			Objects.equals(this.qrCode, that.qrCode) &&
			Objects.equals(this.codeChallenge, that.codeChallenge) &&
			Objects.equals(this.codeChallengeMethod, that.codeChallengeMethod);
	}

	@Override
	public int hashCode() {
		return Objects.hash(appID, scopes, packs, qrCode, codeChallenge, codeChallengeMethod);
	}

	@Override
	public String toString() {
		return "GenerateAuthCodeData[" +
			"appID=" + appID + ", " +
			"scopes=" + scopes + ", " +
			"packs=" + packs + ", " +
			"qrCode=" + qrCode + ", " +
			"codeChallenge=" + codeChallenge + ", " +
			"codeChallengeMethod=" + codeChallengeMethod + ']';
	}


	public static @NotNull String ACTION = "generate-auth-code";
	private final @NotNull String appID;
	private final @Nullable List<String> scopes;
	private final @Nullable List<String> packs;
	private final @Nullable Boolean qrCode;
	private final @Nullable String codeChallenge;
	private final @Nullable String codeChallengeMethod;

	@JsonCreator
	public GenerateAuthCodeData(
		@JsonProperty("appID") @NotNull String appID,
		@JsonProperty("scopes") @Nullable List<String> scopes,
		@JsonProperty("packs") @Nullable List<String> packs,
		@JsonProperty("qrCode") @Nullable Boolean qrCode,
		@JsonProperty("codeChallenge") @Nullable String codeChallenge,
		@JsonProperty("codeChallengeMethod") @Nullable String codeChallengeMethod
	) {
		this.appID = appID;
		this.scopes = scopes;
		this.packs = packs;
		this.qrCode = qrCode;
		this.codeChallenge = codeChallenge;
		this.codeChallengeMethod = codeChallengeMethod;
	}
}
