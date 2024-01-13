/**
 *
 */
package de.klassenserver7b.k7bot.music.utilities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.sedmelluq.discord.lavaplayer.filter.AudioFilter;
import com.sedmelluq.discord.lavaplayer.filter.UniversalPcmAudioFilter;
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

	private final AudioPlayer player;

	private final HashMap<FilterTypes, TriFunction<AudioTrack, AudioDataFormat, UniversalPcmAudioFilter, AudioFilter>> filterfuncs;
	private static final HashMap<AudioPlayer, BotAudioEffectsManager> effmans = new HashMap<>();

	/**
	 * @param player The {@link AudioPlayer} used for the {@link Guild}
	 */
	private BotAudioEffectsManager(AudioPlayer player) {
		this.filterfuncs = new HashMap<>();
		this.player = player;
	}

	/**
	 * @param p The {@link AudioPlayer} used for the {@link Guild}
	 * @return The newly created {@link BotAudioEffectsManager} object for this
	 *         guild/player
	 */
	public static BotAudioEffectsManager getAudioEffectsManager(AudioPlayer p) {

		BotAudioEffectsManager effman = effmans.get(p);
		if (effman == null) {
			effman = new BotAudioEffectsManager(p);
			effmans.put(p, effman);
		}

		return effman;
	}

	/**
	 * Adds the provided {@link TriFunction AudioFilterFunction} to the currently
	 * used filters and applys them
	 * 
	 * @param type   the {@link FilterTypes} of the {@link TriFunction
	 *               AudioFilterFunction}
	 * @param filter The {@link TriFunction AudioFilterFunction} to add
	 */
	public void addAudioFilterFunction(FilterTypes type,
			TriFunction<AudioTrack, AudioDataFormat, UniversalPcmAudioFilter, AudioFilter> filter) {
		filterfuncs.put(type, filter);

		applyFilters();
	}

	/**
	 * Clears all previously used filters and applys the provided {@link TriFunction
	 * AudioFilterFunction}
	 * 
	 * @param type   the {@link FilterTypes} of the {@link TriFunction
	 *               AudioFilterFunction}
	 * @param filter The {@link TriFunction AudioFilterFunction} to use
	 */
	public void setAudioFilterFunction(FilterTypes type,
			TriFunction<AudioTrack, AudioDataFormat, UniversalPcmAudioFilter, AudioFilter> filter) {
		filterfuncs.clear();
		filterfuncs.put(type, filter);
		applyFilters();
	}

	/**
	 * Removes every {@link TriFunction AudioFilterFunction} matching the given
	 * {@link FilterTypes}
	 * 
	 * @param type the {@link FilterTypes} to remove
	 * @return statuscode <br>
	 *         304 means nothing changed -> {@link FilterTypes} wasn't present in
	 *         the current filters<br>
	 *         200 means sucessfully removed the matching filter
	 * 
	 */
	public int removeAudioFilterFunction(FilterTypes type) {
		TriFunction<AudioTrack, AudioDataFormat, UniversalPcmAudioFilter, AudioFilter> oldfilter = filterfuncs
				.remove(type);
		applyFilters();

		if (oldfilter == null) {
			return 304;
		}

		return 200;
	}

	/**
	 * Adds all provided {@link TriFunction AudioFilterFunction} to the currently
	 * used filters and applies them
	 * 
	 * @param audiofilters A Hashmap respresenting the {@link FilterTypes
	 *                     FilterTypes} and coresponding {@link TriFunction
	 *                     AudioFilterFunctions}
	 * 
	 */
	public void addAudioFilterFunctions(
			HashMap<FilterTypes, TriFunction<AudioTrack, AudioDataFormat, UniversalPcmAudioFilter, AudioFilter>> audiofilters) {

		filterfuncs.putAll(audiofilters);
		applyFilters();
	}

	/**
	 * Clears all previously used filters and applys all provided {@link TriFunction
	 * AudioFilterFunction}
	 * 
	 * @param audiofilters A Hashmap respresenting the {@link FilterTypes
	 *                     FilterTypes} and coresponding {@link TriFunction
	 *                     AudioFilterFunctions}
	 */
	public void setAudioFilterFunctions(
			HashMap<FilterTypes, TriFunction<AudioTrack, AudioDataFormat, UniversalPcmAudioFilter, AudioFilter>> audiofilters) {
		filterfuncs.clear();
		filterfuncs.putAll(audiofilters);
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

			for (TriFunction<AudioTrack, AudioDataFormat, UniversalPcmAudioFilter, AudioFilter> func : filterfuncs
					.values()) {

				audiofilters.add(func.apply(track, format, output));

			}

			return audiofilters;
		});
	}

	public enum FilterTypes {

		EQ(0), SPEED(1);

		private final int id;

		private FilterTypes(int id) {
			this.id = id;
		}

		public static FilterTypes fromId(int id) {
			for (FilterTypes type : values()) {
				if (type.id == id)
					return type;
			}
			throw new IllegalArgumentException("Invalid FilterType Id");
		}

	}

}
