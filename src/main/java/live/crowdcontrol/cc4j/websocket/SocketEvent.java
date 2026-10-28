package live.crowdcontrol.cc4j.websocket;

import com.google.gson.JsonElement;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
class SocketEvent {
	public String domain;
	public String type;
	public JsonElement payload;
}
