/**
 * 
 */
package de.klassenserver7b.k7bot.listener;

import de.klassenserver7b.k7bot.sql.LiteSQL;
import net.dv8tion.jda.api.events.message.MessageDeleteEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.annotation.Nonnull;
import java.util.Date;

/**
 * 
 */
public class MessageListener extends ListenerAdapter {

	@Override
	public void onMessageDelete(@Nonnull MessageDeleteEvent event) {

	}

	@Override
	public void onMessageReceived(@Nonnull MessageReceivedEvent event) {

		// FIXME enable logging
		boolean loggingenabled = false;

		if (loggingenabled) {

			LiteSQL.onUpdate("INSERT INTO messagelogs(messageId, guildId, date, messageText) VALUES(?,?,?)",
					event.getMessageIdLong(),
					(event.getGuild() == null ? 0 : event.getGuild().getIdLong()),
					new Date().getTime(),
					event.getMessage().getContentRaw());

		}

	}

}
