package net.lopymine.dma.utils;

import com.google.gson.JsonElement;

import net.lopymine.dma.DMAPlugin;

import java.net.URI;
import java.net.http.*;
import java.net.http.HttpRequest.Builder;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.function.Consumer;
import org.jetbrains.annotations.Nullable;

public class HttpUtils {

	@Nullable
	public static JsonElement get(String url, Consumer<Builder> setupRequest) {
		try (HttpClient httpClient = HttpClient.newHttpClient()) {
			Builder builder = HttpUtils.getBaseGETRequestBuilder(url);
			setupRequest.accept(builder);

			HttpResponse<String> response = httpClient.send(builder.build(), BodyHandlers.ofString());
			if ((response.statusCode() / 100) != 2) {
				System.out.printf("Failed request to %s, status code: %s, body:\n%s%n", url, response.statusCode(), response.body());
				return null;
			}
			JsonElement jsonElement = DMAPlugin.GSON.fromJson(response.body(), JsonElement.class);
			if (jsonElement == null) {
				System.out.printf("Failed to parse response body, status code: %s, body:\n%s%n", response.statusCode(), response.body());
				return null;
			}
			return jsonElement;
		} catch (Exception e) {
			System.out.printf("Failed request to %s:%n", url);
			e.printStackTrace();
		}
		return null;
	}

	public static Builder getBaseGETRequestBuilder(String url) {
		return HttpRequest.newBuilder()
				.uri(URI.create(url))
				.GET()
				.header("Content-Type", "application/json");
	}
}
