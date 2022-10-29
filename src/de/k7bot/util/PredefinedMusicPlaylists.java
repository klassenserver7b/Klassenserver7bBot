/**
 * 
 */
package de.k7bot.util;

import javax.annotation.Nonnull;

/**
 * @author Felix
 *
 */
public enum PredefinedMusicPlaylists {

	Party(1, "https://www.youtube.com/playlist?list=PLAzC6gV-_NVO39WbWU5K76kczhRuKOV9F"),

	YTCharts(0, "https://music.youtube.com/playlist?list=PL0sHkSjKd2rpxgOMD-vlUlIDqvQ5ChYJh"),

	Nightcore(2, "https://www.youtube.com/playlist?list=PLAzC6gV-_NVM6uLLprtddVl4My07_CHtL");

	private final int id;
	private final String url;

	private PredefinedMusicPlaylists(int id, String url) {

		this.id = id;
		this.url = url;

	}

	/**
	 * The K7Bot id key used to represent the {@link PredefinedMusicPlaylists}.
	 * 
	 * @return The id as int
	 */
	public int getId() {
		return this.id;
	}

	/**
	 * Used to retrieve the url of the playlist
	 * 
	 * @return The Url as String
	 */
	public String getUrl() {
		return this.url;
	}

	/**
	 * Static accessor for retrieving a PredefinedMusicPlaylist based on its K7Bot
	 * id key.
	 *
	 * @param id The id key of the requested MusicPlaylist.
	 *
	 * @return The {@link PredefinedMusicPlaylists} that is referred to by the
	 *         provided key. If the id key is unknown, {@link #YTCharts} is
	 *         returned.
	 */
	@Nonnull
	public static PredefinedMusicPlaylists fromId(int id) {
		for (PredefinedMusicPlaylists type : values()) {
			if (type.id == id)
				return type;
		}
		return YTCharts;
	}

}
