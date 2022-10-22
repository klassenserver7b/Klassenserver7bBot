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

	public static final int STANDARD = 0;
	public static final int BASS_BOOST = 1;
	public static final int LOW_BASS = 2;

	private static final float[] BASS_BOOST_ARR = { 0.2f, 0.15f, 0.1f, 0.05f, 0.0f, -0.05f, -0.1f, -0.1f, -0.1f, -0.1f,
			-0.1f, -0.1f, -0.1f, -0.1f, -0.1f };

	private final EqualizerFactory eqfac;
	private final AudioPlayer player;

	/**
	 * 
	 * @param play
	 */
	private BotEqualizer(AudioPlayer play) {
		this.eqfac = new EqualizerFactory();
		this.player = play;
	}

	/**
	 * 
	 * @param p
	 * @return
	 */
	public static BotEqualizer getEQ(AudioPlayer p) {		
		return new BotEqualizer(p);
	}

	/**
	 * 
	 * @param diff
	 */
	private void bassUp(float diff) {
		for (int i = 0; i < BASS_BOOST_ARR.length; i++) {
			eqfac.setGain(i, BASS_BOOST_ARR[i] + diff);
		}
	}

	/**
	 * 
	 * @param diff
	 */
	private void bassDown(float diff) {
		for (int i = 0; i < BASS_BOOST_ARR.length; i++) {
			eqfac.setGain(i, -BASS_BOOST_ARR[i] + diff);
		}
	}

	/**
	 * 
	 */
	public void eqstart() {
		player.setFilterFactory(eqfac);
	}

	/**
	 * 
	 */
	public void eqstop() {
		player.setFilterFactory(null);
	}

	/**
	 * 
	 * @param mode
	 */
	public void setEQMode(int mode) {

		switch (mode) {
		case 0: {
			eqstop();
			break;
		}
		case 1: {
			bassUp(0);
			eqstart();
			break;
		}
		case 2: {
			bassDown(0);
			eqstart();
			break;
		}
		default: {
			eqstop();
		}
		}

	}
	
	/**
	 * 
	 * @param preset
	 */
	public void setEQMode(EqualizerPreset preset) {
		
		if(preset == EqualizerPreset.OFF) {
			eqstop();
			return;
		}
		
		float[] bands = preset.getBands();
		
		for (int i = 0; i < bands.length; i++) {
			eqfac.setGain(i, bands[i]);
		}
		
		eqstart();
		
		
	}

}
