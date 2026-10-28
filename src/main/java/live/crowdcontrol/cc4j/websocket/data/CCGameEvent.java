package live.crowdcontrol.cc4j.websocket.data;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Informs the server of a notable event that has occurred in the game.
 */
public class CCGameEvent {
	protected final @NotNull String id;
	protected final @Nullable Map<@NotNull String, @NotNull Object> args;

	/**
	 * Creates an event.
	 *
	 * @param id   Event internal ID
	 * @param args Arguments if applicable
	 */
    public CCGameEvent(@NotNull String id,
                       @Nullable Map<@NotNull String, @NotNull Object> args) {
		this.id = id;
		this.args = (args == null || args.isEmpty()) ? null : Collections.unmodifiableMap(new HashMap<>(args));
	}

	/**
	 * Gets the unique internal ID of the event.
	 *
	 * @return event ID
	 */
	public @NotNull String getId() {
		return id;
	}

	/**
	 * Gets the metadata relevant to the event, if available.
	 *
	 * @return args
	 */
	public @Nullable Map<@NotNull String, @NotNull Object> getArgs() {
		return args;
	}
}
