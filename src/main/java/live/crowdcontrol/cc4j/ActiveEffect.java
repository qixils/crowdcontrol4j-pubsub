package live.crowdcontrol.cc4j;

import live.crowdcontrol.cc4j.websocket.ConnectedPlayer;
import live.crowdcontrol.cc4j.websocket.data.CCInstantEffectResponse;
import live.crowdcontrol.cc4j.websocket.data.CCTimedEffectResponse;
import live.crowdcontrol.cc4j.websocket.data.ResponseStatus;
import live.crowdcontrol.cc4j.websocket.payload.PublicEffectPayload;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * Util denoting relevant information about pending effects and active timed effects.
 */
final class ActiveEffect {
	private static final Logger log = LoggerFactory.getLogger(ActiveEffect.class);
	private final @NotNull CrowdControl cc;
	private final @NotNull CCEffect effect;
	private final @NotNull PublicEffectPayload payload;
	private final @NotNull ConnectedPlayer player;
	private @Nullable ScheduledFuture<?> completer;
	private @Nullable Instant startedAt;
	private long timeRemaining = -1;
	private @Nullable CompletableFuture<Void> responseFuture;
	private @Nullable Future<?> responseThread;
	private @Nullable ScheduledFuture<?> responseTimeout;

	public ActiveEffect(@NotNull CrowdControl cc,
						@NotNull CCEffect effect,
						@NotNull PublicEffectPayload payload,
						@NotNull ConnectedPlayer player) {
		this.cc = cc;
		this.effect = effect;
		this.payload = payload;
		this.player = player;
	}

	public @NotNull PublicEffectPayload getPayload() {
		return payload;
	}

	public @NotNull ConnectedPlayer getPlayer() {
		return player;
	}


	public @Nullable Future<?> getCompleter() {
		return completer;
	}

	public void scheduleCompleter(long timeRemaining) {
		this.timeRemaining = timeRemaining;
		setCompleter(cc.getTimedEffectPool().schedule(() -> {
			player.sendResponse(new CCInstantEffectResponse(
				payload.getRequestId(),
				ResponseStatus.TIMED_END
			));

			if (!(effect instanceof CCTimedEffect)) return;
			try {
				((CCTimedEffect) effect).onEnd(payload, player);
			} catch (Exception e) {
				log.error("Failed to invoke {} end handler for request {}", payload.getEffect().getEffectId(), payload.getRequestId());
			}
		}, timeRemaining, TimeUnit.MILLISECONDS));
	}

	private void setCompleter(@Nullable ScheduledFuture<?> completer) {
		if (this.completer != null) {
			this.completer.cancel(false);
		}
		this.completer = completer;
		this.startedAt = completer != null
			? Instant.now()
			: null;
	}

	public void pause() {
		if (startedAt == null) return;

		this.timeRemaining = completer != null
			? Math.max(0, completer.getDelay(TimeUnit.MILLISECONDS))
			: 0;
		setCompleter(null);

		player.sendResponse(new CCTimedEffectResponse(
			payload.getRequestId(),
			ResponseStatus.TIMED_PAUSE,
			timeRemaining
		));

		if (!(effect instanceof CCTimedEffect)) return;
		try {
			((CCTimedEffect) effect).onPause(payload, player);
		} catch (Exception e) {
			log.error("Failed to invoke {} pause handler for request {}", payload.getEffect().getEffectId(), payload.getRequestId());
		}
	}

	public void resume() {
		if (timeRemaining <= 0) return;

		scheduleCompleter(timeRemaining);

		player.sendResponse(new CCTimedEffectResponse(
			payload.getRequestId(),
			ResponseStatus.TIMED_RESUME,
			timeRemaining
		));

		if (!(effect instanceof CCTimedEffect)) return;
		try {
			((CCTimedEffect) effect).onResume(payload, player);
		} catch (Exception e) {
			log.error("Failed to invoke {} resume handler for request {}", payload.getEffect().getEffectId(), payload.getRequestId());
		}
	}

	public @Nullable CompletableFuture<Void> getResponseFuture() {
		return responseFuture;
	}

	public void setResponseFuture(@Nullable CompletableFuture<Void> responseFuture) {
		this.responseFuture = responseFuture;
	}

	public @Nullable Future<?> getResponseThread() {
		return responseThread;
	}

	public void setResponseThread(@Nullable Future<?> responseThread) {
		this.responseThread = responseThread;
	}

	public @Nullable ScheduledFuture<?> getResponseTimeout() {
		return responseTimeout;
	}

	public void setResponseTimeout(@Nullable ScheduledFuture<?> responseTimeout) {
		this.responseTimeout = responseTimeout;
	}

	public @NotNull CCEffect getEffect() {
		return effect;
	}
}
