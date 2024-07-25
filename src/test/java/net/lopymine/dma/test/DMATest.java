package net.lopymine.dma.test;

import org.gradle.testkit.runner.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.*;
import java.nio.file.Files;

public class DMATest {

	@TempDir
	private File testProjectDir;

	@Test
	public void testAnnounce() {
		try {
			InputStream inputStream = DMATest.class.getClassLoader().getResourceAsStream("icon.png");
			if (inputStream == null) {
				throw new NullPointerException("Failed to find icon for testing!");
			}
			Files.copy(inputStream, this.testProjectDir.toPath().resolve("icon.png"));

			FileWriter writer = new FileWriter(new File(this.testProjectDir, "build.gradle"));
			writer.write("""
				 plugins {
				      id 'net.lopymine.discord-mod-announcer'
				 }

				 discordModAnnouncer {
				    announceMode = TEST
				    token = providers.environmentVariable("DISCORD_BOT_TOKEN")
				    icon = project.rootProject.file("icon.png")

				    title = "My Cool Mod v2.0.0 is out!"
				    changelog = "- Changelog line one \\n - Changelog line two \\n - Changelog line three"

				    modrinthLink = "https://youtu.be/dQw4w9WgXcQ?si=YuNYqbxc3xXANfKl"
				    curseForgeLink = "https://youtu.be/hvL1339luv0?si=m9v6lHiIz7aly3uJ"
				    githubLink = "https://youtu.be/EpX1_YJPGAY?si=MfyB_wTVIv6I3NcZ"

				    uploaderId = "616939110598443008"
				    announcementChannelId = "1102941223003631698"
				    testAnnouncementChannelId = "1266007822173470730"
				    pingRoleName = "Developer"
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
