/**
 *
 */
package de.k7bot.music.commands.common;

import de.k7bot.music.commands.generic.GenericPlayCommand;
import de.k7bot.music.lavaplayer.AudioLoadResult;
import de.k7bot.music.lavaplayer.MusicController;
import de.k7bot.music.utilities.AudioLoadOption;

/**
 * @author Felix
 *
 */
public class AddQueueTrackCommand extends GenericPlayCommand {

	/**
	 *
	 */
	public AddQueueTrackCommand() {
		super();
	}

	@Override
	public String gethelp() {
		return "Lädt den/die ausgewählte/-n Track / Livestream / Playlist und fügt ihn/sie der aktuellen Queue hinzu.\n - z.B. [prefix]addtoqueue [url / YouTube Suchbegriff]";
	}

	@Override
	protected AudioLoadResult generateAudioLoadResult(MusicController controller, String url) {
		return new AudioLoadResult(controller, url, AudioLoadOption.APPEND);
	}

	@Override
	protected GenericPlayCommand getChildClass() {
		return this;
	}

}
