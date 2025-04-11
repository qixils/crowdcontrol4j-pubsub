package live.crowdcontrol.cc4j.websocket.payload;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Describes the metadata of an effect.
 */
public class CCBaseEffectDescription {
	protected final @NotNull CCName name;
	protected final @Nullable String image;
	protected final @Nullable String note;
	protected final @Nullable String description;
	protected final boolean disabled;
	@JsonProperty("new")
	protected final boolean isNew;
	protected final boolean inactive;
	protected final boolean admin;
	protected final boolean hidden;
	protected final boolean unavailable;
	protected final @Nullable List<@NotNull String> category;
	protected final @Nullable List<@NotNull String> group;
	protected final @Nullable List<@NotNull String> tags;
	protected final int duration;
	// TODO tiktok
	// TODO sessionCooldown
	// TODO userCooldown
	// TODO scale

	@JsonCreator
	public CCBaseEffectDescription(@JsonProperty("name") @NotNull CCName name,
								   @JsonProperty("image") @Nullable String image,
								   @JsonProperty("note") @Nullable String note,
								   @JsonProperty("description") @Nullable String description,
								   @JsonProperty("disabled") boolean disabled,
								   @JsonProperty("new") boolean isNew,
								   @JsonProperty("inactive") boolean inactive,
								   @JsonProperty("admin") boolean admin,
								   @JsonProperty("hidden") boolean hidden,
								   @JsonProperty("unavailable") boolean unavailable,
								   @JsonProperty("category") @Nullable List<String> category,
								   @JsonProperty("group") @Nullable List<String> group,
								   @JsonProperty("tags") @Nullable List<String> tags,
								   @JsonProperty("duration") int duration) {
		this.name = name;
		this.image = image;
		this.note = note;
		this.description = description;
		this.disabled = disabled;
		this.isNew = isNew;
		this.inactive = inactive;
		this.admin = admin;
		this.hidden = hidden;
		this.unavailable = unavailable;
		this.category = category;
		this.group = group;
		this.tags = tags;
		this.duration = duration;
	}


	// boring getters

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
	@JsonProperty("admin")
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
	@JsonProperty("category")
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
	@JsonProperty("group")
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

	@Override
	public String toString() {
		return "CCBaseEffectDescription{" +
			"name=" + name +
			", image='" + image + '\'' +
			", note='" + note + '\'' +
			", description='" + description + '\'' +
			", disabled=" + disabled +
			", isNew=" + isNew +
			", inactive=" + inactive +
			", admin=" + admin +
			", hidden=" + hidden +
			", unavailable=" + unavailable +
			", category=" + category +
			", group=" + group +
			", tags=" + tags +
			", duration=" + duration +
			'}';
	}
}
