package live.crowdcontrol.cc4j;

import live.crowdcontrol.cc4j.websocket.ConnectedPlayer;
import live.crowdcontrol.cc4j.websocket.data.CCEffectResponse;
import live.crowdcontrol.cc4j.websocket.data.CCInstantEffectResponse;
import live.crowdcontrol.cc4j.websocket.data.ResponseStatus;
import live.crowdcontrol.cc4j.websocket.payload.PublicEffectPayload;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

import static live.crowdcontrol.cc4j.CCEffect.EFFECT_ID_PATTERN;

public class CrowdControl {
	/**
	 * Amount of time in seconds that effects are allowed to execute for.
	 */
	public static final int QUEUE_DURATION = 60;
	private static final Logger log = LoggerFactory.getLogger(CrowdControl.class);
	protected final Map<String, Supplier<CCEffect>> effects = new HashMap<>();
	protected final Map<UUID, ConnectedPlayer> players = new HashMap<>();
	protected final ExecutorService effectPool = Executors.newCachedThreadPool();
	protected final ScheduledExecutorService timedEffectPool = Executors.newScheduledThreadPool(20);
	protected final ExecutorService eventPool = Executors.newCachedThreadPool();
	protected final Path dataFolder;
	protected AtomicInteger runningEffects;

	public CrowdControl(@NotNull Path dataFolder) {
		this.dataFolder = dataFolder;
	}

	/**
	 * Gets the folder in which players' Crowd Control tokens are stored.
	 *
	 * @return data folder
	 */
	public Path getDataFolder() {
		return dataFolder;
	}

	/**
	 * Gets the executor service on which effects are to be run.
	 *
	 * @return executor service
	 */
	public @NotNull ExecutorService getEffectPool() {
		return effectPool;
	}

	/**
	 * Gets the executor service on which timed effect updates are to be run.
	 *
	 * @return scheduled executor service
	 */
	public @NotNull ScheduledExecutorService getTimedEffectPool() {
		return timedEffectPool;
	}

	/**
	 * Gets the executor service on which events are to be run.
	 *
	 * @return executor service
	 */
	public @NotNull ExecutorService getEventPool() {
		return eventPool;
	}

	@Nullable
	public CCPlayer getPlayer(@NotNull UUID playerId) {
		return players.get(playerId);
	}

	@NotNull
	public CCPlayer addPlayer(@NotNull UUID playerId) {
		CCPlayer existing = getPlayer(playerId);
		if (existing != null) {
			log.warn("Asked to add player {} with existing connection", playerId);
			return existing;
		}
		ConnectedPlayer player = new ConnectedPlayer(playerId, this);
		player.connect();
		players.put(playerId, player);
		return player;
	}

	public boolean removePlayer(@NotNull UUID playerId) {
		ConnectedPlayer existing = players.remove(playerId);
		if (existing == null) return false;
		if (!existing.isClosed())
			existing.close();
		return true;
	}

	/**
	 * Registers an effect which maintains one object across its lifetime.
	 *
	 * @param effectID ID of the effect
	 * @param effect executor object
	 * @return whether the effect was added successfully
	 */
	public boolean addEffect(@NotNull String effectID, @NotNull CCEffect effect) {
		return addEffect(effectID, () -> effect);
	}

	/**
	 * Registers an effect which is instantiated upon triggering.
	 *
	 * @param effectID ID of the effect
	 * @param supplier executor supplier
	 * @return whether the effect was added successfully
	 */
	public boolean addEffect(@NotNull String effectID, @NotNull Supplier<@NotNull CCEffect> supplier) {
		if (!effectID.matches(EFFECT_ID_PATTERN)) {
			log.error("Effect ID {} should match pattern {}", effectID, EFFECT_ID_PATTERN);
			return false;
		}
		if (effects.containsKey(effectID)) {
			log.error("Effect ID {} is already registered", effectID);
			return false;
		}
		effects.put(effectID, supplier);
		return true;
	}

	public void executeEffect(@NotNull PublicEffectPayload payload, @NotNull ConnectedPlayer source) {
		String effectID = payload.getEffect().getEffectId();
		Supplier<CCEffect> supplier = effects.get(effectID);
		if (supplier == null) {
			log.error("Cannot execute unknown effect {}", effectID);
			source.sendResponse(new CCInstantEffectResponse(
				payload.getRequestId(),
				ResponseStatus.FAIL_PERMANENT,
				"Unknown Effect"
			));
			return;
		}

		// TODO: TIMEd EFECTS
		// TODO: game could return null and then not emit an event

		CompletableFuture<CCEffectResponse> future = CompletableFuture.supplyAsync(() -> {
			try {
				return supplier.get().onTrigger(payload, source);
			} catch (Exception e) {
				log.error("Failed to invoke effect {}", effectID, e);
				source.sendResponse(new CCInstantEffectResponse(
					payload.getRequestId(),
					ResponseStatus.FAIL_TEMPORARY,
					"Effect experienced an unknown error"
				));
				return null;
			}
		}, effectPool);

		ScheduledFuture<?> timeout = timedEffectPool.schedule(() -> future.cancel(true), QUEUE_DURATION, TimeUnit.SECONDS);

		future.handleAsync((result, e) -> {
			if (e != null) {
				log.error("Failed to await effect {}", effectID, e);
			}
			else if (result != null) {
				source.sendResponse(result);
			}
			timeout.cancel(false);
			return null;
		}, effectPool);
	}
}
