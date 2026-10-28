package live.crowdcontrol.cc4j.websocket.http;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public final class PutCustomEffectsData {
	private final @NotNull String gamePackID;
	private final @NotNull List<CustomEffectsOperation> operations;

	@JsonCreator
	public PutCustomEffectsData(
		@JsonProperty("gamePackID") @NotNull String gamePackID,
		@JsonProperty("operations") @NotNull List<CustomEffectsOperation> operations
	) {
		this.gamePackID = gamePackID;
		this.operations = operations;
	}

	public @NotNull String gamePackID() {
		return gamePackID;
	}

	public @NotNull List<CustomEffectsOperation> operations() {
		return operations;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) return true;
		if (obj == null || obj.getClass() != this.getClass()) return false;
		PutCustomEffectsData that = (PutCustomEffectsData) obj;
		return Objects.equals(this.gamePackID, that.gamePackID) &&
			Objects.equals(this.operations, that.operations);
	}

	@Override
	public int hashCode() {
		return Objects.hash(gamePackID, operations);
	}

	@Override
	public String toString() {
		return "PutCustomEffectsData[" +
			"gamePackID=" + gamePackID + ", " +
			"operations=" + operations + ']';
	}

}
