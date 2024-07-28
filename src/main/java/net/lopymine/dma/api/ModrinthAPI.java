package net.lopymine.dma.api;

import com.google.gson.*;

import net.lopymine.dma.utils.HttpUtils;

public class ModrinthAPI {

	public static String getLinkForLatestModVersion(String modIdOrSlug, String minecraftVersion, String loader) {
		try {
			String url = String.format(
					"https://api.modrinth.com/v2/project/%s/version?game_versions=[\"%s\"]&loaders=[\"%s\"]",
					modIdOrSlug,
					minecraftVersion,
					loader.toLowerCase()
			).replace("[", "%5B").replace("]", "%5D").replace("\"", "%22");
			JsonElement jsonElement = HttpUtils.get(url, (builder) -> {});
			if (jsonElement == null) {
				return null;
			}

			JsonArray array = jsonElement.getAsJsonArray();
			if (array.isEmpty()) {
				System.out.printf("Failed to find latest mod version from Modrinth for %s %s%n", loader, minecraftVersion);
				return null;
			}
			String id = array.get(0).getAsJsonObject().get("id").getAsString();
			return String.format("https://modrinth.com/mod/%s/version/%s", modIdOrSlug, id);
		} catch (Exception e) {
			System.out.println("Failed to get link for latest mod version from Modrinth:");
			e.printStackTrace();
		}
		return null;
	}

}
