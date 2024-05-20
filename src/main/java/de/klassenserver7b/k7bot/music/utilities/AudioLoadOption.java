/**
 *
 */
package de.klassenserver7b.k7bot.music.utilities;

/**
 * @author K7
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

    AudioLoadOption(int id) {
        this.id = id;
    }

    /**
     * @return the id of the AudioLoadOption
     */
    public int getId() {
        return id;
    }

    /**
     * @return the AudioLoadOption by the id
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
