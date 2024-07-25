package net.lopymine.dma;

import lombok.*;
import net.dv8tion.jda.api.*;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.interactions.components.*;
import net.dv8tion.jda.api.interactions.components.buttons.*;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.requests.restaction.MessageCreateAction;
import net.dv8tion.jda.api.utils.*;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.Optional;
import org.gradle.api.tasks.*;

import java.io.File;
import java.util.*;
import org.jetbrains.annotations.*;

@Setter
@Getter
public class DMATask extends DefaultTask {

	@Input
	@NotNull
	public String token;
	@Input
	@NotNull
	public AnnounceMode announceMode;
	@InputFile
	@Nullable
	@Optional
	public File icon;
	@Input
	@NotNull
	public String changelog;
	@Input
	@NotNull
	public String title;
	@Input
	@Nullable
	@Optional
	public Integer color;
	@Input
	@Nullable
	@Optional
	public String modrinthLink;
	@Input
	@Nullable
	@Optional
	public String curseForgeLink;
	@Input
	@Nullable
	@Optional
	public String githubLink;
	@Input
	@Nullable
	@Optional
	public String uploaderId;
	@Input
	@NotNull
	public String announcementChannelId;
	@Input
	@NotNull
	public String testAnnouncementChannelId;
	@Input
	@Nullable
	@Optional
	public String pingRoleName;
	@Input
	@Nullable
	@Optional
	public List<String> projectsForAnnounce;

	@TaskAction
	private void announce() {
		if (this.announceMode == AnnounceMode.DISABLE) {
			System.out.println("[Discord Mod Announcer] The announcement has been canceled because it's disabled");
			return;
		}
		try {
			JDA bot = JDABuilder.createDefault(this.token)
					.setEnabledIntents(GatewayIntent.getIntents(GatewayIntent.ALL_INTENTS))
					.setMemberCachePolicy(MemberCachePolicy.ALL)
					.setActivity(Activity.customStatus("Announcing... \uD83C\uDF3F"))
					.build()
					.awaitReady();

			String channelId = this.announceMode == AnnounceMode.TEST ? this.testAnnouncementChannelId : this.announcementChannelId;
			TextChannel channel = bot.getTextChannelById(channelId);
			if (channel == null) {
				bot.shutdown();
				throw new NullPointerException(String.format("Failed to find channel with id '%s'", channelId));
			}
			User uploaderUser = this.uploaderId == null ? null : bot.getUserById(this.uploaderId);
			List<Role> roles = this.pingRoleName == null ? new ArrayList<>() : bot.getRolesByName(this.pingRoleName, true);

			this.sendEmbed(channel, this.getAnnounceEmbed(uploaderUser), roles.isEmpty() ? null : roles.get(0));

			System.out.println("[Discord Mod Announcer] Announced");

			bot.shutdown();
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}

	private void sendEmbed(@NotNull TextChannel channel, MessageEmbed embed, @Nullable Role pingRole) {
		MessageCreateAction action = channel.sendMessage(MessageCreateData.fromEmbeds(embed));
		if (pingRole != null) {
			action.setContent(String.format("||<@&%s>||", pingRole.getId()));
		}
		Collection<ItemComponent> buttons = this.getButtons();
		if (!buttons.isEmpty()) {
			action.addComponents(ActionRow.of(buttons));
		}
		if (this.icon != null) {
			action.addFiles(FileUpload.fromData(this.icon, "icon.png"));
		}
		action.queue();
	}

	private @NotNull MessageEmbed getAnnounceEmbed(@Nullable User user) {
		EmbedBuilder builder = new EmbedBuilder()
				.setDescription(String.format("# %s \n %s", this.title, this.changelog.isEmpty() ? "- *No changelog provided.*" : this.changelog))
				.setColor(this.color == null ? 3265101 : this.color);
		if (user != null) {
			builder.setAuthor(user.getEffectiveName(), null, user.getAvatarUrl());
		}
		if (this.icon != null) {
			builder.setThumbnail("attachment://icon.png");
		}
		return builder.build();
	}

	private @NotNull Collection<ItemComponent> getButtons() {
		Collection<ItemComponent> components = new ArrayList<>();
		if (this.modrinthLink != null) {
			components.add(Button.of(ButtonStyle.LINK, this.modrinthLink, "Modrinth"));
		}
		if (this.curseForgeLink != null) {
			components.add(Button.of(ButtonStyle.LINK, this.curseForgeLink, "CurseForge"));
		}
		if (this.githubLink != null) {
			components.add(Button.of(ButtonStyle.LINK, this.githubLink, "Github"));
		}
		return components;
	}
}
