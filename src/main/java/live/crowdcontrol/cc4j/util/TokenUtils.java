package live.crowdcontrol.cc4j.util;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

public final class TokenUtils {

	private TokenUtils() {
	}

	/**
	 * Splits the given token on the "." chars into a String array with 3 parts.
	 *
	 * @param token the string to split.
	 * @return the array representing the 3 parts of the token.
	 * @throws IllegalArgumentException if the Token doesn't have 3 parts.
	 */
	static String[] splitToken(String token) throws IllegalArgumentException {
		if (token == null) {
			throw new IllegalArgumentException("The token is null.");
		}

		char delimiter = '.';

		int firstPeriodIndex = token.indexOf(delimiter);
		if (firstPeriodIndex == -1) {
			throw wrongNumberOfParts(0);
		}

		int secondPeriodIndex = token.indexOf(delimiter, firstPeriodIndex + 1);
		if (secondPeriodIndex == -1) {
			throw wrongNumberOfParts(2);
		}

		// too many ?
		if (token.indexOf(delimiter, secondPeriodIndex + 1) != -1) {
			throw wrongNumberOfParts("> 3");
		}

		String[] parts = new String[3];
		parts[0] = token.substring(0, firstPeriodIndex);
		parts[1] = token.substring(firstPeriodIndex + 1, secondPeriodIndex);
		parts[2] = token.substring(secondPeriodIndex + 1);

		return parts;
	}

	private static IllegalArgumentException wrongNumberOfParts(Object partCount) {
		return new IllegalArgumentException(String.format("The token was expected to have 3 parts, but got %s.", partCount));
	}

	public static String decodePart(String base64) throws IllegalArgumentException {
		return new String(Base64.getUrlDecoder().decode(base64), StandardCharsets.UTF_8);
	}

	public static String decodePart(String token, int part) throws IllegalArgumentException {
		return decodePart(splitToken(token)[part]);
	}

	public static String decodePayload(String token) throws IllegalArgumentException {
		return decodePart(token, 1);
	}
}
