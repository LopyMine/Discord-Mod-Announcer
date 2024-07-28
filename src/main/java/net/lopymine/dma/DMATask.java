package net.lopymine.dma;

import lombok.*;
import net.dv8tion.jda.api.*;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.MessageEmbed.Field;
import net.dv8tion.jda.api.entities.channel.concrete.*;
import net.dv8tion.jda.api.interactions.components.*;
import net.dv8tion.jda.api.interactions.components.buttons.*;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.requests.restaction.MessageCreateAction;
import net.dv8tion.jda.api.utils.*;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import org.gradle.api.DefaultTask;
import org.gradle.api.provider.Provider;
import org.gradle.api.tasks.Optional;
import org.gradle.api.tasks.*;

import net.lopymine.dma.api.*;
import net.lopymine.dma.options.*;

import java.io.File;
import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Stream;
import org.jetbrains.annotations.*;

@Setter
@Getter
public class DMATask extends DefaultTask {

	@NotNull
	@Input
	DMAExtension extension;
	@Nullable
	@Input
	@Optional
	Map<String, Set<String>> map;

	@TaskAction
	private void announce() {
		if (this.extension.getAnnounceMode() == AnnounceMode.DISABLE) {
			System.out.println("[Discord Mod Announcer] The announcement has been canceled because it's disabled");
			return;
		}

		try {
			JDA bot = JDABuilder.createDefault(this.extension.getToken().get())
					.setEnabledIntents(GatewayIntent.getIntents(GatewayIntent.ALL_INTENTS))
					.setMemberCachePolicy(MemberCachePolicy.ALL)
					.setActivity(Activity.customStatus("Announcing... \uD83C\uDF3F"))
					.build()
					.awaitReady();

			try {
				TextChannel channel = this.getTextChannel(bot);
				User uploaderUser = this.getUploaderUser(bot);
				List<Role> pingRoles = this.getPingRoles(bot);

				Message message = this.sendEmbed(channel, pingRoles, uploaderUser);
				this.tryCreateShowcaseThread(message);

				System.out.println("[Discord Mod Announcer] Announced");
			} catch (Exception e) {
				System.out.println("Failed to announce:");
				e.printStackTrace();
			}

			bot.shutdown();
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}

	@NotNull
	private TextChannel getTextChannel(JDA bot) {
		String channelId = this.getChannelId();
		TextChannel channel = bot.getTextChannelById(channelId);
		if (channel == null) {
			throw new NullPointerException(String.format("Failed to find channel with id '%s'", channelId));
		}
		return channel;
	}

	private @Nullable File getFirstShowcaseImageOrNull() {
		return this.extension.getShowcaseImages() != null && !this.extension.getShowcaseImages().isEmpty() ? this.extension.getShowcaseImages().get(0) : null;
	}

	private @Nullable User getUploaderUser(JDA bot) {
		return this.extension.getUploaderId() == null ? null : bot.getUserById(this.extension.getUploaderId());
	}

	private @Nullable List<Role> getPingRoles(JDA bot) {
		return this.extension.getPingRoles() != null && !this.extension.getPingRoles().isEmpty() ?
				this.extension.getPingRoles().stream().flatMap((roleName) -> {
					List<Role> rolesByName = bot.getRolesByName(roleName, true);
					return rolesByName.isEmpty() ? Stream.empty() : Stream.of(rolesByName.get(0));
				}).toList()
				:
				null;
	}

	private @NotNull String getChannelId() {
		return this.extension.getAnnounceMode() == AnnounceMode.TEST ? this.extension.getTestAnnouncementChannelId() : this.extension.getAnnouncementChannelId();
	}

	private void tryCreateShowcaseThread(Message announcementMessage) {
		if (this.extension.getShowcaseImages() != null && this.extension.getShowcaseImages().size() >= 2) {
			String threadName = this.extension.getShowcaseThreadTitle() == null ? "Showcase" : this.extension.getShowcaseThreadTitle();

			ThreadChannel threadChannel = announcementMessage.createThreadChannel(threadName).complete();
			threadChannel.getManager().setLocked(true).queue();

			for (int i = 1; i < this.extension.getShowcaseImages().size(); i += 3) {
				int end = Math.min(i + 3, this.extension.getShowcaseImages().size());

				threadChannel.sendMessage(MessageCreateData.fromFiles(
						this.extension.getShowcaseImages().subList(i, end)
								.stream()
								.flatMap((file) -> Stream.of(FileUpload.fromData(file)))
								.toList().toArray(new FileUpload[0])
				)).queue();
			}
		}
	}

	@NotNull
	private Message sendEmbed(@NotNull TextChannel channel, @Nullable List<Role> pingRoles, @Nullable User uploaderUser) {
		Collection<ItemComponent> buttons = this.getButtons();
		File firstImage = this.getFirstShowcaseImageOrNull();
		File icon = this.extension.getIcon();

		MessageEmbed embed = this.buildAnnouncementEmbed(uploaderUser, firstImage, icon);
		MessageCreateAction action = channel.sendMessage(MessageCreateData.fromEmbeds(embed));

		if (pingRoles != null && !pingRoles.isEmpty()) {
			StringBuilder builder = new StringBuilder();
			for (Role role : pingRoles) {
				builder.append(String.format("<@&%s> ", role.getId()));
			}
			action.setContent(String.format("||%s||", builder.toString().trim()));
		}

		if (!buttons.isEmpty()) {
			action.addComponents(ActionRow.of(buttons));
		}
		if (icon != null) {
			action.addFiles(FileUpload.fromData(icon, "icon.png"));
		}
		if (firstImage != null) {
			action.addFiles(FileUpload.fromData(firstImage));
		}

		return action.complete();
	}

	@NotNull
	private MessageEmbed buildAnnouncementEmbed(@Nullable User user, @Nullable File image, @Nullable File icon) {
		EmbedDescription embedDescription = this.getEmbedDescription();
		List<Field> fields = embedDescription.fields();

		EmbedBuilder builder = new EmbedBuilder()
				.setDescription(embedDescription.description())
				.setColor(this.extension.getColor() == null ? 3265101 : this.extension.getColor());

		if (user != null) {
			builder.setAuthor(user.getEffectiveName(), null, user.getAvatarUrl());
		}
		if (icon != null) {
			builder.setThumbnail("attachment://icon.png");
		}
		if (image != null) {
			builder.setImage(String.format("attachment://%s", image.getName()));
		}
		if (fields != null && !fields.isEmpty()) {
			fields.forEach(builder::addField);
		}

		return builder.build();
	}

	@NotNull
	private EmbedDescription getEmbedDescription() {
		if (this.extension.getLinksFormat() == LinksFormat.BUTTONS || this.map == null) {
			return new EmbedDescription(String.format(
					"# %s\n%s",
					this.extension.getTitle(),
					this.extension.getChangelog()
			), null);
		}

		List<Field> linkFields = this.getLinkFields(this.map);
		return new EmbedDescription(String.format(
				"# %s\n%s\n%s",
				this.extension.getTitle(),
				this.extension.getChangelog(),
				"### Direct Links\n"
		), linkFields);
	}

	@NotNull
	private List<Field> getLinkFields(@NotNull Map<String, Set<String>> map) {
		List<Field> fields = new ArrayList<>();

		for (Entry<String, Set<String>> entry : map.entrySet()) {
			String loader = entry.getKey();

			StringBuilder linksBuilder = new StringBuilder();
			for (String minecraftVersion : entry.getValue()) {
				String linkForLatestVersion = this.getLinkForLatestVersion(minecraftVersion, loader);
				if (linkForLatestVersion == null || linkForLatestVersion.isEmpty()) {
					continue;
				}
				linksBuilder.append(String.format("[%s](%s) ", minecraftVersion, linkForLatestVersion));
			}

			if (linksBuilder.isEmpty()) {
				continue;
			}

			String presentableLoaderName = this.getPresentableLoaderName(loader);
			String lineWithVersions = linksBuilder.toString().trim().replaceAll(" ", " | ");

			fields.add(new Field(presentableLoaderName, lineWithVersions, false));
		}

		if (fields.isEmpty()) {
			fields.add(new Field("Failed to get any links", "Check your console for more info!", false));
		}

		return fields;
	}

	@NotNull
	private String getPresentableLoaderName(String loader) {
		return switch (loader.toLowerCase()) {
			case "fabric" -> "Fabric";
			case "forge" -> "Forge";
			case "neoforge" -> "NeoForge";
			case "quilt" -> "Quilt";
			default -> loader.substring(0, 1).toUpperCase() + loader.substring(1);
		};
	}

	@Nullable
	private String getLinkForLatestVersion(String minecraftVersion, String loader) {
		PriorityPlatform priorityPlatform = this.extension.getPriorityPlatform();
		if (priorityPlatform == null) {
			return null;
		}
		if (priorityPlatform == PriorityPlatform.MODRINTH) {
			return ModrinthAPI.getLinkForLatestModVersion(this.extension.getModrinthModId(), minecraftVersion, loader);
		} else {
			Provider<String> tokenProvider = this.extension.getCurseForgeAPIToken();
			if (tokenProvider == null) {
				return null;
			}
			return CurseForgeAPI.getLinkForLatestModVersion(this.extension.getCurseForgeProjectId(), minecraftVersion, loader, tokenProvider.get());
		}
	}

	@NotNull
	private Collection<ItemComponent> getButtons() {
		Collection<ItemComponent> components = new ArrayList<>();

		if (this.extension.getModrinthLink() != null) {
			components.add(Button.of(ButtonStyle.LINK, this.extension.getModrinthLink(), "Modrinth"));
		}
		if (this.extension.getCurseForgeLink() != null) {
			components.add(Button.of(ButtonStyle.LINK, this.extension.getCurseForgeLink(), "CurseForge"));
		}
		if (this.extension.getGithubLink() != null) {
			components.add(Button.of(ButtonStyle.LINK, this.extension.getGithubLink(), "Github"));
		}

		return components;
	}
}
