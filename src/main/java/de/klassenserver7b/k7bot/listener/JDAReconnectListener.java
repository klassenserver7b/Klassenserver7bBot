/**
 *
 */
package de.klassenserver7b.k7bot.listener;

import javax.annotation.Nonnull;

import de.klassenserver7b.k7bot.util.RestartUtil;
import net.dv8tion.jda.api.events.session.SessionRecreateEvent;
import net.dv8tion.jda.api.events.session.SessionResumeEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

/**
 * @author K7
 *
 */
public class JDAReconnectListener extends ListenerAdapter {

	@Override
	public void onSessionRecreate(@Nonnull SessionRecreateEvent event) {

		RestartUtil.restart();

	}
	
	@Override
	public void onSessionResume(@Nonnull SessionResumeEvent event) {
		RestartUtil.restart();

	}

}
