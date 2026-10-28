package live.crowdcontrol.cc4j.websocket.http;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

public final class PutCustomEffectsData {
	private final @NotNull String gamePackID;
	private final @NotNull List<CustomEffectsOperation> operations;

	public PutCustomEffectsData(
		@NotNull String gamePackID,
		@NotNull List<CustomEffectsOperation> operations
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
