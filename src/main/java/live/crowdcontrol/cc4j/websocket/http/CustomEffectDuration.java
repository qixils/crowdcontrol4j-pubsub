package live.crowdcontrol.cc4j.websocket.http;

public record CustomEffectDuration(
	double value,
	boolean immutable
) {
	public CustomEffectDuration(double value) {
		this(value, false);
	}
}
