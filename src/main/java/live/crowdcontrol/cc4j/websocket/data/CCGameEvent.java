package live.crowdcontrol.cc4j.websocket.data;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
	@JsonCreator
    public CCGameEvent(@JsonProperty("id") @NotNull String id,
                       @JsonProperty("args") @Nullable Map<@NotNull String, @NotNull Object> args) {
		this.id = id;
		this.args = (args == null || args.isEmpty()) ? null : Map.copyOf(args);
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
