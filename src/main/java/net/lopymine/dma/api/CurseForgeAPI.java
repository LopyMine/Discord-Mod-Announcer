package net.lopymine.dma.api;

import com.google.gson.JsonElement;

import net.lopymine.dma.utils.HttpUtils;

import org.jetbrains.annotations.Nullable;

public class CurseForgeAPI {

	@Nullable
	public static String getLinkForLatestModVersion(Integer projectId, String minecraftVersion, String loader, String token) {
		try {
			String url = String.format(
					"https://api.curseforge.com/v1/mods/%s/files?gameVersion=%s&modLoaderType=%s&pageSize=1",
					projectId,
					minecraftVersion,
					loader.toLowerCase()
			);

			JsonElement jsonElement = HttpUtils.get(url, (builder) -> builder.header("x-api-key", token));
			if (jsonElement == null) {
				return null;
			}

			String projectSlug = CurseForgeAPI.getProjectSlug(projectId, token);
			String id = jsonElement.getAsJsonObject().get("data").getAsJsonArray().get(0).getAsJsonObject().get("id").getAsString();
			return String.format("https://www.curseforge.com/minecraft/mc-mods/%s/files/%s", projectSlug, id);
		} catch (Exception e) {
			System.out.println("Failed to get link for latest mod version from CurseForge:");
			e.printStackTrace();
		}
		return null;
	}

	@Nullable
	public static String getProjectSlug(Integer projectId, String token) {
		try {
			String url = String.format(
					"https://api.curseforge.com/v1/mods/%s",
					projectId
			);

			JsonElement jsonElement = HttpUtils.get(url, (builder) -> builder.header("x-api-key", token));
			if (jsonElement == null) {
				return null;
			}

			return jsonElement.getAsJsonObject().get("data").getAsJsonObject().get("slug").getAsString();
		} catch (Exception e) {
			System.out.println("Failed to get project slug from CurseForge:");
			e.printStackTrace();
		}
		return null;
	}
}
