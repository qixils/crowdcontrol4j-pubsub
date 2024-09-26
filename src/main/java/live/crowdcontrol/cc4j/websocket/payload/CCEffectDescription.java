package live.crowdcontrol.cc4j.websocket.payload;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Describes the metadata of an effect.
 */
public class CCEffectDescription extends CCBaseEffectDescription {
	protected final @NotNull String effectID;
	protected final @NotNull String type;

	@JsonCreator
	public CCEffectDescription(@JsonProperty("effectID") @NotNull String effectID,
								   @JsonProperty("type") @NotNull String type,
								   @JsonProperty("name") @NotNull CCName name,
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
}
