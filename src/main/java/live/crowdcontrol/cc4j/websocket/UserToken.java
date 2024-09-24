package live.crowdcontrol.cc4j.websocket;

import org.intellij.lang.annotations.Pattern;
import org.intellij.lang.annotations.RegExp;
import org.intellij.lang.annotations.Subst;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class UserToken {
	@RegExp
	public static final String CCUID_PATTERN = "^ccuid-[0-7][0-9a-hjkmnp-tv-z]{25}$";

	private final @NotNull String type;
	private final @NotNull String jti;
	@Pattern(CCUID_PATTERN)
	@Subst("ccuid-01j7cnrvpbh5aw45pwpe1vqvdw")
	private final @NotNull String ccUID;
	private final @NotNull String originID;
	private final @NotNull String profileType;
	private final @NotNull String name;
	private final @NotNull List<@NotNull String> roles;
	private final long exp;
	private final @NotNull String ver;

	public UserToken(@NotNull String type,
					 @NotNull String jti,
					 @Pattern(CCUID_PATTERN) @Subst("ccuid-01j7cnrvpbh5aw45pwpe1vqvdw") @NotNull String ccUID,
					 @NotNull String originID,
					 @NotNull String profileType,
					 @NotNull String name,
					 @NotNull List<@NotNull String> roles,
					 long exp,
					 @NotNull String ver) {
		this.type = type;
		this.jti = jti;
		this.ccUID = ccUID;
		this.originID = originID;
		this.profileType = profileType;
		this.name = name;
		this.roles = roles;
		this.exp = exp;
		this.ver = ver;
	}

	public @NotNull String getType() {
		return type;
	}

	public @NotNull String getJti() {
		return jti;
	}

	/**
	 * Gets the user's Crowd Control User ID.
	 *
	 * @return ccUID
	 */
	public @NotNull String getId() {
		return ccUID;
	}

	/**
	 * Gets the user's display name.
	 *
	 * @return display name
	 */
	public @NotNull String getName() {
		return name;
	}

	/**
	 * Gets the type of profile the user logged in with.
	 *
	 * @return profile type
	 */
	public @NotNull String getProfile() {
		return profileType;
	}

	/**
	 * Gets the ID of the user on their {@link #getProfile() home platform}.
	 *
	 * @return origin id
	 */
	public @NotNull String getOriginId() {
		return originID;
	}

	/**
	 * Gets the user's roles.
	 * Some roles include {@code admin}, {@code ambassador}, {@code partner}.
	 *
	 * @return list of roles
	 */
	public @NotNull List<@NotNull String> getRoles() {
		return roles;
	}

	/**
	 * Gets the Unix epoch seconds timestamp at which this token will expire.
	 *
	 * @return expiration time in unix epoch seconds
	 */
	public long getExp() {
		return exp;
	}

	public @NotNull String getVer() {
		return ver;
	}
}
