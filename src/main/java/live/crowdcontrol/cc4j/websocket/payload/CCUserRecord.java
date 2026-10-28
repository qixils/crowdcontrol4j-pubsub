package live.crowdcontrol.cc4j.websocket.payload;

import live.crowdcontrol.cc4j.IUserRecord;
import org.intellij.lang.annotations.Pattern;
import org.intellij.lang.annotations.Subst;
import org.jetbrains.annotations.NotNull;

import static live.crowdcontrol.cc4j.websocket.UserToken.CCUID_PATTERN;

/**
 * A record detailing information about a Crowd Control user.
 */
public class CCUserRecord implements IUserRecord {
	@Subst("ccuid-01j7cnrvpbh5aw45pwpe1vqvdw")
	@Pattern(CCUID_PATTERN)
	private final @NotNull String ccUID;
	private final @NotNull String name;
	private final @NotNull ProfileType profile;
	private final @NotNull String originID;
	private final @NotNull String image;

	/**
	 * Creates a user record.
	 *
	 * @param ccUID    user ID
	 * @param name     display name
	 * @param profile  profile type
	 * @param originID profile ID
	 * @param image    profile picture URL
	 */
	public CCUserRecord(@Pattern(CCUID_PATTERN) @Subst("ccuid-01j7cnrvpbh5aw45pwpe1vqvdw") @NotNull String ccUID,
						@NotNull String name,
						@NotNull ProfileType profile,
						@NotNull String originID,
						@NotNull String image) {
		this.ccUID = ccUID;
		this.image = image;
		this.name = name;
		this.profile = profile;
		this.originID = originID;
	}

	@Pattern(CCUID_PATTERN)
	@Override
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
	public @NotNull String getOriginId() {
		return originID;
	}

	/**
	 * Gets the user's profile picture URL.
	 *
	 * @return profile picture URL
	 */
	public @NotNull String getImage() {
		return image;
	}

	@Override
	public String toString() {
		return "CCUserRecord{" +
			"ccUID='" + ccUID + '\'' +
			", name='" + name + '\'' +
			", profile=" + profile +
			", originID='" + originID + '\'' +
			", image='" + image + '\'' +
			'}';
	}
}
