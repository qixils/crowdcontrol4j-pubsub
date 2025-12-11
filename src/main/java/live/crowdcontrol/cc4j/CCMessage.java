package live.crowdcontrol.cc4j;

public record CCMessage(Level level, String message) {
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
