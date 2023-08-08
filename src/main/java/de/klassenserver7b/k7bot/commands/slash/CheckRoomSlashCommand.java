/**
 * 
 */
package de.klassenserver7b.k7bot.commands.slash;

import java.awt.Color;

import de.klassenserver7b.k7bot.commands.types.TopLevelSlashCommand;
import de.klassenserver7b.k7bot.util.EmbedUtils;
import de.klassenserver7b.k7bot.util.VplanRoomChecker;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

/**
 * 
 */
public class CheckRoomSlashCommand implements TopLevelSlashCommand {

	/**
	 * 
	 */
	public CheckRoomSlashCommand() {
	}

	@Override
	public void performSlashCommand(SlashCommandInteraction event) {

		String room = event.getOption("room").getAsString().replaceAll("[Rr]", "");
		long lesson = event.getOption("lesson").getAsLong();

		if (lesson <= 0 || room.isBlank()) {
			event.replyEmbeds(EmbedUtils.getErrorEmbed("Invalid Data submitted!").build())
					.queue();
		}

		InteractionHook hook = event.deferReply().complete();

		if (!VplanRoomChecker.isRoomFree(lesson, room)) {
			hook.sendMessageEmbeds(EmbedUtils
					.getBuilderOf(Color.decode("#bd7604"), "I'm sorry but this room is already taken").build()).queue();

			return;
		}

		hook.sendMessageEmbeds(EmbedUtils.getSuccessEmbed("I'm happy to tell you that this room is free!").build())
				.queue();

	}

	@Override
	public SlashCommandData getCommandData() {
		return Commands.slash("checkroom", "Checks if the selected room is free in the selected lesson").addOptions(
				new OptionData(OptionType.INTEGER, "lesson", "the lesson to check", true),
				new OptionData(OptionType.STRING, "room", "the room to check", true));
	}

}
