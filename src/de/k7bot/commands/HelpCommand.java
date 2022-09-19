package de.k7bot.commands;

import java.awt.Color;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

import org.jetbrains.annotations.NotNull;

import de.k7bot.Klassenserver7bbot;
import de.k7bot.commands.types.ServerCommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.PrivateChannel;
import net.dv8tion.jda.api.entities.TextChannel;

/**
 * 
 * @author felix
 *
 */
public class HelpCommand implements ServerCommand {

	private static List<String> categories = new ArrayList<>();

	@Override
	public String gethelp() {
		return "Shows the Help for the Bot!";
	}

	@Override
	public String getcategory() {
		return "Allgemein";
	}

	@Override
	public void performCommand(Member m, TextChannel channel, Message mess) {
		String[] args = mess.getContentDisplay().split(" ");

		if (args.length > 1) {
			sendEmbedPrivate(generateHelpforCategory(args[1], channel.getGuild()), m, channel);
		} else {
			sendEmbedPrivate(generateHelpOverview(channel.getGuild()), m, channel);
		}
	}
	
	public void performCommand(Member m, TextChannel channel, String mess) {
		String[] args = mess.split(" ");

		if (args.length > 1) {
			sendEmbedPrivate(generateHelpforCategory(args[1], channel.getGuild()), m, channel);
		} else {
			sendEmbedPrivate(generateHelpOverview(channel.getGuild()), m, channel);
		}
	}

	public void performCommand(PrivateChannel channel, Message mess) {
		String[] args = mess.getContentDisplay().split(" ");

		if (args.length > 1) {
			sendEmbedPrivate(generateHelpforCategory(args[1], null), channel);
		} else {
			sendEmbedPrivate(generateHelpOverview(null), channel);
		}
	}


	/**
	 * 
	 * @param guild
	 * @return
	 */
	public MessageEmbed generateHelpOverview(Guild guild) {

		EmbedBuilder ret = new EmbedBuilder();

		ret.setTitle("Help for the K7Bot");
		ret.setTimestamp(OffsetDateTime.now());
		ret.setColor(3066993);

		StringBuilder strbuild = new StringBuilder();

		if (guild != null) {
			ret.addField("**General**",
					"Befehle auf diesem Server beginnen mit `"
							+ Klassenserver7bbot.INSTANCE.prefixl.get(guild.getIdLong()) + "`\r\n"
							+ "[TEXT] stellt benötigte Commandargumente dar.\r\n"
							+ "<TEXT> stellt optionale Commandargumente dar.\r\n" + "\r\n\r\n",
					false);
		} else {
			ret.addField("**General**",
					"Bot-Befehle beginnen standardmäßig mit `-`\r\n" + "[TEXT] stellt benötigte Commandargumente dar.\r\n"
							+ "<TEXT> stellt optionale Commandargumente dar.\r\n" + "\r\n\r\n",
					false);
		}

		strbuild.append("**The help for the K7Bot is structured in categories**");
		strbuild.append("\r\n\r\n");
		strbuild.append("Use this command like `[prefix]help <category>`");
		strbuild.append("\r\n\r\n");
		strbuild.append("**Valid categorys are:**");
		strbuild.append("\r\n");

		categories.forEach(cat -> {

			strbuild.append("`" + cat + "`");
			strbuild.append("\r\n");

		});

		ret.addField("",strbuild.toString(), false);

		return ret.build();
	}

	/**
	 * 
	 * @param category
	 * @return
	 */
	public MessageEmbed generateHelpforCategory(String category, Guild guild) {

		LinkedHashMap<String, ServerCommand> commands = (Klassenserver7bbot.INSTANCE.getCmdMan()).commands;
		List<Entry<String, ServerCommand>> searchresults = new ArrayList<>();

		int limitmultiplicator = 1;
		int buildlength = 0;
		String prefix;

		EmbedBuilder ret = new EmbedBuilder();

		if (guild != null) {

			prefix = Klassenserver7bbot.INSTANCE.prefixl.get(guild.getIdLong());
			ret.addField("**General**",
					"Befehle auf diesem Server beginnen mit `"
							+ Klassenserver7bbot.INSTANCE.prefixl.get(guild.getIdLong()) + "`\r\n"
							+ "[TEXT] stellt benötigte Commandargumente dar.\r\n"
							+ "<TEXT> stellt optionale Commandargumente dar.\r\n",
					false);

		} else {

			prefix = "-";
			ret.addField("**General**",
					"Bot-Befehle beginnen standardmäßig mit `-`\r\n" + "[TEXT] stellt benötigte Commandargumente dar.\r\n"
							+ "<TEXT> stellt optionale Commandargumente dar.\r\n",
					false);
		}

		if (categories.contains(category)) {

			commands.entrySet().forEach(key -> {
				ServerCommand comm = key.getValue();

				if (comm.getcategory()!=null && comm.getcategory().equalsIgnoreCase(category)) {
					searchresults.add(key);
				}
			});

			StringBuilder categoryHelpStr = new StringBuilder();

			for (int i = 0; i < searchresults.size(); i++) {

				Entry<String, ServerCommand> entry = searchresults.get(i);

				StringBuilder inbuild = new StringBuilder();

				inbuild.append("\r\n");
				inbuild.append("`");
				inbuild.append(prefix);
				inbuild.append(entry.getKey());
				inbuild.append("` - ");
				inbuild.append(entry.getValue().gethelp()+" \r\n");

				if (buildlength + inbuild.toString().length() + 5 >= (1024 * limitmultiplicator+((limitmultiplicator-1)*3))) {

					buildlength = limitmultiplicator * 1024 + inbuild.toString().length();
					limitmultiplicator++;
					categoryHelpStr.append("<@>");
					categoryHelpStr.append(inbuild.toString());

				} else {

					buildlength += inbuild.toString().length();
					categoryHelpStr.append(inbuild.toString());

				}

			}

			String[] helpparts = categoryHelpStr.toString().split("<@>");

			for (int i = 0; i < helpparts.length; i++) {

				ret.addField("", helpparts[i], false);

			}

			ret.setTitle("Help for the K7Bot");
			ret.setTimestamp(OffsetDateTime.now());
			ret.setColor(Color.decode("#14cdc8"));
			
			return ret.build();

		} else {

			ret.setColor(Color.decode("#ff0000"));
			ret.setDescription("There are no commands listed for the submitted category - please check the spelling!");

			return ret.build();
		}

	}

	/**
	 * Updates the List of used Categories by checking all Commands
	 */
	public static void updateCategoryList() {
		LinkedHashMap<String, ServerCommand> commands = (Klassenserver7bbot.INSTANCE.getCmdMan()).commands;

		commands.values().forEach(command -> {

			if (command.getcategory() != null && !categories.contains(command.getcategory())) {

				categories.add(command.getcategory());

			}

		});
	}

	/**
	 * Sends the {@link MessageEmbed Embed} with the HelpMessage to the
	 * {@link net.dv8tion.jda.api.entities.User User} using {@link PrivateChannel
	 * PrivateChannels}
	 * 
	 * @param embed The HelpEmbed to be sent to the user
	 * @param m     The {@link Member} wich is used to get the
	 *              {@link net.dv8tion.jda.api.entities.User User}
	 * @param tc    The {@link TextChannel} in which the reference to the DM and the
	 *              error messages are to be sent.
	 */
	private void sendEmbedPrivate(MessageEmbed embed, Member m, TextChannel tc) {

		PrivateChannel ch = m.getUser().openPrivateChannel().complete();

		if (ch != null) {

			ch.sendMessageEmbeds(embed).queue();

			if (tc != null) {
				tc.sendMessage("** look into your DM's **" + m.getAsMention()).complete().delete().queueAfter(10L,
						TimeUnit.SECONDS);
			}

		} else {

			MessageEmbed errorembed = new EmbedBuilder().setColor(16711680).setDescription(
					"Couldn't send you a DM - please check if you have the option `get DM's from server members` in the `Privacy & Safety` settings enabled!")
					.build();

			if (tc != null) {
				tc.sendMessageEmbeds(errorembed).complete().delete().queueAfter(20, TimeUnit.SECONDS);
			}

		}

	}
	private void sendEmbedPrivate(MessageEmbed embed, @NotNull PrivateChannel ch) {
			ch.sendMessageEmbeds(embed).queue();

	}

}
