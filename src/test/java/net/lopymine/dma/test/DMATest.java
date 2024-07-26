package net.lopymine.dma.test;

import org.gradle.testkit.runner.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.*;
import java.nio.file.Files;
import org.jetbrains.annotations.NotNull;

public class DMATest {

	@TempDir
	private File testProjectDir;

	@Test
	public void testAnnounce() {
		try {
			ClassLoader classLoader = DMATest.class.getClassLoader();
			this.copyTestFilesToTestProject(classLoader, "icon.png", "icon.png");
			this.copyTestFilesToTestProject(classLoader, "images/showcase.png", "showcase.png");
			this.copyTestFilesToTestProject(classLoader, "images/showcase2.png", "showcase2.png");

			FileWriter writer = new FileWriter(new File(this.testProjectDir, "build.gradle"));
			writer.write("""
				 plugins {
				      id 'net.lopymine.discord-mod-announcer'
				 }

				 announceToDiscord {
				    announceMode = TEST
				    token = providers.environmentVariable("DISCORD_BOT_TOKEN")
				    icon = project.rootProject.file("icon.png")

				    title = "My Cool Mod v2.0.0 is out!"
				    showcaseThreadTitle = "Showcase My Cool Mod v2.0.0"
				    changelog = "- Changelog line one \\n - Changelog line two \\n - Changelog line three"

				    modrinthLink = "https://youtu.be/dQw4w9WgXcQ?si=YuNYqbxc3xXANfKl"
				    curseForgeLink = "https://youtu.be/hvL1339luv0?si=m9v6lHiIz7aly3uJ"
				    githubLink = "https://youtu.be/EpX1_YJPGAY?si=MfyB_wTVIv6I3NcZ"

				    uploaderId = "616939110598443008"
				    announcementChannelId = "1102941223003631698"
				    testAnnouncementChannelId = "1266007822173470730"
				    pingRoles = ["Developer", "Mossy"]

					showcaseImages = [project.rootProject.file("showcase.png"),project.rootProject.file("showcase2.png"), project.rootProject.file("showcase2.png")]
				 }
			""");
			writer.close();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		BuildResult result = GradleRunner.create()
				.withProjectDir(this.testProjectDir)
				.withArguments("announceToDiscord")
				.withPluginClasspath()
				.build();

		System.out.println("Test result: " + result.getOutput());
	}

	private void copyTestFilesToTestProject(@NotNull ClassLoader classLoader, String from, String to) throws IOException {
		InputStream inputStream = classLoader.getResourceAsStream(from);
		if (inputStream == null) {
			throw new NullPointerException("Failed to find file at \"%s\" for testing!".formatted(from));
		}
		Files.copy(inputStream, this.testProjectDir.toPath().resolve(to));
	}

	@Test
	public void testAnnounceTwo() {
		try {
			FileWriter writer = new FileWriter(new File(this.testProjectDir, "build.gradle"));
			writer.write("""
				 plugins {
				      id 'net.lopymine.discord-mod-announcer'
				 }

				 announceToDiscord {
				    announceMode = TEST
				    token = providers.environmentVariable("DISCORD_BOT_TOKEN")

				    title = "My Cool Mod v2.0.0 is out!"
				    changelog = "- Changelog line one \\n - Changelog line two \\n - Changelog line three"

				    announcementChannelId = "1102941223003631698"
				    testAnnouncementChannelId = "1266007822173470730"
				 }
			""");
			writer.close();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		BuildResult result = GradleRunner.create()
				.withProjectDir(this.testProjectDir)
				.withArguments("announceToDiscord")
				.withPluginClasspath()
				.build();

		System.out.println("Test result: " + result.getOutput());
	}
}
