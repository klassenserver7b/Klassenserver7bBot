/**
 * 
 */
package de.klassenserver7b.k7bot.music.commands.slash;

import de.klassenserver7b.k7bot.Klassenserver7bbot;
import de.klassenserver7b.k7bot.commands.types.TopLevelSlashCommand;
import de.klassenserver7b.k7bot.music.utilities.BotAudioEffectsManager;
import de.klassenserver7b.k7bot.music.utilities.MusicUtil;
import de.klassenserver7b.k7bot.util.EmbedUtils;
import de.klassenserver7b.k7bot.util.GenericMessageSendHandler;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

/**
 * 
 */
public class ClearAudioFilterFilterSlashCommand implements TopLevelSlashCommand {

	/**
	 * 
	 */
	public ClearAudioFilterFilterSlashCommand() {

	}

	@Override
	public void performSlashCommand(SlashCommandInteraction event) {

		InteractionHook hook = event.deferReply(false).complete();

		Member m = event.getMember();

		AudioChannel vc = MusicUtil.getMembVcConnection(m);
		if (!MusicUtil.checkDefaultConditions(new GenericMessageSendHandler(hook), m)) {
			return;
		}

		BotAudioEffectsManager effman = BotAudioEffectsManager.getAudioEffectsManager(
				Klassenserver7bbot.getInstance().getPlayerUtil().getController(vc.getGuild().getIdLong()).getPlayer());

		effman.clearFilters();

		hook.sendMessageEmbeds(EmbedUtils
				.getSuccessEmbed("Successfully removed all AudioFilters", event.getGuild().getIdLong()).build())
				.queue();

	}

	@Override
	public SlashCommandData getCommandData() {
		return Commands.slash("audiofilterclear", "removes all audio filters from the current player")
				.setGuildOnly(true);
	}

}
