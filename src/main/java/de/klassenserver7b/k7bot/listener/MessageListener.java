/**
 *
 */
package de.klassenserver7b.k7bot.listener;

import de.klassenserver7b.k7bot.sql.LiteSQL;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.annotation.Nonnull;

/**
 *
 */
public class MessageListener extends ListenerAdapter {

    @Override
    public void onMessageReceived(@Nonnull MessageReceivedEvent event) {

        if (!event.isFromGuild()) {
            return;
        }

        LiteSQL.onUpdate("INSERT INTO messagelogs(messageId, guildId, timestamp, authorId, messageText) VALUES(?,?,?,?,?)",
                event.getMessageIdLong(),
                event.getGuild().getIdLong(),
                System.currentTimeMillis(),
                event.getAuthor().getIdLong(),
                event.getMessage().getContentRaw());

    }

}
