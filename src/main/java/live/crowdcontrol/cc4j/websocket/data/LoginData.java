package live.crowdcontrol.cc4j.websocket.data;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.intellij.lang.annotations.Pattern;
import org.intellij.lang.annotations.RegExp;
import org.intellij.lang.annotations.Subst;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Defines an attempt to subscribe to channels of information on the WebSocket.
 */
public class LoginData {

	@RegExp
	public static final @NotNull String CODE_PATTERN = "^[BCDFGHJKLMNPQRTW6789]{6}$";
	private static final @NotNull Logger log = LoggerFactory.getLogger("CrowdControl/LoginData");

	private final @NotNull String appID;
	@Pattern(CODE_PATTERN) @Subst("BCDFGH")
	private final @NotNull String code;

	/**
	 * Creates a login body.
	 *
	 * @param appID the ID of the application
	 * @param code the code to log in with, typically conforming to {@link #CODE_PATTERN}
	 */
	@JsonCreator
	public LoginData(@JsonProperty("appID") @NotNull String appID,
					 @JsonProperty("code") @Pattern(CODE_PATTERN) @Subst("BCDFGH") @NotNull String code) {
		if (!code.matches(CODE_PATTERN))
			log.warn("Code {} may be invalid; fails to match expected pattern", code);

		this.appID = appID;
		this.code = code;
	}

	/**
	 * Gets the ID of the application.
	 *
	 * @return application ID
	 */
	public @NotNull String getAppID() {
		return appID;
	}

	/**
	 * Gets the code to log in with, typically conforming to {@link #CODE_PATTERN}.
	 *
	 * @return login code
	 */
	@Pattern(CODE_PATTERN)
	public @NotNull String getCode() {
		return code;
	}
}
