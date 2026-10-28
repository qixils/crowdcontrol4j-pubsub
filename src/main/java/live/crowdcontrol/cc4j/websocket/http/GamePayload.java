package live.crowdcontrol.cc4j.websocket.http;

import live.crowdcontrol.cc4j.websocket.payload.CCName;
import org.intellij.lang.annotations.Pattern;
import org.intellij.lang.annotations.RegExp;
import org.intellij.lang.annotations.Subst;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class GamePayload {
	@RegExp
	public static final String DATE_PATTERN = "^\\d{4}-\\d{2}-\\d{2}$";

	private final @NotNull String gameID;
	private final @NotNull CCName name;
	private final @NotNull List<String> platforms;
	@Subst("1970-01-01") @Pattern(DATE_PATTERN)
	private final @NotNull String releaseDate;
	private final @NotNull List<String> packs; // min 1
	private final @Nullable String title; // unknown
	private final @Nullable String description;
	private final @Nullable String image;
	private final @Nullable TwitchGameDetails twitch;
	private final @Nullable List<String> tags;
	private final @Nullable List<String> keywords;
	private final @Nullable List<GameClip> clips;
	private final boolean recommended;
	private final boolean earlyAccess;
	private final boolean newUpdate;
	private final boolean disabled;

	public GamePayload(@NotNull String gameID,
					   @NotNull CCName name,
					   @NotNull List<String> platforms,
					   @Subst("1970-01-01") @Pattern(DATE_PATTERN) @NotNull String releaseDate,
					   @NotNull List<String> packs,
					   @Nullable String title,
					   @Nullable String description,
					   @Nullable String image,
					   @Nullable TwitchGameDetails twitch,
					   @Nullable List<String> tags,
					   @Nullable List<String> keywords,
					   @Nullable List<GameClip> clips,
					   boolean recommended,
					   boolean earlyAccess,
					   boolean newUpdate,
					   boolean disabled) {
		this.gameID = gameID;
		this.name = name;
		this.platforms = platforms;
		this.releaseDate = releaseDate;
		this.packs = packs;
		this.title = title;
		this.description = description;
		this.image = image;
		this.twitch = twitch;
		this.tags = tags;
		this.keywords = keywords;
		this.clips = clips;
		this.recommended = recommended;
		this.earlyAccess = earlyAccess;
		this.newUpdate = newUpdate;
		this.disabled = disabled;
	}

	public @NotNull String getGameID() {
		return gameID;
	}

	public @NotNull CCName getName() {
		return name;
	}

	public @NotNull List<String> getPlatforms() {
		return platforms;
	}

	@Pattern(DATE_PATTERN)
	public @NotNull String getReleaseDate() {
		return releaseDate;
	}

	public @NotNull List<String> getPacks() {
		return packs;
	}

	public @Nullable String getTitle() {
		return title;
	}

	public @Nullable String getDescription() {
		return description;
	}

	public @Nullable String getImage() {
		return image;
	}

	public @Nullable TwitchGameDetails getTwitch() {
		return twitch;
	}

	public @Nullable List<String> getTags() {
		return tags;
	}

	public @Nullable List<String> getKeywords() {
		return keywords;
	}

	public @Nullable List<GameClip> getClips() {
		return clips;
	}

	public boolean isRecommended() {
		return recommended;
	}

	public boolean isEarlyAccess() {
		return earlyAccess;
	}

	public boolean isNewUpdate() {
		return newUpdate;
	}

	public boolean isDisabled() {
		return disabled;
	}
}
