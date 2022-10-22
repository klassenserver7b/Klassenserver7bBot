/**
 * 
 */
package de.k7bot;

import javax.annotation.Nonnull;

/**
 * @author Felix
 *
 */
public enum HelpCategories {

	/**
	 * 
	 */
	UNKNOWN(-1),

	/**
	 * 
	 */
	ALLGEMEIN(0),

	/**
	 * 
	 */
	TOOLS(1),

	/**
	 * 
	 */
	MODERATION(2),

	/**
	 * 
	 */
	MUSIK(3),

	/**
	 * 
	 */
	GAMES(4);

	private final int id;

	private HelpCategories(int id) {

		this.id = id;

	}

	/**
	 * Used to retrieve the id of the category Can be used to spimplify the category
	 * and to retrieve the corresponding {@link HelpCategories} by using
	 * {@link #fromId(int)}
	 * 
	 * @return The static id of the category
	 */
	public int getId() {
		return this.id;
	}

	/**
	 * Static accessor for retrieving a category based on its K7Bot id key.
	 *
	 * @param id The id key of the requested category.
	 *
	 * @return The {@link HelpCategories} that is referred to by the provided key.
	 *         If the id key is unknown, {@link #UNKNOWN} is returned.
	 */
	@Nonnull
	public static HelpCategories fromId(int id) {
		for (HelpCategories type : values()) {
			if (type.id == id)
				return type;
		}
		return UNKNOWN;
	}
}
