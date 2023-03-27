/**
 *
 */
package de.k7bot.util.customapis.types;

/**
 * @author Klassenserver7b
 *
 */
public class ApiEntry {
	private InternalAPI api;
	private boolean enabled;

	public ApiEntry(InternalAPI api, boolean enabled) {
		this.api = api;
		this.enabled = enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public InternalAPI getAPI() {
		return this.api;
	}

	public boolean isEnabled() {
		return this.enabled;
	}
}
