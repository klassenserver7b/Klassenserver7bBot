package de.klassenserver7b.k7bot.listener;

import java.util.concurrent.TimeUnit;

import de.klassenserver7b.k7bot.Klassenserver7bbot;
import de.klassenserver7b.k7bot.commands.common.HelpCommand;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class SlashCommandListener extends ListenerAdapter {
	HelpCommand help = new HelpCommand();

	@Override
	public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {

		if (!Klassenserver7bbot.getInstance().getslashMan().perform(event)) {
			event.getChannel().sendMessage("`unbekannter Slash-Command`").complete().delete().queueAfter(10L,
					TimeUnit.SECONDS);
		}
	}
}
