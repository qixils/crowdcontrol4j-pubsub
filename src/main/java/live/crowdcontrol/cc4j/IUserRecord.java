package live.crowdcontrol.cc4j;

import live.crowdcontrol.cc4j.websocket.payload.ProfileType;
import org.intellij.lang.annotations.Pattern;
import org.jetbrains.annotations.NotNull;

import static live.crowdcontrol.cc4j.websocket.UserToken.CCUID_PATTERN;

public interface IUserRecord {
	/**
	 * Gets the user's Crowd Control User ID.
	 *
	 * @return ccUID
	 */
	@Pattern(CCUID_PATTERN)
	@NotNull String getId();

	/**
	 * Gets the user's display name.
	 *
	 * @return display name
	 */
	@NotNull String getName();

	/**
	 * Gets the type of profile the user logged in with.
	 *
	 * @return profile type
	 */
	@NotNull ProfileType getProfile();

	/**
	 * Gets the ID of the user on their {@link #getProfile() home platform}.
	 *
	 * @return origin id
	 */
	@NotNull String getOriginId();
}
