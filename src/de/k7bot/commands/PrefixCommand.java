package de.k7bot.commands;

import de.k7bot.Klassenserver7bbot;
import de.k7bot.commands.types.ServerCommand;
import de.k7bot.util.PermissionError;
import de.k7bot.util.SyntaxError;

import java.time.OffsetDateTime;
import java.util.concurrent.TimeUnit;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;

public class PrefixCommand implements ServerCommand {
	public void performCommand(Member m, TextChannel channel, Message message) {

		if (m.hasPermission(Permission.ADMINISTRATOR)) {
			String[] args = message.getContentDisplay().split(" ");
			if (args.length > 1) {
				Klassenserver7bbot.INSTANCE.prefixl.put(channel.getGuild().getIdLong(), args[1]);
				Klassenserver7bbot.INSTANCE.getDB().onUpdate("UPDATE botutil SET prefix = '" + args[1]
						+ "' WHERE guildId = " + channel.getGuild().getIdLong());
				EmbedBuilder builder = new EmbedBuilder();
				builder.setFooter("Requested by @" + m.getEffectiveName());
				builder.setTimestamp(OffsetDateTime.now());
				builder.setTitle("Prefix was set to \"" + args[1] + "\"");
				(channel.sendMessageEmbeds(builder.build()).complete()).delete().queueAfter(10L, TimeUnit.SECONDS);
			} else {

				SyntaxError.oncmdSyntaxError(channel, "prefix [String]", m);
			}
		} else {
			PermissionError.onPermissionError(m, channel);
		}
	}

	@Override
	public String gethelp() {
		String help = "Ändert das Prefix des Bots auf diesem Server.\n - z.B. [prefix][new prefix] '-'";
		return help;
	}

	@Override
	public String getcategory() {
		String category = "Tools";
		return category;
	}
}