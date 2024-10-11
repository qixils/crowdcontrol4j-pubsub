package live.crowdcontrol.cc4j.websocket.data;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * Instructs the server to adjust the visibility or usability of an effect in the menu.
 */
public class CCEffectReport {
	protected final @NotNull UUID id;
	protected final long stamp;
	protected final @NotNull IdentifierType identifierType;
	protected final @NotNull List<@NotNull String> ids;
	protected final @NotNull ReportStatus status;

	@JsonCreator
	CCEffectReport(@JsonProperty("id") @NotNull UUID id,
				   @JsonProperty("stamp") long stamp,
				   @JsonProperty("identifierType") @NotNull IdentifierType identifierType,
				   @JsonProperty("ids") @NotNull List<@NotNull String> ids,
				   @JsonProperty("status") @NotNull ReportStatus status) {
		this.id = id;
		this.stamp = stamp;
		this.identifierType = identifierType;
		this.ids = ids;
		this.status = status;
	}

	/**
	 * Creates a report.
	 *
	 * @param identifierType the type of objects being reported on
	 * @param status         the new status of the objects
	 * @param ids            the objects being reported on
	 */
	public CCEffectReport(@NotNull IdentifierType identifierType,
						  @NotNull ReportStatus status,
						  @NotNull List<@NotNull String> ids) {
		this.id = UUID.randomUUID();
		this.stamp = (int) (System.currentTimeMillis() / 1000L);
		this.identifierType = identifierType;
		this.ids = Collections.unmodifiableList(new ArrayList<>(ids));
		this.status = status;
	}

	/**
	 * Creates a report.
	 *
	 * @param identifierType the type of objects being reported on
	 * @param status         the new status of the objects
	 * @param ids            the objects being reported on
	 */
	public CCEffectReport(@NotNull IdentifierType identifierType,
						  @NotNull ReportStatus status,
						  @NotNull String @NotNull ... ids) {
		this(identifierType, status, Arrays.asList(ids));
	}

	/**
	 * Creates a report for effects.
	 *
	 * @param status the new status of the objects
	 * @param ids    the objects being reported on
	 */
	public CCEffectReport(@NotNull ReportStatus status,
						  @NotNull List<@NotNull String> ids) {
		this(IdentifierType.EFFECT, status, ids);
	}

	/**
	 * Creates a report for effects.
	 *
	 * @param status the new status of the objects
	 * @param ids    the objects being reported on
	 */
	public CCEffectReport(@NotNull ReportStatus status,
						  @NotNull String @NotNull ... ids) {
		this(status, Arrays.asList(ids));
	}

	/**
	 * Gets the randomly generated ID of the argument.
	 *
	 * @return arg ID
	 */
	public @NotNull UUID getId() {
		return id;
	}

	/**
	 * Gets the timestamp in seconds since Unix epoch that this result was generated.
	 *
	 * @return unix epoch seconds timestamp
	 */
	public long getTimestamp() {
		return stamp;
	}

	/**
	 * Gets the type of objects being reported on.
	 *
	 * @return identifier type
	 */
	public @NotNull IdentifierType getIdentifierType() {
		return identifierType;
	}

	/**
	 * Gets the list of IDs being reported on.
	 *
	 * @return list of ids
	 */
	public @NotNull List<@NotNull String> getIds() {
		return ids;
	}

	/**
	 * The status value of the report.
	 *
	 * @return result status
	 */
	public @NotNull ReportStatus getStatus() {
		return status;
	}
}
