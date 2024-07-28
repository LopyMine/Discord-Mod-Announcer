package net.lopymine.dma;

import lombok.*;
import org.gradle.api.provider.Provider;
import org.gradle.api.tasks.*;

import net.lopymine.dma.options.*;

import java.io.File;
import java.util.List;
import org.jetbrains.annotations.*;

@Setter
@Getter
public class DMAExtension {

	AnnounceMode ENABLE = AnnounceMode.ENABLE;
	AnnounceMode DISABLE = AnnounceMode.DISABLE;
	AnnounceMode TEST = AnnounceMode.TEST;

	LinksFormat BUTTONS = LinksFormat.BUTTONS;
	LinksFormat VERSIONED = LinksFormat.VERSIONED;

	PriorityPlatform MODRINTH = PriorityPlatform.MODRINTH;
	PriorityPlatform CURSEFORGE = PriorityPlatform.CURSEFORGE;

	@Nullable
	@Input
	LinksFormat linksFormat;
	@Nullable
	@Input
	PriorityPlatform priorityPlatform;
	@Nullable
	@Input
	String minecraftVersion;
	@Nullable
	@Input
	String loader;
	@Nullable
	@Input
	String modrinthModId;
	@Nullable
	@Input
	Integer curseForgeProjectId;
	@Nullable
	@Input
	Provider<String> curseForgeAPIToken;

	@NotNull
	@Input
	Provider<String> token;
	@Nullable
	@Input
	AnnounceMode announceMode;
	@Nullable
	@InputFile
	File icon;

	@NotNull
	@Input
	String title;
	@Nullable
	@Input
	String showcaseThreadTitle;
	@NotNull
	@Input
	String changelog;
	@Nullable
	@Input
	Integer color;
	@Nullable
	@InputFiles
	List<File> showcaseImages;

	@Nullable
	@Input
	String modrinthLink;
	@Nullable
	@Input
	String curseForgeLink;
	@Nullable
	@Input
	String githubLink;

	@Nullable
	@Input
	String uploaderId;
	@NotNull
	@Input
	String announcementChannelId;
	@NotNull
	@Input
	String testAnnouncementChannelId;
	@Nullable
	@Input
	List<String> pingRoles;
}
