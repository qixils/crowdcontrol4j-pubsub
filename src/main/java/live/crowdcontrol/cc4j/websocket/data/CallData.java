package live.crowdcontrol.cc4j.websocket.data;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * Data describing a remote procedure to be called.
 */
public class CallData<A> {
	protected final @NotNull String type;
	protected final @NotNull UUID id;
	protected final @NotNull String method;
	protected final @NotNull List<@NotNull A> args;

	@JsonCreator
	CallData(@JsonProperty("type") @NotNull String type,
			 @JsonProperty("id") @NotNull UUID id,
			 @JsonProperty("method") @NotNull String method,
			 @JsonProperty("args") @NotNull List<A> args) {
		this.type = type;
		this.id = id;
		this.method = method;
		this.args = Collections.unmodifiableList(new ArrayList<>(args));
	}

	/**
	 * Creates a new call.
	 *
	 * @param method method to call
	 * @param args arguments to provide to the call
	 */
	public CallData(@NotNull CallDataMethod<A> method,
					@NotNull List<A> args) {
		this.type = "call";
		this.id = UUID.randomUUID();
		this.method = method.getValue();
		this.args = Collections.unmodifiableList(new ArrayList<>(args));
	}

	/**
	 * The type of the call.
	 * This is only known to be {@code call}.
	 *
	 * @return call type
	 */
	public @NotNull String getType() {
		return type;
	}

	/**
	 * Gets the randomly generated ID representing this call.
	 *
	 * @return unique ID
	 */
	public @NotNull UUID getId() {
		return id;
	}

	/**
	 * Gets the name of the method being called.
	 *
	 * @return call method
	 */
	public @NotNull String getMethod() {
		return method;
	}

	/**
	 * Gets the unmodifiable list of arguments to be provided to the remote method.
	 *
	 * @return call args
	 */
	public @NotNull List<@NotNull A> getArgs() {
		return args;
	}
}
