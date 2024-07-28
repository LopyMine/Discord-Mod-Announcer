package net.lopymine.dma.options;

import lombok.Getter;

@Getter
public enum PriorityPlatform {
	MODRINTH("Modrinth"),
	CURSEFORGE("CurseForge");

	private final String prettyName;

	PriorityPlatform(String prettyName) {
		this.prettyName = prettyName;
	}
}
