package net.lopymine.dma;

import net.dv8tion.jda.api.entities.MessageEmbed.Field;

import java.util.List;

public record EmbedDescription(String description, List<Field> fields) {

}
