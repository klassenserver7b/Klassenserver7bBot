
package de.k7bot.listener;

import de.k7bot.Klassenserver7bbot;
import java.util.concurrent.TimeUnit;

import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Guild;
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

			if (event.isFromType(ChannelType.TEXT)) {
				TextChannel channel = event.getTextChannel();
				Guild guild = event.getGuild();

				if (message.equalsIgnoreCase("-help")) {

					Klassenserver7bbot.INSTANCE.getCmdMan().perform("help", event.getMember(), channel,
							event.getMessage());

				} else if (message.equalsIgnoreCase("-getprefix")) {

					channel.sendMessage("The prefix for your Guild is: `"
							+ Klassenserver7bbot.INSTANCE.prefixl.get(event.getGuild().getIdLong()) + "`.").queue();

				} else {
					if (message.startsWith(Klassenserver7bbot.INSTANCE.prefixl.get(guild.getIdLong()).toLowerCase())
							&& message.length() != 0) {

						String[] args = message
								.substring(Klassenserver7bbot.INSTANCE.prefixl.get(guild.getIdLong()).length())
								.split(" ");

						if (args.length > 0 && !Klassenserver7bbot.INSTANCE.getCmdMan().perform(args[0],
								event.getMember(), channel, event.getMessage())) {

							channel.sendMessage("`unbekannter Command`").complete().delete().queueAfter(10L,
									TimeUnit.SECONDS);

						}
					}
				}
			}
		}
	}
}