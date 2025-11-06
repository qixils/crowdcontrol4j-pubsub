package live.crowdcontrol.cc4j.util;

import com.fasterxml.jackson.core.type.TypeReference;
import live.crowdcontrol.cc4j.CrowdControl;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.http.HttpClient;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

import static live.crowdcontrol.cc4j.websocket.ConnectedPlayer.JACKSON;

public class HttpUtil {
	public static final @NotNull URL OPEN_API_URL;
	public static final HttpClient HTTP_CLIENT = HttpClient.newHttpClient();
	private static final @NotNull TypeReference<String> STRING_TYPE = new TypeReference<String>() { };
	private static final Logger log = LoggerFactory.getLogger("CrowdControl/HttpUtil");

	static {
		try {
			OPEN_API_URL = new URL("https://openapi.crowdcontrol.live");
		} catch (MalformedURLException e) {
			throw new RuntimeException("Failed to create OpenAPI URL", e);
		}
	}

	private final @NotNull CrowdControl parent;

	public HttpUtil(@NotNull CrowdControl parent) {
		this.parent = parent;
	}

	private <T> @NotNull CompletableFuture<T> apiCall(@NotNull String method, @NotNull String spec, @NotNull Function<InputStream, T> output, @Nullable String token, @Nullable Object data) {
		return CompletableFuture.supplyAsync(() -> {
			HttpURLConnection con = null;
			try {
				URL url = new URL(OPEN_API_URL, spec);
				con = (HttpURLConnection) url.openConnection();
				con.setRequestMethod(method);
				con.setRequestProperty("User-Agent", "crowdcontrol4j");
				con.setRequestProperty("Content-Type", "application/json");
				if (token != null) {
					con.setRequestProperty("Authorization", "cc-auth-token " + token);
				}
				con.setConnectTimeout(10000);
				con.setReadTimeout(10000);
				if (data != null) {
					con.setRequestProperty("Accept", "application/json");
					con.setDoOutput(true);
					try (OutputStream os = con.getOutputStream()) {
//						log.info("Outputting {}", JACKSON.writeValueAsString(data));
						JACKSON.writeValue(os, data);
					}
				}
				T out = output.apply(con.getInputStream());
				if (con.getResponseCode() != 200)
					throw new IllegalStateException("Server returned code " + con.getResponseCode()); // TODO: this is jank !
				return out;
			} catch (Exception e) {
				throw new RuntimeException(e);
			} finally {
				if (con != null) con.disconnect();
			}
		}, parent.getEffectPool());
	}

	private String asString(InputStream stream) {
		try (BufferedReader br = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8))) {
			StringBuilder response = new StringBuilder();
			String responseLine;
			while ((responseLine = br.readLine()) != null) {
				response.append(responseLine.trim());
			}
			return response.toString();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private <T> @NotNull Function<InputStream, T> createOutputFunction(@NotNull TypeReference<T> output) {
		return input -> {
			try {
				return JACKSON.readValue(input, output);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		};
	}

	private <T> @NotNull Function<InputStream, T> createOutputFunction(@NotNull Class<T> output) {
		return input -> {
			try {
				return JACKSON.readValue(input, output);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		};
	}

	public <T> @NotNull CompletableFuture<T> apiGet(@NotNull String spec, @NotNull TypeReference<T> output, @Nullable String token) {
		return apiCall("GET", spec, createOutputFunction(output), token, null);
	}

	public <T> @NotNull CompletableFuture<T> apiGet(@NotNull String spec, @NotNull Class<T> output, @Nullable String token) {
		return apiCall("GET", spec, createOutputFunction(output), token, null);
	}

	public @NotNull CompletableFuture<String> apiGet(@NotNull String spec, @Nullable String token) {
		return apiCall("GET", spec, this::asString, token, null);
	}

	public <T> @NotNull CompletableFuture<T> apiPost(@NotNull String spec, @NotNull TypeReference<T> output, @Nullable String token, @Nullable Object data) {
		return apiCall("POST", spec, createOutputFunction(output), token, data);
	}

	public <T> @NotNull CompletableFuture<T> apiPost(@NotNull String spec, @NotNull Class<T> output, @Nullable String token, @Nullable Object data) {
		return apiCall("POST", spec, createOutputFunction(output), token, data);
	}

	public @NotNull CompletableFuture<String> apiPost(@NotNull String spec, @Nullable String token, @Nullable Object data) {
		return apiCall("POST", spec, this::asString, token, data);
	}

	public <T> @NotNull CompletableFuture<T> apiPut(@NotNull String spec, @NotNull TypeReference<T> output, @Nullable String token, @Nullable Object data) {
		return apiCall("PUT", spec, createOutputFunction(output), token, data);
	}

	public <T> @NotNull CompletableFuture<T> apiPut(@NotNull String spec, @NotNull Class<T> output, @Nullable String token, @Nullable Object data) {
		return apiCall("PUT", spec, createOutputFunction(output), token, data);
	}

	public @NotNull CompletableFuture<String> apiPut(@NotNull String spec, @Nullable String token, @Nullable Object data) {
		return apiCall("PUT", spec, this::asString, token, data);
	}
}
