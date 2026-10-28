package live.crowdcontrol.cc4j.websocket.http;

import live.crowdcontrol.cc4j.websocket.data.CCEffectReport;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Data to be encoded as JSON and sent when starting a game session.
 */
public class GameSessionStartData {
	private final @NotNull String gamePackID;
	private final @Nullable List<CCEffectReport> effectReportArgs;

	/**
	 * Creates data.
	 *
	 * @param gamePackID ID of your game pack
	 * @param effectReportArgs optional effect reports
	 */
	public GameSessionStartData(@NotNull String gamePackID,
								@Nullable List<CCEffectReport> effectReportArgs) {
		this.gamePackID = gamePackID;
		this.effectReportArgs = effectReportArgs;
	}

	/**
	 * Creates data.
	 *
	 * @param gamePackID ID of your game pack
	 */
	public GameSessionStartData(@NotNull String gamePackID) {
		this.gamePackID = gamePackID;
		this.effectReportArgs = null;
	}

	/**
	 * Gets the ID of the game pack to load.
	 *
	 * @return gamePackID
	 */
	public @NotNull String getGamePackId() {
		return gamePackID;
	}

	/**
	 * Gets the optional list of effect report statuses.
	 *
	 * @return effect reports
	 */
	public @Nullable List<CCEffectReport> getEffectReportArgs() {
		return effectReportArgs;
	}
}
