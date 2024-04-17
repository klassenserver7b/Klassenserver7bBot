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

        if (!event.isFromGuild()) {
            return;
        }

        LiteSQL.onUpdate("INSERT INTO messagelogs(messageId, guildId, timestamp, authorId, messageText) VALUES(?,?,?,?,?)",
                event.getMessageIdLong(),
                event.getGuild().getIdLong(),
                new Date().getTime(),
                event.getAuthor().getIdLong(),
                event.getMessage().getContentRaw());

    }

}
