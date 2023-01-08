/**
 * 
 */
package de.k7bot.music.utilities;

/**
 * @author Felix
 *
 */
public enum AudioLoadOption {

	/**
	 * Append this track to the queuelist
	 */
	APPEND(0),

	/**
	 * Play this track as the next track
	 */
	NEXT(1),

	/**
	 * replace the current track with this track
	 */
	REPLACE(2);

	private final int id;

	private AudioLoadOption(int id) {
		this.id = id;
	}

	/**
	 * 
	 * @return
	 */
	public int getId() {
		return id;
	}

	/**
	 * 
	 * @return
	 */
	public static AudioLoadOption byId(int id) {

		for (AudioLoadOption l : values()) {
			if (l.getId() == id) {
				return l;
			}
		}

		return APPEND;

	}
}
