package de.k7bot.listener;

import de.k7bot.Klassenserver7bbot;
import de.k7bot.commands.helpCommand;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.concurrent.TimeUnit;

public class SlashCommandListener extends ListenerAdapter {
	helpCommand help = new helpCommand();

	@Override
	public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
		
		if (event.getGuild() == null) {
			return;
		}

		Klassenserver7bbot.INSTANCE.getsyschannell().checkSysChannelList();
		
		if (!Klassenserver7bbot.INSTANCE.getslashMan().perform(event)) {
			event.getChannel().sendMessage("`unbekannter Slash-Command`").complete().delete()
					.queueAfter(10L, TimeUnit.SECONDS);
		}
	}
}
