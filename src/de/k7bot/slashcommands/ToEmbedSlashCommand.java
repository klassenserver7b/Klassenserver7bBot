package de.k7bot.slashcommands;

import java.time.OffsetDateTime;

import java.awt.Color;

import de.k7bot.commands.types.SlashCommand;
import de.k7bot.util.PermissionError;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;

public class ToEmbedSlashCommand implements SlashCommand {

	@Override
	public void performSlashCommand(SlashCommandInteraction event) {

		if (event.getMember().hasPermission(Permission.MESSAGE_SEND)) {

			OptionMapping title = event.getOption("title");
			OptionMapping description = event.getOption("description");
			OptionMapping coloropt = event.getOption("color");

			InteractionHook hook = event.deferReply().complete();

			EmbedBuilder builder = new EmbedBuilder();
			builder.setTimestamp(OffsetDateTime.now());
			builder.setTitle(title.getAsString());
			builder.setDescription(description.getAsString());
			builder.setFooter("requested by @" + event.getMember().getEffectiveName());

			if (coloropt != null) {

				String colortxt;
				colortxt = coloropt.getAsString();
				
				if (!colortxt.startsWith("#")) {
					colortxt = "#" + colortxt;
				}

				Color color = Color.decode(colortxt);
				builder.setColor(color);

			}

			hook.sendMessageEmbeds(builder.build()).queue();

		} else {

			PermissionError.onPermissionError(event.getMember(), event.getTextChannel());
		}

	}

}
