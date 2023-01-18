/**
 * 
 */
package de.k7bot.music.commands.slash;

import java.util.ArrayList;

import de.k7bot.commands.types.SubSlashCommand;
import de.k7bot.music.AudioLoadResult;
import de.k7bot.music.MusicController;
import de.k7bot.music.commands.generic.GenericPlayCommand;
import de.k7bot.music.utilities.AudioLoadOption;
import de.k7bot.music.utilities.PredefinedMusicPlaylists;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.Command.Choice;
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
