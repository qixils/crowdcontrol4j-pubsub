package live.crowdcontrol.cc4j.util;

import org.java_websocket.framing.CloseFrame;
import org.jetbrains.annotations.Nullable;

/**
 * Information related to the WebSocket connection closing
 */
public final class CloseData {
	private final int code;
	private final @Nullable String reason;
	private final boolean remote;

	public CloseData(int code, @Nullable String reason, boolean remote) {
		this.code = code;
		this.reason = reason;
		this.remote = remote;
	}

	/**
	 * The codes can be looked up here: {@link CloseFrame}
	 *
	 * @return close frame code
	 */
	public int getCode() {
		return code;
	}

	/***
	 * Additional information string
	 *
	 * @return reason string
	 */
	public @Nullable String getReason() {
		return reason;
	}

	/**
	 * Returns whether the closing of the connection was initiated by the remote host.
	 *
	 * @return remote cause
	 * @deprecated no longer available
	 */
	@Deprecated
	public boolean isRemote() {
		return remote;
	}
}
