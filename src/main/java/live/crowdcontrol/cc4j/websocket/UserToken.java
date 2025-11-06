package live.crowdcontrol.cc4j.websocket;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import live.crowdcontrol.cc4j.IUserRecord;
import live.crowdcontrol.cc4j.websocket.payload.ProfileType;
import org.intellij.lang.annotations.Pattern;
import org.intellij.lang.annotations.RegExp;
import org.intellij.lang.annotations.Subst;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class UserToken implements IUserRecord {
	@RegExp
	public static final String CCUID_PATTERN = "^ccuid-[0-7][0-9a-hjkmnp-tv-z]{25}$";

	private final @NotNull String type;
	private final @NotNull String jti;
	@Pattern(CCUID_PATTERN)
	@Subst("ccuid-01j7cnrvpbh5aw45pwpe1vqvdw")
	private final @NotNull String ccUID;
	private final @NotNull String originID;
	private final @NotNull ProfileType profile;
	private final @NotNull String name;
	private final @NotNull List<@NotNull String> roles;
	private final @Nullable UserTokenApplication app;
	private final long exp;
	private final @NotNull String ver;

	@JsonCreator
	public UserToken(@JsonProperty("type") @NotNull String type,
					 @JsonProperty("jti") @NotNull String jti,
					 @JsonProperty("ccUID") @Pattern(CCUID_PATTERN) @Subst("ccuid-01j7cnrvpbh5aw45pwpe1vqvdw") @NotNull String ccUID,
					 @JsonProperty("originID") @NotNull String originID,
					 @JsonProperty("profileType") @NotNull ProfileType profile,
					 @JsonProperty("name") @NotNull String name,
					 @JsonProperty("roles") @NotNull List<@NotNull String> roles,
					 @JsonProperty("app") @Nullable UserTokenApplication app,
					 @JsonProperty("exp") long exp,
					 @JsonProperty("ver") @NotNull String ver) {
		this.type = type;
		this.jti = jti;
		this.ccUID = ccUID;
		this.originID = originID;
		this.profile = profile;
		this.name = name;
		this.roles = roles;
		this.app = app;
		this.exp = exp;
		this.ver = ver;
	}

	public @NotNull String getType() {
		return type;
	}

	public @NotNull String getJti() {
		return jti;
	}

	@Pattern(CCUID_PATTERN)
	@Override
	@JsonProperty("ccUID")
	public @NotNull String getId() {
		return ccUID;
	}

	@Override
	public @NotNull String getName() {
		return name;
	}

	@Override
	public @NotNull ProfileType getProfile() {
		return profile;
	}

	@Override
	@JsonProperty("originID")
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
	 * Gets the details for the Application that initialized this token.
	 *
	 * @return app details or null
	 */
	public @Nullable UserTokenApplication getApp() {
		return app;
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
