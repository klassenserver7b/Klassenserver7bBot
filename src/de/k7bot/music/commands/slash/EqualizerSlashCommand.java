/**
 * 
 */
package de.k7bot.music.commands.slash;

import java.awt.Color;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

import org.jetbrains.annotations.NotNull;

import de.k7bot.Klassenserver7bbot;
import de.k7bot.commands.types.SlashCommand;
import de.k7bot.music.BotEqualizer;
import de.k7bot.music.EqualizerPreset;
import de.k7bot.music.utilities.MusicUtil;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.Command.Choice;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

/**
 * @author Felix
 *
 */
public class EqualizerSlashCommand implements SlashCommand {

	@Override
	public void performSlashCommand(SlashCommandInteraction event) {

		String preset = event.getOption("preset").getAsString();
		InteractionHook hook = event.deferReply(true).complete();

		if (!MusicUtil.checkConditions(hook, event.getMember())) {
			return;
		}

		BotEqualizer eq = BotEqualizer.getEQ(
				Klassenserver7bbot.getInstance().getPlayerUtil().getController(event.getGuild().getIdLong()).getPlayer());

		EqualizerPreset[] vals = EqualizerPreset.values();

		for (int i = 0; i < vals.length; i++) {
			if (vals[i].toString().equalsIgnoreCase(preset)) {
				eq.setEQMode(vals[i]);
				hook.sendMessageEmbeds(new EmbedBuilder().setColor(Color.decode("#00ff00")).setFooter("Provided by @K7Bot").setTimestamp(OffsetDateTime.now()).setDescription("Equalizer was sucessful set to preset '"+preset+"'").build()).queue();
				return;
			}
		}
		
		hook.sendMessageEmbeds(new EmbedBuilder().setColor(Color.decode("#ff0000")).setFooter("Provided by @K7Bot").setTimestamp(OffsetDateTime.now()).setDescription("Something went wrong! Please try again and contact the bot support if this keeps happening.").build()).queue();

	}

	@Override
	public @NotNull SlashCommandData getCommandData() {

		List<Choice> choices = new ArrayList<>();

		EqualizerPreset[] vals = EqualizerPreset.values();

		for (int i = 0; i < vals.length; i++) {

			if (vals[i] != EqualizerPreset.UNKNOWN) {
				choices.add(new Choice(vals[i].toString(), vals[i].toString()));
			}
		}

		return Commands.slash("equalizer", "used to set up the equalizer of the bot.")
				.addOptions(new OptionData(OptionType.STRING, "preset", "please select an equalizer preset.")
						.addChoices(choices).setRequired(true))
				.setGuildOnly(true)
				.setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.VOICE_CONNECT));
	}

}
