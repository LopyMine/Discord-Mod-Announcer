package net.lopymine.dma;


import lombok.*;
import org.gradle.api.provider.Provider;
import org.gradle.api.tasks.*;

import java.io.File;

@Setter
@Getter
public class DMAExtension {

	AnnounceMode ENABLE = AnnounceMode.ENABLE;
	AnnounceMode DISABLE = AnnounceMode.DISABLE;
	AnnounceMode TEST = AnnounceMode.TEST;

	@Input
	Provider<String> token;
	@Input
	AnnounceMode announceMode;
	@InputFile
	File icon;

	@Input
	String title;
	@Input
	String changelog;
	@Input
	Integer color;

	@Input
	String modrinthLink;
	@Input
	String curseForgeLink;
	@Input
	String githubLink;


	@Input
	String uploaderId;
	@Input
	String announcementChannelId;
	@Input
	String testAnnouncementChannelId;
	@Input
	String pingRoleName;
}
