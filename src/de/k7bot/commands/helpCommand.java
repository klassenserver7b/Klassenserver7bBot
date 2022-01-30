
package de.k7bot.commands;

import de.k7bot.Klassenserver7bbot;
import de.k7bot.commands.types.ServerCommand;
import java.time.OffsetDateTime;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;

public class helpCommand implements ServerCommand {
	public void performCommand(Member m, TextChannel channel, Message message) {
		message.delete().queue();

		onHelpEmbed(m, channel.getGuild());

		((Message) channel.sendMessage("** look into your DM's **" + m.getAsMention()
				+ "\n (Only available if you have the option `get DM's from server members` in the `Privacy & Safety` settings enabled!)")
				.complete()).delete().queueAfter(10L, TimeUnit.SECONDS);
	}

	public void onHelpEmbed(Member m, Guild g) {
		ConcurrentHashMap<String, ServerCommand> commands = (Klassenserver7bbot.INSTANCE.getCmdMan()).commands;
		ConcurrentHashMap<String, String> help = (Klassenserver7bbot.INSTANCE.getCmdMan()).help;
		ConcurrentHashMap<String, String> category = (Klassenserver7bbot.INSTANCE.getCmdMan()).category;

		StringBuilder strbuild = new StringBuilder();

		strbuild.append("**Allgemein**\n\n");

		commands.forEachKey(0L, name -> {
			if (help.get(name) != null && category.get(name) == "Allgemein") {
				strbuild.append("`-" + name + "` - " + (String) help.get(name) + "\n\n");
			}
		});

		strbuild.append("**Tools**\n\n");

		commands.forEachKey(0L, name -> {
			if (help.get(name) != null && category.get(name) == "Tools") {
				strbuild.append("`-" + name + "` - " + (String) help.get(name) + "\n\n");
			}
		});

		strbuild.append("**Musik**\n\n");

		commands.forEachKey(0L, name -> {
			if (help.get(name) != null && category.get(name) == "Musik") {
				strbuild.append("`-" + name + "` - " + (String) help.get(name) + "\n\n");
			}
		});

		strbuild.append("**Games**\n\n");

		commands.forEachKey(0L, name -> {
			if (help.get(name) == "Games") {
				strbuild.append("`-" + name + "` - " + (String) category.get(name) + "\n\n");
			}
		});

		strbuild.append("**Moderation**\n\n");

		commands.forEachKey(0L, name -> {
			if (help.get(name) != null && category.get(name) == "Moderation") {
				strbuild.append("`-" + name + "` - " + (String) help.get(name) + "\n\n");
			}
		});

		EmbedBuilder messbuild = new EmbedBuilder();

		messbuild.setDescription("Befehle auf diesem Server beginnen mit `" + Klassenserver7bbot.INSTANCE.prefixl.get(g.getIdLong()) + "`\n\n"
				+ "**Hilfe zum Bot:**\n\n" + strbuild.toString().trim());

		messbuild.setColor(3066993);
		messbuild.setFooter("by @Klassenserver 7b");
		messbuild.setTitle("Hilfe zum K7Bot");
		messbuild.setTimestamp(OffsetDateTime.now());

		m.getUser().openPrivateChannel().queue(ch -> ch
				.sendMessageEmbeds(messbuild.build()).queue());
	}
}
