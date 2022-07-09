
package de.k7bot.listener;

import de.k7bot.Klassenserver7bbot;
import de.k7bot.commands.NEWHelpCommand;
import de.k7bot.util.LiteSQL;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.PrivateChannel;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class CommandListener extends ListenerAdapter {
	@Override
	public void onMessageReceived(@NotNull MessageReceivedEvent event) {
		if (!Klassenserver7bbot.INSTANCE.exit) {
			String message = event.getMessage().getContentStripped();
			Klassenserver7bbot.INSTANCE.checkpreflist();
			Klassenserver7bbot.INSTANCE.getsyschannell().checkSysChannelList();

			switch (event.getChannelType()) {
			case TEXT: {
				try {
					String prefix = Klassenserver7bbot.INSTANCE.prefixl.get(event.getGuild().getIdLong()).toLowerCase();
					guildMessageRecieved(event, message, prefix);
				} catch (IllegalStateException e) {}
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
		PrivateChannel channel = event.getPrivateChannel();
		
		if(message.getContentStripped().startsWith("-help")) {
			NEWHelpCommand help = new NEWHelpCommand();
			
			help.performCommand(channel, message);
		}
	}

	public void guildMessageRecieved(@NotNull MessageReceivedEvent event, String message, String prefix) {

		TextChannel channel = event.getTextChannel();

		if (message.equalsIgnoreCase("-help")) {

			Klassenserver7bbot.INSTANCE.getCmdMan().perform("help", event.getMember(), channel, event.getMessage());

			inserttoLog("help", LocalDateTime.now(), event.getGuild());

		} else if (message.equalsIgnoreCase("-getprefix")) {

			channel.sendMessage("The prefix for your Guild is: `" + prefix + "`.").queue();

			inserttoLog("getprefix", LocalDateTime.now(), event.getGuild());

		} else {
			if (message.startsWith(prefix) && message.length() != 0) {

				String[] args = message.substring(prefix.length()).split(" ");

				if (args.length > 0) {

					if (Klassenserver7bbot.INSTANCE.getCmdMan().perform(args[0], event.getMember(), channel,
							event.getMessage())) {

						inserttoLog(args[0].replaceAll("'", ""), LocalDateTime.now(), event.getGuild());

					} else {

						channel.sendMessage("`unbekannter Command`").complete().delete().queueAfter(10L,
								TimeUnit.SECONDS);

					}

				}
			}
		}

	}

	private void inserttoLog(String command, LocalDateTime time, Guild guild) {

		LiteSQL sqlite = Klassenserver7bbot.INSTANCE.getDB();

		if (!Klassenserver7bbot.INSTANCE.exit) {
			sqlite.onUpdate("INSERT INTO commandlog(command, guildId, timestamp) VALUES('" + command + "', "
					+ guild.getIdLong() + ", " + time.format(DateTimeFormatter.ofPattern("uuuuMMddHHmmss")) + ")");
		}

	}
}