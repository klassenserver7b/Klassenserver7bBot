package de.k7bot.util.commands.common;

import java.time.OffsetDateTime;

import de.k7bot.HelpCategories;
import de.k7bot.commands.types.ServerCommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

public class MessagetoEmbedCommand implements ServerCommand {
	@Override
	public void performCommand(Member m, TextChannel channel, Message message) {

		String mess = message.getContentRaw().substring(9);

		EmbedBuilder builder = new EmbedBuilder();
		builder.setFooter("Requested by @" + m.getEffectiveName());
		builder.setTimestamp(OffsetDateTime.now());
		builder.setColor(4128512);
		builder.setTitle("@" + m.getEffectiveName() + "'s embed");
		builder.setDescription(mess);

		channel.sendMessageEmbeds(builder.build()).queue();
	}

	@Override
	public String gethelp() {
		return "Sendet die eingegebene Nachricht als Embed.\n - z.B. [prefix]toembed [nachricht]";
	}

	@Override
	public HelpCategories getcategory() {
		return HelpCategories.TOOLS;
	}
}