/**
 *
 */
package de.klassenserver7b.k7bot.commands.slash;

import de.klassenserver7b.k7bot.commands.types.TopLevelSlashCommand;
import de.klassenserver7b.k7bot.util.EmbedUtils;
import de.klassenserver7b.k7bot.util.VplanDBUtils;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.HashMap;
import java.util.Objects;

/**
 *
 */
public class SearchForTeacherSlashCommand implements TopLevelSlashCommand {

    @Override
    public void performSlashCommand(SlashCommandInteraction event) {
        InteractionHook hook = event.deferReply().complete();

        String teacher = Objects.requireNonNull(event.getOption("teacher")).getAsString();

        OptionMapping lessonMapping = event.getOption("lesson");
        CharSequence result;

        if (lessonMapping != null) {
            result = VplanDBUtils.getTeacherRoomByLesson(teacher, lessonMapping.getAsLong());
        } else {
            result = findAllRooms(teacher);
        }

        if (result == null || result.isEmpty()) {
            hook.sendMessageEmbeds(EmbedUtils.getBuilderOf(Color.decode("#bd7604"),
                    "I'm sorry but the teacher has no lessons today", Objects.requireNonNull(hook.getInteraction().getGuild()).getIdLong()).build()).queue();
            return;

        }

        hook.sendMessageEmbeds(
                        EmbedUtils.getSuccessEmbed("Found teacher " + teacher + " in the following rooms: " + result,
                                Objects.requireNonNull(event.getGuild()).getIdLong()).build())
                .queue();

    }

    protected CharSequence findAllRooms(String teacher) {

        HashMap<String, Long> rooms = VplanDBUtils.getTeacherRooms(teacher);

        StringBuilder output = new StringBuilder();

        rooms.forEach((lesson, room) -> output
                .append("Lesson: ")
                .append(lesson)
                .append("; Room: ")
                .append(room)
                .append("\n"));

        return output;

    }

    @NotNull
    @Override
    public SlashCommandData getCommandData() {
        return Commands.slash("searchteacher", "Searchs for the room of the selected teacher")
                .addOptions(new OptionData(OptionType.INTEGER, "teacher", "the teacher (acronym) to check ", true), new OptionData(OptionType.INTEGER, "lesson", "the lesson to check", false));
    }

}
