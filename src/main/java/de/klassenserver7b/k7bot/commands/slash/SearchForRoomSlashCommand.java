/**
 *
 */
package de.klassenserver7b.k7bot.commands.slash;

import de.klassenserver7b.k7bot.commands.types.TopLevelSlashCommand;
import de.klassenserver7b.k7bot.util.EmbedUtils;
import de.klassenserver7b.k7bot.util.VplanDBUtils;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.List;
import java.util.Objects;

/**
 *
 */
public class SearchForRoomSlashCommand implements TopLevelSlashCommand {

    @Override
    public void performSlashCommand(SlashCommandInteraction event) {
        InteractionHook hook = event.deferReply().complete();

        long lesson = Objects.requireNonNull(event.getOption("lesson")).getAsLong();

        List<String> rooms = VplanDBUtils.checkDefaultRooms(lesson);

        if (rooms.isEmpty()) {
            hook.sendMessageEmbeds(EmbedUtils.getBuilderOf(Color.decode("#bd7604"),
                    "I'm sorry but all rooms are already taken", Objects.requireNonNull(event.getGuild()).getIdLong()).build()).queue();
            return;
        }

		hook.sendMessageEmbeds(
				EmbedUtils.getSuccessEmbed("I'm happy to tell you that the rooms " + strbuild + " are free!",
						Objects.requireNonNull(event.getGuild()).getIdLong()).build())
				.queue();

    }

    @NotNull
    @Override
    public SlashCommandData getCommandData() {
        return Commands.slash("searchroom", "Searchs for a free room in the selected lesson")
                .addOptions(new OptionData(OptionType.INTEGER, "lesson", "the lesson to check", true));
    }

}
