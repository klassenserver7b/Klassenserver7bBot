/**
 * 
 */
package de.klassenserver7b.k7bot.commands.slash;

import java.awt.Color;
import java.time.OffsetDateTime;
import java.util.List;

import de.klassenserver7b.k7bot.commands.types.TopLevelSlashCommand;
import de.klassenserver7b.k7bot.util.VplanRoomChecker;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

/**
 * 
 */
public class SearchForRoomSlashCommand implements TopLevelSlashCommand {

	/**
	 * 
	 */
	public SearchForRoomSlashCommand() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void performSlashCommand(SlashCommandInteraction event) {
		InteractionHook hook = event.deferReply().complete();

		long lesson = event.getOption("lesson").getAsLong();

		List<String> rooms = VplanRoomChecker.checkDefaults(lesson);

		if (rooms.isEmpty()) {
			hook.sendMessageEmbeds(new EmbedBuilder().setColor(Color.decode("#dd2222"))
					.setDescription("I'm sorry but all rooms are already taken").setTimestamp(OffsetDateTime.now())
					.build()).queue();
			return;
		}

		StringBuilder strbuild = new StringBuilder();

		for (String room : rooms) {
			strbuild.append(room);
			strbuild.append(", ");
		}
		
		strbuild.delete(strbuild.length() - 2, strbuild.length());

		hook.sendMessageEmbeds(new EmbedBuilder().setColor(Color.green)
				.setDescription("I'm happy to tell you that the rooms " + strbuild.toString() + " are free!")
				.setTimestamp(OffsetDateTime.now()).build()).queue();

	}

	@Override
	public SlashCommandData getCommandData() {
		return Commands.slash("searchroom", "Searchs for a free room in the selected lesson")
				.addOptions(new OptionData(OptionType.INTEGER, "lesson", "the lesson to check", true));
	}

}
