package live.crowdcontrol.cc4j;

import com.fasterxml.jackson.core.type.TypeReference;
import live.crowdcontrol.cc4j.util.HttpUtil;
import live.crowdcontrol.cc4j.websocket.ConnectedPlayer;
import live.crowdcontrol.cc4j.websocket.data.CCEffectResponse;
import live.crowdcontrol.cc4j.websocket.data.CCInstantEffectResponse;
import live.crowdcontrol.cc4j.websocket.data.CCTimedEffectResponse;
import live.crowdcontrol.cc4j.websocket.data.ResponseStatus;
import live.crowdcontrol.cc4j.websocket.http.GamePack;
import live.crowdcontrol.cc4j.websocket.payload.PublicEffectPayload;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static live.crowdcontrol.cc4j.CCEffect.EFFECT_ID_PATTERN;

// todo javadocs
public class CrowdControl {
	/**
	 * Amount of time in seconds that effects are allowed to execute for.
	 */
	public static final int QUEUE_DURATION = 60;
	private static final @NotNull Logger log = LoggerFactory.getLogger(CrowdControl.class);
	protected final @NotNull Map<String, Supplier<CCEffect>> effects = new HashMap<>();
	protected final @NotNull Map<UUID, ConnectedPlayer> players = new HashMap<>();
	final @NotNull Map<UUID, ActiveEffect> pendingRequests = new HashMap<>();
	final @NotNull Map<UUID, ActiveEffect> timedRequests = new HashMap<>();
	protected final @NotNull ExecutorService effectPool = Executors.newCachedThreadPool();
	protected final @NotNull ScheduledExecutorService timedEffectPool = Executors.newScheduledThreadPool(20);
	protected final @NotNull ExecutorService eventPool = Executors.newCachedThreadPool();
	protected final @NotNull HttpUtil httpUtil = new HttpUtil(this);
	protected final @NotNull String gameId;
	protected final @NotNull String gamePackId;
	protected final @NotNull Path dataFolder;
	protected @Nullable GamePack gamePack;

	public CrowdControl(@NotNull String gameId,
						@NotNull String gamePackId,
						@NotNull Path dataFolder) {
		this.gameId = gameId;
		this.gamePackId = gamePackId;
		this.dataFolder = dataFolder;
		loadGamePack();
	}

	/**
	 * Gets the ID of this game's Crowd Control metadata.
	 *
	 * @return gameID
	 */
	public @NotNull String getGameId() {
		return gameId;
	}

	/**
	 * Gets the ID of this game's Crowd Control pack.
	 *
	 * @return gamePackID
	 */
	public @NotNull String getGamePackId() {
		return gamePackId;
	}

	/**
	 * Gets the folder in which players' Crowd Control tokens are stored.
	 *
	 * @return data folder
	 */
	@NotNull
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

	/**
	 * Gets the utility for making requests to Crowd Control's HTTP servers.
	 *
	 * @return http util
	 */
	public @NotNull HttpUtil getHttpUtil() {
		return httpUtil;
	}

	/**
	 * Gets the data about this game pack.
	 * May be missing if the game IDs are invalid, or it hasn't finished loading yet.
	 *
	 * @return game pack
	 */
	public @Nullable GamePack getGamePack() {
		return gamePack;
	}

	/**
	 * Re-fetches the {@link #getGamePack() game pack}.
	 */
	public void loadGamePack() {
		httpUtil.apiGet(String.format("/games/%s/packs", gameId), new TypeReference<List<GamePack>>() {
		}, null).thenAcceptAsync(gamePacks -> {
			if (gamePacks == null) return;
			for (GamePack gamePack : gamePacks) {
				if (!gamePack.getGamePackId().equalsIgnoreCase(gamePackId)) continue;
				this.gamePack = gamePack;
				return;
			}
		}, effectPool);
	}

	/**
	 * Gets a registered player by the provided unique ID.
	 *
	 * @param playerId unique player id
	 * @return registered player or null
	 */
	@Nullable
	public CCPlayer getPlayer(@NotNull UUID playerId) {
		return players.get(playerId);
	}

	/**
	 * Registers a player by the provided unique ID.
	 *
	 * @param playerId unique player id
	 * @return crowd control player
	 */
	@NotNull
	public CCPlayer addPlayer(@NotNull UUID playerId) {
		CCPlayer existing = getPlayer(playerId);
		if (existing != null) {
			log.warn("Asked to add player {} with existing connection", playerId);
			return existing;
		}
		ConnectedPlayer player = new ConnectedPlayer(playerId, this);
		player.getEventManager().registerEventConsumer(CCEventType.EFFECT_RESPONSE, response -> handleEffectResponse(response, player));
		player.connect();
		players.put(playerId, player);
		return player;
	}

	/**
	 * Removes a registered player by the provided unique ID.
	 *
	 * @param playerId unique player id
	 * @return whether a player was removed
	 */
	public boolean removePlayer(@NotNull UUID playerId) {
		ConnectedPlayer existing = players.remove(playerId);
		if (existing == null) return false;
		existing.stopSession();
		existing.close();
		return true;
	}

	/**
	 * Gets all the currently registered players.
	 * The returned collection is not a view; changes to it will not be reflected.
	 *
	 * @return players
	 */
	@NotNull
	public List<CCPlayer> getPlayers() {
		return new ArrayList<>(players.values());
	}

	/**
	 * Gets the list of players logged in as the specified user ID.
	 *
	 * @param ccUID Crowd Control user IDs
	 * @return game player ids
	 */
	@NotNull
	public Set<UUID> getPlayerIds(@NotNull String ccUID) {
		return getPlayers().stream()
			.filter(player -> player.getUserToken() != null && player.getUserToken().getId().equalsIgnoreCase(ccUID))
			.map(CCPlayer::getUuid)
			.collect(Collectors.toSet());
	}


	/**
	 * Registers an effect which maintains one object across its lifetime.
	 *
	 * @param effectID ID of the effect
	 * @param effect   executor object
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

	/**
	 * Executes the provided effect.
	 *
	 * @param payload info about the effect
	 * @param source  player who spawned the effect
	 */
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

		CCEffect ccEffect;
		try {
			ccEffect = supplier.get();
		} catch (Exception e) {
			log.error("Failed to load effect {}", effectID, e);
			source.sendResponse(new CCInstantEffectResponse(
				payload.getRequestId(),
				ResponseStatus.FAIL_PERMANENT,
				"Effect could not be loaded"
			));
			return;
		}

		ActiveEffect effect = new ActiveEffect(this, ccEffect, payload, source);
		pendingRequests.put(payload.getRequestId(), effect);

		CompletableFuture<Void> responseFuture = new CompletableFuture<>();
		effect.setResponseFuture(responseFuture);

		Future<?> responseThread = effectPool.submit(() -> {
			try {
				ccEffect.onTrigger(payload, source);
				responseFuture.complete(null);
			} catch (Exception e) {
				if (Thread.interrupted()) {
					log.warn("Effect {} cancelled", effectID);
					responseFuture.complete(null);
					// assume interrupter is sending a response
					return;
				}
				log.error("Failed to invoke effect {}", effectID, e);
				source.sendResponse(new CCInstantEffectResponse(
					payload.getRequestId(),
					ResponseStatus.FAIL_TEMPORARY,
					"Effect experienced an unknown error"
				));
				responseFuture.completeExceptionally(e);
			}
		});
		effect.setResponseThread(responseThread);

		ScheduledFuture<?> responseTimeout = timedEffectPool.schedule(
			() -> cancel(effect, "Timed out"),
			QUEUE_DURATION,
			TimeUnit.SECONDS
		);
		effect.setResponseTimeout(responseTimeout);

		responseFuture.handleAsync((result, e) -> {
			if (e != null)
				log.error("Failed to await effect {}", effectID, e);
			return null;
		}, effectPool);
	}

	protected void handleEffectResponse(@NotNull CCEffectResponse response, @NotNull ConnectedPlayer source) {
		if (response.getStatus() == ResponseStatus.TIMED_END) {
			timedRequests.remove(response.getRequestId());
			return;
		}

		if (!response.getStatus().isTerminating()) return;

		ActiveEffect effect = pendingRequests.remove(response.getRequestId());
		if (effect == null) return; // this is a further response

		// Kill timeout task
		ScheduledFuture<?> responseTimeout = effect.getResponseTimeout();
		if (responseTimeout != null) responseTimeout.cancel(false);

		if (response.getStatus() != ResponseStatus.TIMED_BEGIN) return;
		if (!(response instanceof CCTimedEffectResponse)) return;
		CCTimedEffectResponse timedResponse = (CCTimedEffectResponse) response;

		// Start timed effect!
		timedRequests.put(response.getRequestId(), effect);
		effect.scheduleCompleter(timedResponse.getTimeRemaining());
	}

	private void cancel(ActiveEffect effect, String message) {
		pendingRequests.remove(effect.getPayload().getRequestId());

		effect.getPlayer().sendResponse(new CCInstantEffectResponse(
			effect.getPayload().getRequestId(),
			ResponseStatus.FAIL_TEMPORARY,
			message
		));

		CompletableFuture<Void> responseFuture = effect.getResponseFuture();
		if (responseFuture != null) responseFuture.complete(null);

		Future<?> responseThread = effect.getResponseThread();
		if (responseThread != null) responseThread.cancel(true);

		ScheduledFuture<?> responseTimeout = effect.getResponseTimeout();
		if (responseTimeout != null) responseTimeout.cancel(false);
	}

	/**
	 * Cancels a request given its ID.
	 *
	 * @param requestId request id
	 */
	public void cancelByRequestId(@NotNull UUID requestId) {
		ActiveEffect effect = pendingRequests.remove(requestId);
		if (effect != null) {
			cancel(effect, "Effect paused before execution");
			return;
		}

		effect = timedRequests.get(requestId);
		if (effect == null) return;

		// TODO
	}

	/**
	 * Pauses a request given its ID.
	 *
	 * @param requestId request id
	 */
	public void pauseByRequestId(@NotNull UUID requestId) {
		ActiveEffect effect = pendingRequests.remove(requestId);
		if (effect != null) {
			cancel(effect, "Effect paused before execution");
			return;
		}

		effect = timedRequests.get(requestId);
		if (effect == null) return;

		effect.pause();
	}

	/**
	 * Pauses all requests, and cancels all pending requests.
	 */
	public void pauseAll() {
		// arraylist protects against CME
		new ArrayList<>(pendingRequests.values()).forEach(effect -> cancel(effect, "All pending effects were requested to be stopped"));
		timedRequests.values().forEach(ActiveEffect::pause);
	}

	/**
	 * Resumes a request given its ID.
	 *
	 * @param requestId request id
	 */
	public void resumeByRequestId(@NotNull UUID requestId) {
		ActiveEffect effect = timedRequests.get(requestId);
		if (effect == null) return;

		effect.resume();
	}

	/**
	 * Resumes all requests.
	 */
	public void resumeAll() {
		timedRequests.values().forEach(ActiveEffect::resume);
	}

	/**
	 * Returns whether a request by the given timed effect ID is active.
	 * Paused effects are considered active.
	 *
	 * @param effectId effect id
	 * @param playerId player id
	 * @return is effect active
	 */
	public boolean isPlayerEffectActive(@NotNull String effectId, @NotNull UUID playerId) {
		return timedRequests.values().stream().anyMatch(effect -> effect.getPlayer().getUuid().equals(playerId) && effect.getPayload().getEffect().getEffectId().equals(effectId));
	}
}
