package de.k7bot.listener;

import javax.annotation.Nonnull;

import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class ChartsAutocomplete extends ListenerAdapter {

	public void onCommandAutoCompleteInteraction(@Nonnull CommandAutoCompleteInteractionEvent event) {

		if (event.getName().equalsIgnoreCase("charts")) {

			event.replyChoiceStrings("DAYS","MONTHS","YEARS").queue();
			
		}

	}
}
