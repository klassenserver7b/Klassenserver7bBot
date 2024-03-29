package de.klassenserver7b.k7bot.commands.common;

import de.klassenserver7b.k7bot.HelpCategories;
import de.klassenserver7b.k7bot.Klassenserver7bbot;
import de.klassenserver7b.k7bot.commands.types.ServerCommand;
import de.klassenserver7b.k7bot.util.EmbedUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.concrete.PrivateChannel;
import net.dv8tion.jda.api.entities.channel.middleman.GuildMessageChannel;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author K7
 *
 */

public class HelpCommand implements ServerCommand {

	private boolean isEnabled;

	@Override
	public String gethelp() {
		return "Shows the Help for the Bot!";
	}

	@Override
	public String[] getCommandStrings() {
		return new String[] { "help" };
	}

	@Override
	public HelpCategories getcategory() {
		return HelpCategories.ALLGEMEIN;
	}

	@Override
	public void performCommand(Member m, GuildMessageChannel channel, Message mess) {
		String[] args = mess.getContentDisplay().split(" ");

		if (args.length > 1) {
			sendEmbedPrivate(generateHelpforCategory(args[1], channel.getGuild()), m, channel);
		} else {
			sendEmbedPrivate(generateHelpOverview(channel.getGuild()), m, channel);
		}
	}

	public void performCommand(Member m, GuildMessageChannel channel, String mess) {
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
	 *
	 *
	 */
	public MessageEmbed generateHelpOverview(Guild guild) {

		EmbedBuilder ret = EmbedUtils.getBuilderOf(Color.decode("#33CC66"), (guild != null ? guild.getIdLong() : null));
		ret.setTitle("Help for the K7Bot");

		StringBuilder strbuild = new StringBuilder();

		if (guild != null) {
			ret.addField("**General**",
					"Befehle auf diesem Server beginnen mit `"
							+ Klassenserver7bbot.getInstance().getPrefixMgr().getPrefix(guild.getIdLong()) + "`\r\n"
							+ "[TEXT] stellt benötigte Commandargumente dar.\r\n"
							+ "<TEXT> stellt optionale Commandargumente dar.\r\n" + "\r\n\r\n",
					false);
		} else {
			ret.addField("**General**",
					"Bot-Befehle beginnen standardmaßig mit `-`\r\n"
							+ "[TEXT] stellt benötigte Commandargumente dar.\r\n"
							+ "<TEXT> stellt optionale Commandargumente dar.\r\n" + "\r\n\r\n",
					false);
		}

		strbuild.append("**The help for the K7Bot is structured in categories**");
		strbuild.append("\r\n\r\n");
		strbuild.append("Use this command like `[prefix]help <category>`");
		strbuild.append("\r\n\r\n");
		strbuild.append("**Valid categorys are:**");
		strbuild.append("\r\n");

		for (HelpCategories cat : HelpCategories.values()) {

			if (cat == HelpCategories.UNKNOWN || cat == HelpCategories.OVERVIEW) {
				continue;
			}

			strbuild.append("`" + cat.toString() + "`");
			strbuild.append("\r\n");

		}

		ret.setDescription(strbuild);

		ret.addField("", strbuild.toString(), false);

		return ret.build();
	}

	/**
	 * 
	 * @param catstr
	 * @param guild
	 * @return
	 */
	public MessageEmbed generateHelpforCategory(String catstr, Guild guild) {

		ArrayList<ServerCommand> commands = (Klassenserver7bbot.getInstance().getCmdMan()).getCommands();
		List<ServerCommand> searchresults = new ArrayList<>();

		int limitmultiplicator = 1;
		int buildlength = 0;
		String prefix;

		EmbedBuilder ret = EmbedUtils.getBuilderOf(Color.decode("#14cdc8"));

		HelpCategories cat;

		try {

			cat = HelpCategories.valueOf(catstr.trim().toUpperCase());

		} catch (IllegalArgumentException e) {

			ret.setColor(Color.red);
			ret.setDescription("There are no commands listed for the submitted category - please check the spelling!");

			return ret.build();

		}

		if (guild != null) {

			prefix = Klassenserver7bbot.getInstance().getPrefixMgr().getPrefix(guild.getIdLong());
			ret.addField("**General**",
					"Befehle auf diesem Server beginnen mit `"
							+ Klassenserver7bbot.getInstance().getPrefixMgr().getPrefix(guild.getIdLong()) + "`\r\n"
							+ "[TEXT] stellt benötigte Commandargumente dar.\r\n"
							+ "<TEXT> stellt optionale Commandargumente dar.\r\n",
					false);

		} else {

			prefix = "-";
			ret.addField("**General**",
					"Bot-Befehle beginnen standardmäßig mit `-`\r\n"
							+ "[TEXT] stellt benötigte Commandargumente dar.\r\n"
							+ "<TEXT> stellt optionale Commandargumente dar.\r\n",
					false);
		}

		commands.forEach(servercommand -> {

			if (servercommand.getcategory() != null && servercommand.getcategory() == cat) {
				searchresults.add(servercommand);
			}
		});

		StringBuilder categoryHelpStr = new StringBuilder();

		for (ServerCommand servercommand : searchresults) {

			StringBuilder inbuild = new StringBuilder();

			inbuild.append("\r\n");
			for (String s : servercommand.getCommandStrings()) {
				inbuild.append("`");
				inbuild.append(prefix);
				inbuild.append(s);
				inbuild.append("` ");
			}
			inbuild.append("- ");
			inbuild.append(servercommand.gethelp() + " \r\n");

			if (buildlength + inbuild.toString().length()
					+ 5 >= (1024 * limitmultiplicator + ((limitmultiplicator - 1) * 3))) {

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

		for (String helppart : helpparts) {

			ret.addField("", helppart, false);

		}

		ret.setTitle("Help for the K7Bot");

		return ret.build();

	}

	/**
	 * Sends the {@link MessageEmbed Embed} with the HelpMessage to the
	 * {@link net.dv8tion.jda.api.entities.User User} using {@link PrivateChannel
	 * PrivateChannels}
	 *
	 * @param embed The HelpEmbed to be sent to the user
	 * @param m     The {@link Member} wich is used to get the
	 *              {@link net.dv8tion.jda.api.entities.User User}
	 * @param tc    The {@link GuildMessageChannel} in which the reference to the DM and the
	 *              error messages are to be sent.
	 *
	 *
	 */
	private void sendEmbedPrivate(MessageEmbed embed, Member m, GuildMessageChannel tc) {

		PrivateChannel ch = m.getUser().openPrivateChannel().complete();

		if (ch != null) {

			ch.sendMessageEmbeds(embed).queue();

			if (tc != null) {
				tc.sendMessage("** look into your DM's **" + m.getAsMention()).complete().delete().queueAfter(10L,
						TimeUnit.SECONDS);
			}

		} else {

			MessageEmbed errorembed = EmbedUtils.getErrorEmbed(
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

	@Override
	public boolean isEnabled() {
		return isEnabled;
	}

	@Override
	public void disableCommand() {
		isEnabled = false;
	}

	@Override
	public void enableCommand() {
		isEnabled = true;
	}

}
