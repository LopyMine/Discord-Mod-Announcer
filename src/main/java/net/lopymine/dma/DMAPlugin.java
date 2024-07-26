package net.lopymine.dma;

import net.dv8tion.jda.internal.utils.Checks;
import org.gradle.api.*;

import java.io.File;
import java.util.List;
import org.jetbrains.annotations.NotNull;

public class DMAPlugin implements Plugin<Project> {

	@Override
	public void apply(@NotNull Project project) {
		project.getExtensions().create("announceToDiscord", DMAExtension.class);
		project.getTasks().register("announceToDiscord", DMATask.class, (task) -> {
			task.setGroup("announcements");

			DMAExtension extension = (DMAExtension) project.getExtensions().getByName("announceToDiscord");
			String token = extension.getToken().getOrNull();
			AnnounceMode announceMode = extension.getAnnounceMode();
			File icon = extension.getIcon();
			String title = extension.getTitle();
			String showcaseThreadTitle = extension.getShowcaseThreadTitle();
			String changelog = extension.getChangelog();
			Integer color = extension.getColor();
			String modrinthLink = extension.getModrinthLink();
			String curseForgeLink = extension.getCurseForgeLink();
			String githubLink = extension.getGithubLink();
			String uploaderId = extension.getUploaderId();
			List<String> pingRoles = extension.getPingRoles();
			String announcementChannelId = extension.getAnnouncementChannelId();
			String testAnnouncementChannelId = extension.getTestAnnouncementChannelId();
			List<File> images = extension.getShowcaseImages();

			Checks.notNull(token, "token");
			Checks.notNull(title, "title");
			Checks.notNull(changelog, "changelog");
			Checks.notNull(announcementChannelId, "announcementChannelId");
			Checks.notNull(testAnnouncementChannelId, "testAnnouncementChannelId");

			task.setToken(token);
			task.setAnnounceMode(announceMode == null ? AnnounceMode.ENABLE : announceMode);
			task.setIcon(icon);
			task.setTitle(title);
			task.setChangelog(changelog);
			task.setColor(color);
			task.setModrinthLink(modrinthLink);
			task.setCurseForgeLink(curseForgeLink);
			task.setGithubLink(githubLink);
			task.setUploaderId(uploaderId);
			task.setPingRoles(pingRoles);
			task.setAnnouncementChannelId(announcementChannelId);
			task.setTestAnnouncementChannelId(testAnnouncementChannelId);
			task.setShowcaseThreadTitle(showcaseThreadTitle);
			task.setShowcaseImages(images);
		});
	}
}
