package live.crowdcontrol.cc4j;

import java.util.Objects;

public final class CCMessage {
	private final Level level;
	private final String message;

	public CCMessage(Level level, String message) {
		this.level = level;
		this.message = message;
	}

	public Level level() {
		return level;
	}

	public String message() {
		return message;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) return true;
		if (obj == null || obj.getClass() != this.getClass()) return false;
		CCMessage that = (CCMessage) obj;
		return Objects.equals(this.level, that.level) &&
			Objects.equals(this.message, that.message);
	}

	@Override
	public int hashCode() {
		return Objects.hash(level, message);
	}

	@Override
	public String toString() {
		return "CCMessage[" +
			"level=" + level + ", " +
			"message=" + message + ']';
	}

	public enum Level {
		DEBUG,
		INFO,
		WARN,
		ERROR;

		public boolean isAtLeast(Level configuredLevel) {
			return compareTo(configuredLevel) >= 0;
		}
	}
}
