/**
 *
 */
package de.k7bot.music.commands.common;

import de.k7bot.music.AudioLoadResult;
import de.k7bot.music.MusicController;
import de.k7bot.music.commands.generic.GenericPlayCommand;
import de.k7bot.music.utilities.AudioLoadOption;

/**
 * @author Felix
 *
 */
public class PlayCommand extends GenericPlayCommand {

	/**
	 *
	 */
	public PlayCommand() {
		super();
	}

	@Override
	public String gethelp() {
		return "Spielt den/die ausgewählte/-n Track / Livestream / Playlist.\n - z.B. [prefix]play [url / YouTube Suchbegriff]";
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
