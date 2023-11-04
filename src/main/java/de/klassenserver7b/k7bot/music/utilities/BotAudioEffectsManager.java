/**
 *
 */
package de.klassenserver7b.k7bot.music.utilities;

import java.util.ArrayList;
import java.util.List;

import com.sedmelluq.discord.lavaplayer.filter.AudioFilter;
import com.sedmelluq.discord.lavaplayer.filter.UniversalPcmAudioFilter;
import com.sedmelluq.discord.lavaplayer.filter.equalizer.Equalizer;
import com.sedmelluq.discord.lavaplayer.format.AudioDataFormat;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import de.klassenserver7b.k7bot.util.TriFunction;
import net.dv8tion.jda.api.entities.Guild;

/**
 * @author Klassenserver7b
 *
 */
public class BotAudioEffectsManager {

	public static final int STANDARD = 0;
	public static final int BASS_BOOST = 1;
	public static final int LOW_BASS = 2;

	private static final float[] BASS_BOOST_ARR = { 0.2f, 0.15f, 0.1f, 0.05f, 0.0f, -0.05f, -0.1f, -0.1f, -0.1f, -0.1f,
			-0.1f, -0.1f, -0.1f, -0.1f, -0.1f };

	private final AudioPlayer player;

	private final List<TriFunction<AudioTrack, AudioDataFormat, UniversalPcmAudioFilter, AudioFilter>> filterfuncs;

	/**
	 * @param player The {@link AudioPlayer} used for the {@link Guild}
	 */
	private BotAudioEffectsManager(AudioPlayer player) {
		this.filterfuncs = new ArrayList<>();
		this.player = player;
	}

	/**
	 * @param p The {@link AudioPlayer} used for the {@link Guild}
	 * @return The newly created {@link BotAudioEffectsManager} object for this
	 *         guild/player
	 */
	public static BotAudioEffectsManager getAudioEffectsManager(AudioPlayer p) {
		return new BotAudioEffectsManager(p);
	}

	/**
	 * Adds the provided {@link TriFunction AudioFilterFunction} to the currently
	 * used filters and applys them
	 * 
	 * @param filter The {@link TriFunction AudioFilterFunction} to add
	 */
	public void addAudioFilterFunction(
			TriFunction<AudioTrack, AudioDataFormat, UniversalPcmAudioFilter, AudioFilter> filter) {
		filterfuncs.add(filter);
		applyFilters();
	}

	/**
	 * Clears all previously used filters and applys the provided {@link TriFunction
	 * AudioFilterFunction}
	 * 
	 * @param filter The {@link TriFunction AudioFilterFunction} to use
	 */
	public void setAudioFilterFunction(
			TriFunction<AudioTrack, AudioDataFormat, UniversalPcmAudioFilter, AudioFilter> filter) {
		filterfuncs.clear();
		filterfuncs.add(filter);
		applyFilters();
	}

	/**
	 * Adds all provided {@link TriFunction AudioFilterFunction} to the currently
	 * used filters and applys them
	 * 
	 * @param audiofilters The {@link TriFunction AudioFilterFunction} to add
	 */
	public void addAudioFilterFunctions(
			List<TriFunction<AudioTrack, AudioDataFormat, UniversalPcmAudioFilter, AudioFilter>> audiofilters) {
		filterfuncs.addAll(audiofilters);
		applyFilters();
	}

	/**
	 * Clears all previously used filters and applys all provided {@link TriFunction
	 * AudioFilterFunction}
	 * 
	 * @param audiofilters The {@link TriFunction AudioFilterFunction} to use
	 */
	public void setAudioFilterFunctions(
			List<TriFunction<AudioTrack, AudioDataFormat, UniversalPcmAudioFilter, AudioFilter>> audiofilters) {
		filterfuncs.clear();
		filterfuncs.addAll(audiofilters);
		applyFilters();
	}

	/**
	 * Clears all previously used filters
	 */
	public void clearFilters() {
		filterfuncs.clear();
		applyFilters();
	}

	/**
	 * 
	 */
	protected void applyFilters() {
		player.setFilterFactory((track, format, output) -> {

			List<AudioFilter> audiofilters = new ArrayList<>();

			for (TriFunction<AudioTrack, AudioDataFormat, UniversalPcmAudioFilter, AudioFilter> func : filterfuncs) {

				audiofilters.add(func.apply(track, format, output));

			}

			return audiofilters;
		});
	}

	/**
	 * Clears all previously used filters ann applys the provided
	 * {@link EqualizerPreset}
	 *
	 * @param preset THe {@link EqualizerPreset} to use
	 */
	public void setEQMode(EqualizerPreset preset) {

		if (preset == EqualizerPreset.OFF) {
			clearFilters();
			return;
		}

		setAudioFilterFunction(((track, format, output) -> {

			float[] bands = preset.getBands();

			Equalizer eq = new Equalizer(format.channelCount, output);

			for (int i = 0; i < bands.length; i++) {
				eq.setGain(i, bands[i]);
			}

			return eq;
		}));

		applyFilters();

	}

	/**
	 *
	 * @param diff
	 */
	private void bassUp(float diff) {

		setAudioFilterFunction(((track, format, output) -> {

			Equalizer eq = new Equalizer(format.channelCount, output);

			for (int i = 0; i < BASS_BOOST_ARR.length; i++) {
				eq.setGain(i, BASS_BOOST_ARR[i] + diff);
			}

			return eq;
		}));
		applyFilters();
	}

	/**
	 *
	 * @param diff
	 */
	private void bassDown(float diff) {

		setAudioFilterFunction(((track, format, output) -> {

			Equalizer eq = new Equalizer(format.channelCount, output);

			for (int i = 0; i < BASS_BOOST_ARR.length; i++) {
				eq.setGain(i, -BASS_BOOST_ARR[i] + diff);
			}

			return eq;
		}));
		applyFilters();
	}

	/**
	 *
	 * @param mode
	 */
	public void setEQMode(int mode) {

		switch (mode) {
		case 0: {
			clearFilters();
			break;
		}
		case 1: {
			bassUp(0);
			break;
		}
		case 2: {
			bassDown(0);
			break;
		}
		default: {
			clearFilters();
		}
		}

	}

}
