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
public class PlayNextCommand extends GenericPlayCommand {

	/**
	 *
	 */
	public PlayNextCommand() {
		super();
	}

	@Override
	public String gethelp() {
		return "Lädt den/die ausgewählte/-n Track / Livestream / Playlist und fügt ihn/sie als nächste/-n in die Queue ein.\n - z.B. [prefix]playnext [url / YouTube Suchbegriff]";
	}

	@Override
	protected AudioLoadResult generateAudioLoadResult(MusicController controller, String url) {
		return new AudioLoadResult(controller, url, AudioLoadOption.NEXT);
	}

	@Override
	protected GenericPlayCommand getChildClass() {
		return this;
	}
}
