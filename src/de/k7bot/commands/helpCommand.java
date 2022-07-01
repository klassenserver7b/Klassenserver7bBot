
package de.k7bot.commands;

import de.k7bot.Klassenserver7bbot;
import de.k7bot.commands.types.ServerCommand;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.PrivateChannel;
import net.dv8tion.jda.api.entities.TextChannel;

public class helpCommand implements ServerCommand {
	private int limitmultiplicator = 1;
	private int buildlength = 0;

	public void performCommand(Member m, TextChannel channel, Message message) {

		onHelpEmbed(m, channel);

		channel.sendMessage("** look into your DM's **" + m.getAsMention()
				+ "\n (Only available if you have the option `get DM's from server members` in the `Privacy & Safety` settings enabled!)")
				.complete().delete().queueAfter(10L, TimeUnit.SECONDS);
	}

	@Override
	public String gethelp() {

		return "Zeigt diese Hilfe an.";
	}

	@Override
	public String getcategory() {

		return "Allgemein";
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
		List<String> categories = new ArrayList<>();

		EmbedBuilder messbuild = new EmbedBuilder();

		commands.forEachValue(0, command -> {

			if (command.getcategory() != null && !categories.contains(command.getcategory())) {

				categories.add(command.getcategory());

			}

		});

		// adds the headline to the Embed
		messbuild.addField("**General**",
				"Befehle auf diesem Server beginnen mit `"
						+ Klassenserver7bbot.INSTANCE.prefixl.get(channel.getGuild().getIdLong()) + "`\n"
						+ "[TEXT] stellt benötigte Commandargumente dar.\n"
						+ "<TEXT> stellt optionale Commandargumente dar.\n" + "\n\n",
				false);

		categories.forEach(cat -> {

			StringBuilder build = new StringBuilder();

			commands.forEachKey(0, key -> {

				String help = commands.get(key).gethelp();

				if (commands.get(key).getcategory() != null && commands.get(key).getcategory().equalsIgnoreCase(cat)
						&& help != null) {

					StringBuilder inbuild = new StringBuilder();

					inbuild.append("");
					inbuild.append("`");
					inbuild.append(Klassenserver7bbot.INSTANCE.prefixl.get(channel.getGuild().getIdLong()));
					inbuild.append(key);
					inbuild.append("` - ");
					inbuild.append(help);
					inbuild.append("\n\n|");

					if (buildlength + inbuild.length() + 5 >= 1024 * limitmultiplicator) {

						buildlength = buildlength + inbuild.length();
						limitmultiplicator++;
						build.append("<@>");
						build.append(inbuild.toString().trim());

					} else {

						buildlength += inbuild.length();
						build.append(inbuild.toString().trim());

					}

				}

			});

			String[] helpparts = build.toString().split("<@>");

			System.out.println(helpparts.length + "; " + cat);

			for (int i = 0; i < helpparts.length; i++) {

				// System.out.println("length: " + helpparts[i].length());

				if (i == 0) {
					messbuild.addField("**" + cat + "**", helpparts[i], false);
				} else {
					messbuild.addField("", helpparts[i], false);
				}
			}

			limitmultiplicator = 1;
			buildlength = 0;
		});

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

		limitmultiplicator = 1;
		buildlength = 0;

	}
}
