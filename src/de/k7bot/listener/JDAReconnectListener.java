/**
 *
 */
package de.k7bot.listener;

import javax.annotation.Nonnull;

import de.k7bot.util.RestartUtil;
import net.dv8tion.jda.api.events.session.SessionRecreateEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

/**
 * @author Felix
 *
 */
public class JDAReconnectListener extends ListenerAdapter {

	@Override
	public void onSessionRecreate(@Nonnull SessionRecreateEvent event) {

		RestartUtil.restart();

	}

}