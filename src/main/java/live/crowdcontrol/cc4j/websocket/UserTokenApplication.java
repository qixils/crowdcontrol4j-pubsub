package live.crowdcontrol.cc4j.websocket;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;

public final class UserTokenApplication {
	private final @NotNull String appID;
	private final @NotNull List<@NotNull String> scopes;
	private final @Nullable List<@NotNull String> packs;

	public UserTokenApplication(
		@NotNull String appID,
		@NotNull List<@NotNull String> scopes,
		@Nullable List<@NotNull String> packs
	) {
		this.appID = appID;
		this.scopes = scopes;
		this.packs = packs;
	}

	public @NotNull String appID() {
		return appID;
	}

	public @NotNull List<@NotNull String> scopes() {
		return scopes;
	}

	public @Nullable List<@NotNull String> packs() {
		return packs;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) return true;
		if (obj == null || obj.getClass() != this.getClass()) return false;
		UserTokenApplication that = (UserTokenApplication) obj;
		return Objects.equals(this.appID, that.appID) &&
			Objects.equals(this.scopes, that.scopes) &&
			Objects.equals(this.packs, that.packs);
	}

	@Override
	public int hashCode() {
		return Objects.hash(appID, scopes, packs);
	}

	@Override
	public String toString() {
		return "UserTokenApplication[" +
			"appID=" + appID + ", " +
			"scopes=" + scopes + ", " +
			"packs=" + packs + ']';
	}

}
