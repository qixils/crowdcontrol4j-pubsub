package live.crowdcontrol.cc4j.util;

import live.crowdcontrol.cc4j.CrowdControl;
import live.crowdcontrol.cc4j.ServerURLs;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

import static live.crowdcontrol.cc4j.websocket.ConnectedPlayer.GSON;

public class HttpUtil {
	public static final @NotNull URL OPEN_API_URL;
	private static final @NotNull Logger log = LoggerFactory.getLogger("CrowdControl/Http");

	static {
		try {
			OPEN_API_URL = new URL(ServerURLs.OPENAPI);
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
					try (Writer writer = new OutputStreamWriter(con.getOutputStream(), StandardCharsets.UTF_8)) {
						log.debug("Outputting to {}: {}", spec, GSON.toJson(data));
						GSON.toJson(data, writer);
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
		try {
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			byte[] buffer = new byte[8192];
			int read;
			while ((read = stream.read(buffer)) != -1)
				out.write(buffer, 0, read);
			return new String(out.toByteArray(), StandardCharsets.UTF_8);
		} catch (Exception e) {
			throw new RuntimeException("Failed to read as string", e);
		}
	}

	private <T> @NotNull Function<InputStream, T> createOutputFunction(@NotNull Type output) {
		return input -> {
			try (Reader reader = new InputStreamReader(input, StandardCharsets.UTF_8)) {
				return GSON.fromJson(reader, output);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		};
	}

	private <T> @NotNull Function<InputStream, T> createOutputFunction(@NotNull TypeToken<T> output) {
		return createOutputFunction(output.getType());
	}

	private <T> @NotNull Function<InputStream, T> createOutputFunction(@NotNull Class<T> output) {
		return createOutputFunction((Type) output);
	}

	public <T> @NotNull CompletableFuture<T> apiGet(@NotNull String spec, @NotNull TypeToken<T> output, @Nullable String token) {
		return apiCall("GET", spec, createOutputFunction(output), token, null);
	}

	public <T> @NotNull CompletableFuture<T> apiGet(@NotNull String spec, @NotNull Class<T> output, @Nullable String token) {
		return apiCall("GET", spec, createOutputFunction(output), token, null);
	}

	public @NotNull CompletableFuture<String> apiGet(@NotNull String spec, @Nullable String token) {
		return apiCall("GET", spec, this::asString, token, null);
	}

	public <T> @NotNull CompletableFuture<T> apiPost(@NotNull String spec, @NotNull TypeToken<T> output, @Nullable String token, @Nullable Object data) {
		return apiCall("POST", spec, createOutputFunction(output), token, data);
	}

	public <T> @NotNull CompletableFuture<T> apiPost(@NotNull String spec, @NotNull Class<T> output, @Nullable String token, @Nullable Object data) {
		return apiCall("POST", spec, createOutputFunction(output), token, data);
	}

	public @NotNull CompletableFuture<String> apiPost(@NotNull String spec, @Nullable String token, @Nullable Object data) {
		return apiCall("POST", spec, this::asString, token, data);
	}

	public <T> @NotNull CompletableFuture<T> apiPut(@NotNull String spec, @NotNull TypeToken<T> output, @Nullable String token, @Nullable Object data) {
		return apiCall("PUT", spec, createOutputFunction(output), token, data);
	}

	public <T> @NotNull CompletableFuture<T> apiPut(@NotNull String spec, @NotNull Class<T> output, @Nullable String token, @Nullable Object data) {
		return apiCall("PUT", spec, createOutputFunction(output), token, data);
	}

	public @NotNull CompletableFuture<String> apiPut(@NotNull String spec, @Nullable String token, @Nullable Object data) {
		return apiCall("PUT", spec, this::asString, token, data);
	}
}
