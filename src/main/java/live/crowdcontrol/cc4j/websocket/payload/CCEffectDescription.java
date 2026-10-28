package live.crowdcontrol.cc4j.websocket.payload;

import live.crowdcontrol.cc4j.websocket.http.CustomEffectDuration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Describes the metadata of an effect.
 */
public class CCEffectDescription extends CCBaseEffectDescription {
	protected final @NotNull String effectID;
	protected final @NotNull String type;

	public CCEffectDescription(@NotNull String effectID,
								   @NotNull String type,
								   @NotNull CCName name,
								   @Nullable String image,
								   @Nullable String note,
								   @Nullable String description,
								   boolean disabled,
								   boolean isNew,
								   boolean inactive,
								   boolean admin,
								   boolean hidden,
								   boolean unavailable,
								   @Nullable List<String> category,
								   @Nullable List<String> group,
								   @Nullable List<String> tags,
								   @Nullable CustomEffectDuration duration) {
		super(name, image, note, description, disabled, isNew, inactive, admin, hidden, unavailable, category, group, tags, duration);
		this.effectID = effectID;
		this.type = type;
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

	@Override
	public String toString() {
		return "CCEffectDescription{" +
			"effectID='" + effectID + '\'' +
			", type='" + type + '\'' +
			", name=" + name +
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
