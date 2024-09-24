package live.crowdcontrol.cc4j.websocket.payload;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Describes the metadata of an effect.
 */
public class CCEffectDescription {
	protected @NotNull String effectID;
	protected @NotNull String type;
	protected @NotNull CCName name;
	protected @Nullable String image;
	protected @Nullable String note;
	protected @Nullable String description;
	protected boolean disabled;
	@JsonProperty("new")
	protected boolean isNew;
	protected boolean inactive;
	protected boolean admin;
	protected boolean hidden;
	protected boolean unavailable;
	protected @Nullable List<@NotNull String> category;
	protected @Nullable List<@NotNull String> group;
	protected @Nullable List<@NotNull String> tags;
	protected int duration;
	// TODO tiktok
	// TODO sessionCooldown
	// TODO userCooldown
	// TODO scale

	CCEffectDescription() {
	}

	// boring getters

	/**
	 * The unique ID of the effect.
	 *
	 * @return effectID
	 */
	public @NotNull String getEffectId() {
		return effectID;
	}

	/**
	 * Gets the type of the effect.
	 * Typically {@code game}.
	 *
	 * @return effect type
	 */
	public @NotNull String getType() {
		return type;
	}

	/**
	 * Gets the display name of the effect.
	 *
	 * @return display name
	 * @see #getNote()
	 */
	public @NotNull CCName getName() {
		return name;
	}

	/**
	 * Gets the filename of the image.
	 * The full image URL is determined using:
	 * {@code https://resources.crowdcontrol.live/images/{GAME_ID}/{GAME_PACK_ID}/icons/{EFFECT_IMAGE ?? EFFECT_ID}.png}
	 *
	 * @return image filename override
	 */
	public @Nullable String getImage() {
		return image;
	}

	/**
	 * Gets the note to be displayed next to the {@link #getName() effect name}.
	 *
	 * @return clarifying note
	 */
	public @Nullable String getNote() {
		return note;
	}

	/**
	 * Gets the description of the effect.
	 * It describes what is expected to happen.
	 *
	 * @return effect description
	 */
	public @Nullable String getDescription() {
		return description;
	}

	/**
	 * Whether the effect has been temporarily removed by Crowd Control staff due to instability.
	 *
	 * @return is disabled
	 */
	public boolean isDisabled() {
		return disabled;
	}

	/**
	 * Whether the effect has been added in a recent update.
	 *
	 * @return is new
	 */
	public boolean isNew() {
		return isNew;
	}

	/**
	 * Whether the effect has been hidden by the streamer.
	 *
	 * @return is inactive
	 */
	public boolean isInactive() {
		return inactive;
	}

	/**
	 * Whether the effect is hidden from viewers,
	 * only allowing it to be used by streamers.
	 * Typically used for repairing broken game states.
	 *
	 * @return is admin-only
	 */
	public boolean isAdminOnly() {
		return admin;
	}

	/**
	 * Whether the effect is currently hidden by the game.
	 *
	 * @return is hidden
	 */
	public boolean isHidden() {
		return hidden;
	}

	/**
	 * Whether the effect is currently available for purchase, as dictated by the game.
	 *
	 * @return is unavailable
	 */
	public boolean isUnavailable() {
		return unavailable;
	}

	/**
	 * Gets the effect's categories.
	 * These are displayed to the viewer and streamer and used to narrow down their list of visible effects.
	 *
	 * @return groups
	 */
	public @Nullable List<String> getCategories() {
		return category;
	}

	/**
	 * Gets the effect's groups.
	 * Groups are used to connect effects together so the pack can change multiple effects at once.
	 * This is used for hiding effects or making multiple effects unavailable at once.
	 *
	 * @return groups
	 */
	public @Nullable List<String> getGroups() {
		return group;
	}

	/**
	 * Unused.
	 *
	 * @return tags
	 */
	@Deprecated
	public @Nullable List<@NotNull String> getTags() {
		return tags;
	}

	/**
	 * The duration of the effect in seconds.
	 * 0 if non-timed.
	 *
	 * @return duration in seconds
	 */
	public int getDuration() {
		return duration;
	}
}
