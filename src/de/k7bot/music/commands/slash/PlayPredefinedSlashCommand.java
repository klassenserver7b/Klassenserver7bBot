/**
 * 
 */
package de.k7bot.music.commands.slash;

import java.util.ArrayList;

import de.k7bot.Klassenserver7bbot;
import de.k7bot.commands.types.SubSlashCommand;
import de.k7bot.music.commands.generic.GenericPlayCommand;
import de.k7bot.music.lavaplayer.AudioLoadResult;
import de.k7bot.music.lavaplayer.MusicController;
import de.k7bot.music.utilities.AudioLoadOption;
import de.k7bot.music.utilities.MusicUtil;
import de.k7bot.music.utilities.PredefinedMusicPlaylists;
import de.k7bot.util.GenericMessageSendHandler;
import de.k7bot.util.errorhandler.SyntaxError;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;
import net.dv8tion.jda.api.interactions.commands.Command.Choice;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;

/**
 * @author Felix
 *
 */
public class PlayPredefinedSlashCommand extends GenericPlayCommand implements SubSlashCommand {

	@Override
	public SubcommandData getSubCommandData() {

		ArrayList<Choice> playlists = new ArrayList<>();
		for (PredefinedMusicPlaylists q : PredefinedMusicPlaylists.values()) {
			playlists.add(new Choice(q.toString(), q.getId()));
		}
		OptionData playlist = new OptionData(OptionType.INTEGER, "playlist", "a predefined playlist")
				.addChoices(playlists).setRequired(true);

		return new SubcommandData("predefined", "for our predefined playlists").addOptions(playlist);
	}

	@Override
	public void performSlashCommand(SlashCommandInteraction event) {
		InteractionHook hook = event.deferReply(true).complete();
		Member m = event.getMember();

		if (event.getOptions().isEmpty()) {
			SyntaxError.oncmdSyntaxError(new GenericMessageSendHandler(hook), gethelp(), m);
			return;
		}

		AudioChannel vc = MusicUtil.getMembVcConnection(m);
		MusicController controller = Klassenserver7bbot.getInstance().getPlayerUtil()
				.getController(vc.getGuild().getIdLong());

		super.performInternalChecks(m, vc, controller, new GenericMessageSendHandler(hook));

		OptionMapping predef = event.getOption("playlist");
		String url = PredefinedMusicPlaylists.fromId(predef.getAsInt()).getUrl();

		super.performItemLoad(url, controller, vc.getName());

		hook.sendMessage("Successfully Loaded").queue();
	}

	@Override
	public String getSubPath() {
		return "predefined";
	}

	@Override
	public String gethelp() {
		return null;
	}

	@Override
	protected AudioLoadResult generateAudioLoadResult(MusicController controller, String url) {
		return new AudioLoadResult(controller, url, AudioLoadOption.REPLACE);
	}

	@Override
	protected GenericPlayCommand getChildClass() {
		return this;
	}

}
