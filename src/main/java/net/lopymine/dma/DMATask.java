package net.lopymine.dma;

import lombok.*;
import net.dv8tion.jda.api.*;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.channel.concrete.*;
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
import java.util.stream.Stream;
import org.jetbrains.annotations.*;

@Setter
@Getter
public class DMATask extends DefaultTask {

	@NotNull
	@Input
	String token;
	@Nullable
	@Optional
	@Input
	AnnounceMode announceMode;
	@Nullable
	@Optional
	@InputFile
	File icon;

	@NotNull
	@Input
	String title;
	@Nullable
	@Optional
	@Input
	String showcaseThreadTitle;
	@NotNull
	@Input
	String changelog;
	@Nullable
	@Optional
	@Input
	Integer color;
	@Nullable
	@Optional
	@InputFiles
	List<File> showcaseImages;

	@Nullable
	@Optional
	@Input
	String modrinthLink;
	@Nullable
	@Optional
	@Input
	String curseForgeLink;
	@Nullable
	@Optional
	@Input
	String githubLink;

	@Nullable
	@Optional
	@Input
	String uploaderId;
	@NotNull
	@Input
	String announcementChannelId;
	@NotNull
	@Input
	String testAnnouncementChannelId;
	@Nullable
	@Optional
	@Input
	List<String> pingRoles;

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

			String channelId = this.announceMode == AnnounceMode.TEST ?
					this.testAnnouncementChannelId
					:
					this.announcementChannelId;

			TextChannel channel = bot.getTextChannelById(channelId);
			if (channel == null) {
				bot.shutdown();
				throw new NullPointerException(String.format("Failed to find channel with id '%s'", channelId));
			}

			User uploaderUser = this.uploaderId == null ?
					null
					:
					bot.getUserById(this.uploaderId);

			List<Role> roles = this.pingRoles != null && !this.pingRoles.isEmpty() ?
					this.pingRoles.stream().flatMap((roleName) -> {
						List<Role> rolesByName = bot.getRolesByName(roleName, true);
						return rolesByName.isEmpty() ? Stream.empty() : Stream.of(rolesByName.get(0));
					}).toList()
					:
					null;

			MessageEmbed embed = this.createAnnounceEmbed(
					uploaderUser,
					this.showcaseImages != null && !this.showcaseImages.isEmpty()
							? this.showcaseImages.get(0)
							:
							null
			);

			Message message = this.sendEmbed(channel, embed, roles);

			if (this.showcaseImages != null && this.showcaseImages.size() >= 2) {
				ThreadChannel threadChannel = message.createThreadChannel(this.showcaseThreadTitle == null ? "Showcase" : this.showcaseThreadTitle).complete();
				threadChannel.getManager().setLocked(true).queue();

				for (int i = 1; i < this.showcaseImages.size(); i += 3) {
					int end = Math.min(i + 3, this.showcaseImages.size());

					threadChannel.sendMessage(MessageCreateData.fromFiles(
							this.showcaseImages.subList(i, end)
									.stream()
									.flatMap((file) -> Stream.of(FileUpload.fromData(file)))
									.toList().toArray(new FileUpload[0])
					)).queue();
				}
			}


			System.out.println("[Discord Mod Announcer] Announced");

			bot.shutdown();
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}

	private Message sendEmbed(@NotNull TextChannel channel, MessageEmbed embed, @Nullable List<Role> pingRoles) {
		MessageCreateAction action = channel.sendMessage(MessageCreateData.fromEmbeds(embed));
		if (pingRoles != null && !pingRoles.isEmpty()) {
			StringBuilder builder = new StringBuilder();
			for (Role role : pingRoles) {
				builder.append(String.format("<@&%s> ", role.getId()));
			}
			action.setContent(String.format("||%s||", builder.toString().trim()));
		}
		Collection<ItemComponent> buttons = this.getButtons();
		if (!buttons.isEmpty()) {
			action.addComponents(ActionRow.of(buttons));
		}
		if (this.icon != null) {
			action.addFiles(FileUpload.fromData(this.icon, "icon.png"));
		}
		if (this.showcaseImages != null && !this.showcaseImages.isEmpty()) {
			action.addFiles(FileUpload.fromData(this.showcaseImages.get(0)));
		}
		return action.complete();
	}

	private @NotNull MessageEmbed createAnnounceEmbed(@Nullable User user, @Nullable File image) {
		EmbedBuilder builder = new EmbedBuilder()
				.setDescription(String.format("# %s \n %s", this.title, this.changelog))
				.setColor(this.color == null ? 3265101 : this.color);
		if (user != null) {
			builder.setAuthor(user.getEffectiveName(), null, user.getAvatarUrl());
		}
		if (this.icon != null) {
			builder.setThumbnail("attachment://icon.png");
		}
		if (image != null) {
			builder.setImage(String.format("attachment://%s", image.getName()));
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
