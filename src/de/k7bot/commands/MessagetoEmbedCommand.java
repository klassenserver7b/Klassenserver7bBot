package de.k7bot.commands;

import de.k7bot.commands.types.ServerCommand;
import java.time.OffsetDateTime;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;

public class MessagetoEmbedCommand implements ServerCommand {
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
		String help = "Sendet die eingegebene Nachricht als Embed.\n - z.B. [prefix]toembed [nachricht]";
		return help;
	}

	@Override
	public String getcategory() {
		String category = "Tools";
		return category;
	}
}