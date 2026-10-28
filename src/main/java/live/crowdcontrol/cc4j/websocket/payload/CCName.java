package live.crowdcontrol.cc4j.websocket.payload;

import com.google.gson.*;
import com.google.gson.annotations.SerializedName;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Type;

/**
 * The name of an object.
 * Encapsulates both the display name and the name for sorting.
 */
public class CCName implements Comparable<CCName> {
	@SerializedName("public")
	private @NotNull final String displayName;
	private @Nullable final String sort;

	/**
	 * Creates a name.
	 *
	 * @param displayName viewer-facing display name
	 * @param sort name for alphabetical sorting
	 */
	public CCName(@NotNull String displayName,
				  @Nullable String sort) {
		this.displayName = displayName;
		this.sort = sort;
	}

	/**
	 * Creates a name.
	 *
	 * @param displayName viewer-facing display name
	 */
	public CCName(@NotNull String displayName) {
		this(displayName, null);
	}

	/**
	 * Gets the name displayed to users.
	 *
	 * @return display name
	 */
	public @NotNull String getDisplayName() {
		return displayName;
	}

	/**
	 * Gets the name used for alphabetical sorting.
	 * If null, fall back to {@link #getDisplayName()}.
	 *
	 * @return sort name
	 */
	public @Nullable String getSortValue() {
		return sort;
	}

	/**
	 * Computes the name used for alphabetical sorting.
	 * Returns {@link #getSortValue()} if present, else {@link #getDisplayName()}.
	 *
	 * @return computed sort name
	 */
	public @NotNull String computeSortValue() {
		if (sort != null) return sort;
		return displayName;
	}

	@Override
	public int compareTo(@NotNull CCName o) {
		return computeSortValue().compareTo(o.computeSortValue());
	}

	@ApiStatus.Internal
	public static class CCNameAdapter implements JsonDeserializer<CCName> {
		@Override
		public CCName deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
			String displayName;
			String sortName = null;
			if (json.isJsonObject()) {
				JsonObject node = json.getAsJsonObject();

				displayName = node.get("public").getAsString();
				JsonElement sortNode = node.get("sort");
				sortName = sortNode != null && !sortNode.isJsonNull() ? sortNode.getAsString() : null;
			} else {
				displayName = json.getAsString();
			}
			return new CCName(displayName, sortName);
		}
	}
}
