
package de.k7bot.listener;

import de.k7bot.Klassenserver7bbot;
import de.k7bot.sql.LiteSQL;
import de.k7bot.commands.HelpCommand;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.PrivateChannel;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CommandListener extends ListenerAdapter {
	Logger log = LoggerFactory.getLogger(this.getClass());
	
	@Override
	public void onMessageReceived(@NotNull MessageReceivedEvent event) {
		if (!Klassenserver7bbot.INSTANCE.isInExit()) {
			String message = event.getMessage().getContentStripped();
			Klassenserver7bbot.INSTANCE.checkpreflist();
			Klassenserver7bbot.INSTANCE.getsyschannell().checkSysChannelList();

			switch (event.getChannelType()) {
			case TEXT: {
				try {
					String prefix = Klassenserver7bbot.INSTANCE.getPrefixList().get(event.getGuild().getIdLong()).toLowerCase();
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
		
		if(message.getContentStripped().startsWith("-help")) {
			HelpCommand help = new HelpCommand();
			
			help.performCommand(channel, message);
		}
	}

	public void guildMessageRecieved(@NotNull MessageReceivedEvent event, String message, String prefix) {

		TextChannel channel = event.getChannel().asTextChannel();

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

		if (!Klassenserver7bbot.INSTANCE.isInExit()) {
			LiteSQL.onUpdate("INSERT INTO commandlog(command, guildId, timestamp) VALUES('" + command + "', "
					+ guild.getIdLong() + ", " + time.format(DateTimeFormatter.ofPattern("uuuuMMddHHmmss")) + ")");
		}

	}
}