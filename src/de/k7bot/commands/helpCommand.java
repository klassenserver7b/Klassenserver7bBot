
package de.k7bot.commands;

import de.k7bot.Klassenserver7bbot;
import de.k7bot.commands.types.ServerCommand;
import java.time.OffsetDateTime;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.PrivateChannel;
import net.dv8tion.jda.api.entities.TextChannel;

public class helpCommand implements ServerCommand {
	public void performCommand(Member m, TextChannel channel, Message message) {
		message.delete().queue();

		onHelpEmbed(m, channel);

		((Message) channel.sendMessage("** look into your DM's **" + m.getAsMention()
				+ "\n (Only available if you have the option `get DM's from server members` in the `Privacy & Safety` settings enabled!)")
				.complete()).delete().queueAfter(10L, TimeUnit.SECONDS);
	}

	/**
	 * Generates the Helplist for the Bot which contain all commands sorted by their
	 * categorys
	 * 
	 * @param member  The member which requested the help and which will get the
	 *                help per DM
	 * 
	 * @param channel The TextChannel in which the message was written/the
	 *                slashcommand was used
	 */
	public void onHelpEmbed(Member member, TextChannel channel) {
		ConcurrentHashMap<String, ServerCommand> commands = (Klassenserver7bbot.INSTANCE.getCmdMan()).commands;
		ConcurrentHashMap<String, String> help = (Klassenserver7bbot.INSTANCE.getCmdMan()).help;
		ConcurrentHashMap<String, String> category = (Klassenserver7bbot.INSTANCE.getCmdMan()).category;

		StringBuilder strbuild = new StringBuilder();

		strbuild.append("**Allgemein**\n\n");

		commands.forEachKey(0L, name -> {
			if (help.get(name) != null && category.get(name).equalsIgnoreCase("Allgemein")) {
				strbuild.append("`-" + name + "` - " + (String) help.get(name) + "\n\n");
			}
		});

		strbuild.append("**Tools**\n\n");

		commands.forEachKey(0L, name -> {
			if (help.get(name) != null && category.get(name).equalsIgnoreCase("Tools")) {
				strbuild.append("`-" + name + "` - " + (String) help.get(name) + "\n\n");
			}
		});

		strbuild.append("**Musik**\n\n");

		commands.forEachKey(0L, name -> {
			if (help.get(name) != null && category.get(name).equalsIgnoreCase("Musik")) {
				strbuild.append("`-" + name + "` - " + (String) help.get(name) + "\n\n");
			}
		});

		strbuild.append("**Games**\n\n");

		commands.forEachKey(0L, name -> {
			if (help.get(name) != null && category.get(name).equalsIgnoreCase("Games")) {
				strbuild.append("`-" + name + "` - " + (String) category.get(name) + "\n\n");
			}
		});

		strbuild.append("**Moderation**\n\n");

		commands.forEachKey(0L, name -> {
			if (help.get(name) != null && category.get(name).equalsIgnoreCase("Moderation")) {
				strbuild.append("`-" + name + "` - " + (String) help.get(name) + "\n\n");
			}
		});

		EmbedBuilder messbuild = new EmbedBuilder();

		messbuild.setDescription("Befehle auf diesem Server beginnen mit `"
				+ Klassenserver7bbot.INSTANCE.prefixl.get(channel.getGuild().getIdLong()) + "`\n\n"
				+ "**Hilfe zum Bot:**\n\n" + strbuild.toString().trim());

		messbuild.setColor(3066993);
		messbuild.setFooter("by @Klassenserver 7b");
		messbuild.setTitle("Hilfe zum K7Bot");
		messbuild.setTimestamp(OffsetDateTime.now());

		PrivateChannel pm = member.getUser().openPrivateChannel().complete();

		if (pm != null) {

			pm.sendMessageEmbeds(messbuild.build()).queue();

		} else {

			EmbedBuilder build = new EmbedBuilder();

			build.setColor(16711680);
			build.setDescription(
					"Couldn't send you a DM - please check if you have the option `get DM's from server members` in the `Privacy & Safety` settings enabled!");

			channel.sendMessageEmbeds(build.build()).complete().delete().queueAfter(20, TimeUnit.SECONDS);
		}

	}
}
