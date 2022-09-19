/**
 * 
 */
package de.k7bot.music;

import com.sedmelluq.discord.lavaplayer.filter.equalizer.EqualizerFactory;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;

/**
 * @author Felix
 *
 */
public class BotEqualizer {

	public static final int STANDARD= 0;
	public static final int BASS_BOOST = 1;
	public static final int LOW_BASS = 2;

	private final EqualizerFactory eqfac;
	private final AudioPlayer player;

	private BotEqualizer() {
		this.eqfac = new EqualizerFactory();
		player = null;
		return;
	}

	private BotEqualizer(AudioPlayer play) {
		this.eqfac = new EqualizerFactory();
		this.player = play;
	}

	public static BotEqualizer getEQ(AudioPlayer p) {
		return new BotEqualizer(p);
	}

	public void eqstart() {
		player.setFilterFactory(eqfac);
	}

	public void eqstop() {
		player.setFilterFactory(null);
	}

	/**
	 * 
	 * @param mode
	 */
	public void setEQMode(int mode) {

	}

}
