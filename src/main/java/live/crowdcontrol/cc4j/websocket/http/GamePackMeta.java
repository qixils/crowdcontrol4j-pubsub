package live.crowdcontrol.cc4j.websocket.http;

import com.google.gson.annotations.SerializedName;
import live.crowdcontrol.cc4j.websocket.payload.CCName;
import org.intellij.lang.annotations.Pattern;
import org.intellij.lang.annotations.Subst;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static live.crowdcontrol.cc4j.websocket.http.GamePayload.DATE_PATTERN;

public class GamePackMeta {
//   *     launcher
//	 *     mod: z
//	 *       .strictObject({
//	 *         version: z.string().optional(),
//	 *         path: z.string().optional(),
//	 *         port: z.number().optional(),
//	 *         logPath: z.string().optional(),
//	 *       })
//	 *       .optional(),
//	 *     oneClick: OneClickConfigSchema.or(z.boolean()).optional(),
//	 *     epic: GamePackEpicConfigSchema.optional(),
//	 *     steam: GamePackSteamConfigSchema.optional(),
//	 *     thunderstore: GamePackThunderStoreConfigSchema.optional(),

	private final @NotNull CCName name;
	@Subst("1970-01-01")
	@Pattern(DATE_PATTERN)
	private final @NotNull String releaseDate;
	private final @NotNull String guide;
	private final @NotNull String platform;
	private final @NotNull String visibility; // TODO: enum
	private final @NotNull List<String> connector;
	private final @Nullable String note;
	private final @Nullable String image;
	private final @Nullable String firmware;
	private final @Nullable String executable;
	private final @Nullable String process;
	private final @Nullable String description;
	private final @Nullable String lastStepMessage;
	private final @Nullable String warningMessage;
	private final @Nullable String emulator;
	private final @Nullable List<String> extraArgs;
	private final @Nullable List<GamePackFramework> frameworks;
	private final @Nullable List<GamePackDeveloper> devs;
	private final @Nullable List<GamePackVersion> supportedVersions;
	@SerializedName("new")
	private final boolean isNew;
	private final boolean proExclusive;
	private final boolean patch;
	private final boolean prelaunch;
	private final boolean recommended;
	private final boolean earlyAccess;
	private final boolean disabled;
	private final boolean newUpdate;
	private final boolean guideCheck;
	private final boolean dynamic;
	private final boolean initiateFromCrowdControl;

	public GamePackMeta(@NotNull CCName name,
						@Subst("1970-01-01") @Pattern(DATE_PATTERN) @NotNull String releaseDate,
						@NotNull String guide,
						@NotNull String platform,
						@NotNull String visibility,
						@NotNull List<String> connector,
						@Nullable String note,
						@Nullable String image,
						@Nullable String firmware,
						@Nullable String executable,
						@Nullable String process,
						@Nullable String description,
						@Nullable String lastStepMessage,
						@Nullable String warningMessage,
						@Nullable String emulator,
						@Nullable List<String> extraArgs,
						@Nullable List<GamePackFramework> frameworks,
						@Nullable List<GamePackDeveloper> devs,
						@Nullable List<GamePackVersion> supportedVersions,
						boolean isNew,
						boolean proExclusive,
						boolean patch,
						boolean prelaunch,
						boolean recommended,
						boolean earlyAccess,
						boolean disabled,
						boolean newUpdate,
						boolean guideCheck,
						boolean dynamic,
						boolean initiateFromCrowdControl) {
		this.name = name;
		this.releaseDate = releaseDate;
		this.guide = guide;
		this.platform = platform;
		this.visibility = visibility;
		this.connector = connector;
		this.note = note;
		this.image = image;
		this.firmware = firmware;
		this.executable = executable;
		this.process = process;
		this.description = description;
		this.lastStepMessage = lastStepMessage;
		this.warningMessage = warningMessage;
		this.emulator = emulator;
		this.extraArgs = extraArgs;
		this.frameworks = frameworks;
		this.devs = devs;
		this.supportedVersions = supportedVersions;
		this.isNew = isNew;
		this.proExclusive = proExclusive;
		this.patch = patch;
		this.prelaunch = prelaunch;
		this.recommended = recommended;
		this.earlyAccess = earlyAccess;
		this.disabled = disabled;
		this.newUpdate = newUpdate;
		this.guideCheck = guideCheck;
		this.dynamic = dynamic;
		this.initiateFromCrowdControl = initiateFromCrowdControl;
	}

	public @NotNull CCName getName() {
		return name;
	}

	@Subst("1970-01-01")
	@Pattern(DATE_PATTERN)
	public @NotNull String getReleaseDate() {
		return releaseDate;
	}

	public @NotNull String getGuide() {
		return guide;
	}

	public @NotNull String getPlatform() {
		return platform;
	}

	public @NotNull String getVisibility() {
		return visibility;
	}

	public @NotNull List<String> getConnector() {
		return connector;
	}

	public @Nullable String getNote() {
		return note;
	}

	public @Nullable String getImage() {
		return image;
	}

	public @Nullable String getFirmware() {
		return firmware;
	}

	public @Nullable String getExecutable() {
		return executable;
	}

	public @Nullable String getProcess() {
		return process;
	}

	public @Nullable String getDescription() {
		return description;
	}

	public @Nullable String getLastStepMessage() {
		return lastStepMessage;
	}

	public @Nullable String getWarningMessage() {
		return warningMessage;
	}

	public @Nullable String getEmulator() {
		return emulator;
	}

	public @Nullable List<String> getExtraArgs() {
		return extraArgs;
	}

	public @Nullable List<GamePackFramework> getFrameworks() {
		return frameworks;
	}

	public @Nullable List<GamePackDeveloper> getDevs() {
		return devs;
	}

	public @Nullable List<GamePackVersion> getSupportedVersions() {
		return supportedVersions;
	}

	public boolean isNew() {
		return isNew;
	}

	public boolean isProExclusive() {
		return proExclusive;
	}

	public boolean isPatch() {
		return patch;
	}

	public boolean isPrelaunch() {
		return prelaunch;
	}

	public boolean isRecommended() {
		return recommended;
	}

	public boolean isEarlyAccess() {
		return earlyAccess;
	}

	public boolean isDisabled() {
		return disabled;
	}

	public boolean isNewUpdate() {
		return newUpdate;
	}

	public boolean isGuideCheck() {
		return guideCheck;
	}

	public boolean isDynamic() {
		return dynamic;
	}

	public boolean isInitiateFromCrowdControl() {
		return initiateFromCrowdControl;
	}
}
