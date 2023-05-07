
package de.k7bot.listener;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.k7bot.Klassenserver7bbot;
import de.k7bot.commands.common.HelpCommand;
import de.k7bot.sql.LiteSQL;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.PrivateChannel;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

/**
 * 
 * @author Felix
 *
 */
public class CommandListener extends ListenerAdapter {
	Logger log = LoggerFactory.getLogger(this.getClass());

	@Override
	public void onMessageReceived(@NotNull MessageReceivedEvent event) {
		if (!Klassenserver7bbot.getInstance().isInExit()) {
			String message = event.getMessage().getContentStripped();

			switch (event.getChannelType()) {
			case TEXT: {
				try {
					String prefix = Klassenserver7bbot.getInstance().getPrefixMgr()
							.getPrefix(event.getGuild().getIdLong());

					if (prefix == null) {
						prefix = "-";
					}

					prefix = prefix.toLowerCase();
					guildMessageRecieved(event, message, prefix);
				} catch (IllegalStateException e) {
					log.error(e.getMessage(), e);
				}
				break;
			}
			case PRIVATE: {
				privateMessageRecieved(event, event.getMessage());
				break;
			}

			default:
				break;
			}

		}

	}

	public void privateMessageRecieved(@NotNull MessageReceivedEvent event, Message message) {
		PrivateChannel channel = event.getChannel().asPrivateChannel();

		if (message.getContentStripped().startsWith("-help")) {
			HelpCommand help = new HelpCommand();
			inserttoLog("help", LocalDateTime.now(), 0L, event.getAuthor().getIdLong());
			help.performCommand(channel, message);
		}
	}

	public void guildMessageRecieved(@NotNull MessageReceivedEvent event, String message, String prefix) {

		TextChannel channel = event.getChannel().asTextChannel();

		switch (message) {

		case "-help" -> {
			Klassenserver7bbot.getInstance().getCmdMan().perform("help", event.getMember(), channel,
					event.getMessage());

			inserttoLog("help", LocalDateTime.now(), event.getGuild(), event.getAuthor().getIdLong());
		}

		case "-getprefix" -> {

			channel.sendMessage("The prefix for your Guild is: `" + prefix + "`.").queue();

			inserttoLog("getprefix", LocalDateTime.now(), event.getGuild(), event.getAuthor().getIdLong());

		}

		default -> {

			if (!message.startsWith(prefix) || message.length() == 0) {
				return;
			}

			String[] args = message.substring(prefix.length()).split(" ");

			if (args.length < 1) {
				return;
			}

			int status = Klassenserver7bbot.getInstance().getCmdMan().perform(args[0], event.getMember(), channel,
					event.getMessage());

			switch (status) {
			case 0 -> {
				sendDisabledCommand(channel, args[0]);
			}
			case -1 -> {
				sendUnknownCommand(channel, args[0]);
			}
			}

			inserttoLog(args[0].replaceAll("'", ""), LocalDateTime.now(), event.getGuild(),
					event.getAuthor().getIdLong());

		}

		}

	}

	private void sendDisabledCommand(TextChannel chan, String command) {

		String shortcommand = command;

		if (shortcommand.length() >= 100) {
			shortcommand = shortcommand.substring(0, 99);
			shortcommand += "...";
		}

		chan.sendMessage("`Deaktivierter Command - '" + shortcommand + "'` -> Currently disabled by the Bot-devs!")
				.complete().delete().queueAfter(15L, TimeUnit.SECONDS);
	}

	private void sendUnknownCommand(TextChannel chan, String command) {

		String nearestComm = Klassenserver7bbot.getInstance().getCmdMan().getNearestCommand(command);

		String shortcommand = command;

		if (shortcommand.length() >= 100) {
			shortcommand = shortcommand.substring(0, 99);
			shortcommand += "...";
		}

		chan.sendMessage("`Unbekannter Command - '" + shortcommand + "'` -> Meintest du `" + nearestComm + "`?")
				.complete().delete().queueAfter(15L, TimeUnit.SECONDS);
	}

	private void inserttoLog(String command, LocalDateTime time, Guild guild, Long userid) {

		if (!Klassenserver7bbot.getInstance().isInExit()) {
			LiteSQL.onUpdate("INSERT INTO commandlog(command, guildId, userId, timestamp) VALUES(?, ?, ?, ?);", command,
					guild.getIdLong(), userid, time.format(DateTimeFormatter.ofPattern("uuuuMMddHHmmss")));
		}

	}

	private void inserttoLog(String command, LocalDateTime time, Long guildid, Long userid) {

		if (!Klassenserver7bbot.getInstance().isInExit()) {
			LiteSQL.onUpdate("INSERT INTO commandlog(command, guildId, userId, timestamp) VALUES(?, ?, ?, ?);", command,
					guildid, userid, time.format(DateTimeFormatter.ofPattern("uuuuMMddHHmmss")));
		}

	}
}