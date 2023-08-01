/**
 * 
 */
package de.k7bot.music.commands.slash;

import java.awt.Color;
import java.time.OffsetDateTime;

import com.github.natanbc.lavadsp.timescale.TimescalePcmAudioFilter;

import de.k7bot.Klassenserver7bbot;
import de.k7bot.commands.types.TopLevelSlashCommand;
import de.k7bot.music.utilities.BotAudioEffectsManager;
import de.k7bot.music.utilities.MusicUtil;
import de.k7bot.util.GenericMessageSendHandler;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

/**
 * @author K7
 *
 */
public class SpeedChangeCommand implements TopLevelSlashCommand {

	/**
	 * 
	 */
	public SpeedChangeCommand() {
	}

	@Override
	public void performSlashCommand(SlashCommandInteraction event) {

		InteractionHook hook = event.deferReply(false).complete();

		Member m = event.getMember();

		AudioChannel vc = MusicUtil.getMembVcConnection(m);
		if (!MusicUtil.checkDefaultConditions(new GenericMessageSendHandler(hook), m)) {
			return;
		}

		double speedrate = event.getOption("speedfactor").getAsDouble();
		OptionMapping pitchmap = event.getOption("changepitch");
		boolean changepitch = (pitchmap == null ? true : pitchmap.getAsBoolean());

		BotAudioEffectsManager effman = BotAudioEffectsManager.getAudioEffectsManager(
				Klassenserver7bbot.getInstance().getPlayerUtil().getController(vc.getGuild().getIdLong()).getPlayer());

		effman.setAudioFilterFunction(((track, format, output) -> {

			TimescalePcmAudioFilter timefilter = new TimescalePcmAudioFilter(output, format.channelCount,
					format.sampleRate);

			if (changepitch) {
				timefilter.setRate(speedrate);
			} else {
				timefilter.setSpeed(speedrate);
			}

			return timefilter;
		}));

		hook.sendMessageEmbeds(new EmbedBuilder().setColor(Color.green)
				.setDescription("Successfully applied speed change").setTimestamp(OffsetDateTime.now()).build())
				.queue();
	}

	@Override
	public SlashCommandData getCommandData() {
		return Commands.slash("speedchange", "changes the speed of the currently played audio track").addOptions(
				new OptionData(OptionType.NUMBER, "speedfactor", "factor to multyply the speed with e.g. 1.5", true)
						.setRequiredRange(0, 2),
				new OptionData(OptionType.BOOLEAN, "changepitch", "whether the pitch should be changed default: true",
						false));
	}

}
