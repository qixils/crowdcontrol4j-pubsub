package live.crowdcontrol.cc4j.util;

import live.crowdcontrol.cc4j.CCEventType;
import live.crowdcontrol.cc4j.CrowdControl;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Stream;

/**
 * Manages the dispatching and invoking of events.
 */
public final class EventManager {
	private static final @NotNull Logger log = LoggerFactory.getLogger(EventManager.class);
	public static int CATCH_UP_DEFAULT = -1;
	public static int RECORD_LIMIT = 100;
	private final @NotNull List<EventRecord<?>> records = new ArrayList<>(RECORD_LIMIT);
	private final @NotNull Map<CCEventType<?>, List<Consumer<?>>> listeners = new HashMap<>();
	private final @NotNull CrowdControl parent;

	/**
	 * Create an EventManager.
	 *
	 * @param parent Crowd Control instance
	 */
	public EventManager(@NotNull CrowdControl parent) {
		this.parent = parent;
	}

	@SuppressWarnings("unchecked")
	private <T> @NotNull Stream<@NotNull EventRecord<T>> getRecords(@NotNull CCEventType<T> event, int catchUpPeriod) {
		if (catchUpPeriod == 0) return Stream.empty();
		return records.stream()
			.flatMap(record -> event.equals(record.getEventType()) ? Stream.of((EventRecord<T>) record) : Stream.empty())
			.peek(record -> log.info("Found record {} for event {}", record, event))
			.filter(record -> catchUpPeriod == -1 || !Instant.now().minusSeconds(catchUpPeriod).isBefore(record.getTriggeredAt()));
	}

	private <T> void invoke(@NotNull EventRecord<T> record, @NotNull Consumer<T> listener) {
		parent.getEventPool().submit(() -> {
			try {
				listener.accept(record.getEventBody());
			} catch (Exception e) {
				log.error("Failed to dispatch event {} to listener {}", record.getEventType(), listener.getClass().getSimpleName(), e);
			}
		});
	}

	@SuppressWarnings("unchecked")
	private <T> void _dispatch(@NotNull CCEventType<T> event, @Nullable T body) {
		EventRecord<T> record = new EventRecord<>(event, body);

		List<Consumer<T>> eventListeners = (List<Consumer<T>>) (Object) listeners.get(event);
		if (eventListeners != null) {
			for (Consumer<T> listener : eventListeners) {
				invoke(record, listener);
			}
		}

		while (records.size() >= RECORD_LIMIT)
			records.remove(0);

		records.add(record);
	}

	/**
	 * Dispatches an event to its listeners and stores it in a temporary log.
	 *
	 * @param event event type
	 * @param body body to pass onto listeners
	 */
	public <T> void dispatch(@NotNull CCEventType<T> event, @NotNull T body) {
		_dispatch(event, body);
	}

	/**
	 * Dispatches a Void event to its listeners and stores it in a temporary log.
	 *
	 * @param event event type
	 */
	public void dispatch(@NotNull CCEventType<Void> event) {
		_dispatch(event, null);
	}

	/**
	 * Registers a listener to be called as appropriate.
	 * <p>
	 * {@code catchUpPeriod} determines the time in seconds in which to look for recently sent events
	 * to catch the listener up on events it just missed.
	 * Set to 0 to disable, or -1 to fetch all records (within the {@value RECORD_LIMIT} most recent).
	 *
	 * @param event the type of event to register for
	 * @param listener the function to call as necessary
	 * @param catchUpPeriod duration in seconds or -1
	 */
	public <T> void registerEventConsumer(@NotNull CCEventType<T> event, @NotNull Consumer<T> listener, int catchUpPeriod) {
		listeners.computeIfAbsent(event, $ -> new ArrayList<>()).add(listener);
		getRecords(event, catchUpPeriod).forEachOrdered(record -> invoke(record, listener));
	}

	/**
	 * Registers a listener to be called as appropriate.
	 * <p>
	 * If this event has recently been dispatched, then your listener will immediately be invoked.
	 * See {@link #registerEventConsumer(CCEventType, Consumer, int)} for more information on the catch-up period.
	 *
	 * @param event the type of event to register for
	 * @param listener the function to call as necessary
	 */
	public <T> void registerEventConsumer(@NotNull CCEventType<T> event, @NotNull Consumer<T> listener) {
		registerEventConsumer(event, listener, CATCH_UP_DEFAULT);
	}

	/**
	 * Registers a listener to be called as appropriate.
	 * <p>
	 * {@code catchUpPeriod} determines the time in seconds in which to look for recently sent events
	 * to catch the listener up on events it just missed.
	 * Set to 0 to disable, or -1 to fetch all records (within the {@value RECORD_LIMIT} most recent).
	 *
	 * @param event the type of event to register for
	 * @param listener the function to call as necessary
	 * @param catchUpPeriod duration in seconds or -1
	 */
	public void registerEventRunnable(@NotNull CCEventType<?> event, @NotNull Runnable listener, int catchUpPeriod) {
		registerEventConsumer(event, $ -> listener.run(), catchUpPeriod);
	}

	/**
	 * Registers a listener to be called as appropriate.
	 * <p>
	 * If this event has recently been dispatched, then your listener will immediately be invoked.
	 * See {@link #registerEventRunnable(CCEventType, Runnable, int)} for more information on the catch-up period.
	 *
	 * @param event the type of event to register for
	 * @param listener the function to call as necessary
	 */
	public void registerEventRunnable(@NotNull CCEventType<?> event, @NotNull Runnable listener) {
		registerEventRunnable(event, listener, CATCH_UP_DEFAULT);
	}
}
