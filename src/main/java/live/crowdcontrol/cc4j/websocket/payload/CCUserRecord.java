package live.crowdcontrol.cc4j.websocket.payload;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.intellij.lang.annotations.Pattern;
import org.intellij.lang.annotations.Subst;
import org.jetbrains.annotations.NotNull;

import static live.crowdcontrol.cc4j.websocket.UserToken.CCUID_PATTERN;

/**
 * A record detailing information about a Crowd Control user.
 */
public class CCUserRecord {
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
	@JsonCreator
	public CCUserRecord(@JsonProperty("ccUID") @Pattern(CCUID_PATTERN) @Subst("ccuid-01j7cnrvpbh5aw45pwpe1vqvdw") @NotNull String ccUID,
						@JsonProperty("name") @NotNull String name,
						@JsonProperty("profile") @NotNull ProfileType profile,
						@JsonProperty("originID") @NotNull String originID,
						@JsonProperty("image") @NotNull String image) {
		this.ccUID = ccUID;
		this.image = image;
		this.name = name;
		this.profile = profile;
		this.originID = originID;
	}

	/**
	 * Gets the user's Crowd Control User ID.
	 *
	 * @return ccUID
	 */
	@Pattern(CCUID_PATTERN)
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
	public @NotNull ProfileType getProfile() {
		return profile;
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
	 * Gets the user's profile picture URL.
	 *
	 * @return profile picture URL
	 */
	public @NotNull String getImage() {
		return image;
	}
}
