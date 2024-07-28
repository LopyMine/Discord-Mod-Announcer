package net.lopymine.dma;

import com.google.gson.*;
import net.dv8tion.jda.internal.utils.Checks;
import org.gradle.api.*;

import net.lopymine.dma.options.LinksFormat;

import java.util.*;
import org.jetbrains.annotations.NotNull;

public class DMAPlugin implements Plugin<Project> {

	public static final Gson GSON = new GsonBuilder().setLenient().disableHtmlEscaping().create();

	@Override
	public void apply(@NotNull Project project) {
		project.getExtensions().create("announceToDiscord", DMAExtension.class);
		project.getTasks().register("announceToDiscord", DMATask.class, (task) -> {
			task.setGroup("announcements");

			DMAExtension extension = (DMAExtension) project.getExtensions().getByName("announceToDiscord");

			Checks.notNull(extension.getToken().getOrNull(), "token");
			Checks.notNull(extension.getTitle(), "title");
			Checks.notNull(extension.getChangelog(), "changelog");
			Checks.notNull(extension.getAnnouncementChannelId(), "announcementChannelId");
			Checks.notNull(extension.getTestAnnouncementChannelId(), "testAnnouncementChannelId");
			if (extension.getLinksFormat() == null) {
				extension.setLinksFormat(LinksFormat.BUTTONS);
			}

			Map<String, Set<String>> map = new HashMap<>();
			if (extension.getLinksFormat() == LinksFormat.VERSIONED) {
				Checks.check(extension.getMinecraftVersion() != null, "Minecraft version cannot be null with VERSIONED linksFormat");
				Checks.check(extension.getLoader() != null, "Loader cannot be null with VERSIONED linksFormat");
				Checks.check(extension.getCurseForgeAPIToken() != null, "CurseForge API Token cannot be null with VERSIONED linksFormat");

				for (Project childProject : project.getRootProject().getChildProjects().values()) {
					DMAExtension childExtension = (DMAExtension) childProject.getExtensions().getByName("announceToDiscord");
					Checks.check(childExtension.getLinksFormat() == LinksFormat.VERSIONED, "Subproject %s must have linksFormat VERSIONED because root project linksFormat is VERSIONED", childProject.getName());
					Checks.check(childExtension.getMinecraftVersion() != null, "Minecraft version cannot be null with VERSIONED linksFormat");
					Checks.check(childExtension.getLoader() != null, "Loader cannot be null with VERSIONED linksFormat");

					Set<String> minecraftVersions = map.get(childExtension.getLoader());
					if (minecraftVersions == null) {
						map.put(childExtension.getLoader(), new HashSet<>(List.of(childExtension.getMinecraftVersion())));
					} else {
						minecraftVersions.add(childExtension.getMinecraftVersion());
					}
				}
				task.setMap(map);
			}
			task.setExtension(extension);
		});
	}
}
