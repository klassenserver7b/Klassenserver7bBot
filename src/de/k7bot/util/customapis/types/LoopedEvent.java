/**
 *
 */
package de.k7bot.util.customapis.types;

import de.k7bot.util.InternalStatusCodes;

/**
 * @author Klassenserver7b
 *
 */
public interface LoopedEvent {

	/**
	 * Default method -> called in a loop
	 * 
	 * @return {@link InternalStatusCodes statuscode} as an int
	 * 
	 * 
	 */
	public int checkforUpdates();

	/**
	 * Used to check if event is available again after error
	 * 
	 * @return is the event is available
	 */
	public boolean isAvailable();

	/**
	 * 
	 */
	public void shutdown();

	/**
	 * 
	 * @return if the restart was susccessful
	 */
	public boolean restart();

	/**
	 * 
	 * @return the events identifier as an String
	 */
	public String getIdentifier();

}
