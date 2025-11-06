package live.crowdcontrol.cc4j.websocket.http;

public record CustomEffectQuantity(
	int min,
	int max
) {
	public CustomEffectQuantity(int max) {
		this(1, max);
	}
}
