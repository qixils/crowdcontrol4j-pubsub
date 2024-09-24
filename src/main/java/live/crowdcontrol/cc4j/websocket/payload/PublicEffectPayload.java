package live.crowdcontrol.cc4j.websocket.payload;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

/**
 * A payload denoting information about a requested effect.
 */
public class PublicEffectPayload {

	protected @NotNull UUID requestID;
	protected long timestamp;
	protected @NotNull CCEffectDescription effect;
	protected @NotNull CCUserRecord target;
	protected @Nullable CCUserRecord origin;
	protected @Nullable CCUserRecord requester;
	protected boolean anonymous;
	protected int quantity;
	protected long localTimestamp = System.currentTimeMillis();
	// sourceDetails
	// game
	// gamePack
	// parameters

	// json fixers

	@JsonProperty("quantity")
	void setQuantity(int quantity) {
		this.quantity = Math.max(1, quantity);
	}

	// boring getters

	/**
	 * Gets the ID of the request.
	 *
	 * @return requestID
	 */
	public @NotNull UUID getRequestId() {
		return requestID;
	}

	/**
	 * Gets the time at which the request was generated in Unix epoch milliseconds.
	 *
	 * @return unix epoch milliseconds
	 */
	public long getTimestamp() {
		return timestamp;
	}

	/**
	 * Gets the description of the requested effect.
	 *
	 * @return effect
	 */
	public @NotNull CCEffectDescription getEffect() {
		return effect;
	}

	/**
	 * Gets the player who should be targeted by this effect.
	 *
	 * @return targeted player
	 */
	public @NotNull CCUserRecord getTarget() {
		return target;
	}

	/**
	 * Gets the channel this effect was purchased on.
	 * Used to determine what channel a relayed lobby effect came from.
	 *
	 * @return origin channel
	 */
	public @Nullable CCUserRecord getOrigin() {
		return origin;
	}

	/**
	 * Gets the viewer who purchased this effect.
	 *
	 * @return effect requester
	 */
	public @Nullable CCUserRecord getRequester() {
		return requester;
	}

	/**
	 * Gets whether the viewer asked to be anonymous.
	 *
	 * @return is anonymous
	 */
	public boolean isAnonymous() {
		return anonymous;
	}

	/**
	 * Gets how many of the effect were requested.
	 * 0 indicates no amount was specified.
	 *
	 * @return quantity
	 */
	public int getQuantity() {
		return quantity;
	}

	/**
	 * A local timestamp denoting when the effect came in.
	 * Used to correct for de-synced clocks.
	 *
	 * @return local timestamp
	 */
	public long getLocalTimestamp() {
		return localTimestamp;
	}
}
