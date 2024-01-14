/**
 *
 */
package de.klassenserver7b.k7bot.logging.listeners;

import de.klassenserver7b.k7bot.logging.LoggingConfigDBHandler;
import de.klassenserver7b.k7bot.logging.LoggingOptions;
import de.klassenserver7b.k7bot.util.EmbedUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.channel.middleman.GuildMessageChannel;
import net.dv8tion.jda.api.events.message.MessageBulkDeleteEvent;
import net.dv8tion.jda.api.events.message.MessageDeleteEvent;
import net.dv8tion.jda.api.events.message.MessageUpdateEvent;

import java.awt.*;

/**
 *
 */
public class MessageLoggingListener extends LoggingListener {

    /**
     *
     */
    public MessageLoggingListener() {
        super();
    }

    @Override
    public void onMessageUpdate(MessageUpdateEvent event) {

        if (!LoggingConfigDBHandler.isOptionEnabled(LoggingOptions.MESSAGE_EDITED, event.getGuild())) {
            return;
        }

        GuildMessageChannel system = getSystemChannel(event.getGuild());

        EmbedBuilder embbuild = EmbedUtils.getDefault(event.getGuild());

        embbuild.setColor(Color.yellow);
        embbuild.setTitle("Message edited in " + event.getChannel().getAsMention());
        embbuild.setDescription("**User: **" + event.getAuthor().getAsMention() + "\n**Message: **" + event.getJumpUrl());

        system.sendMessageEmbeds(embbuild.build()).queue();

    }

    @Override
    public void onMessageDelete(MessageDeleteEvent event) {

        if (!LoggingConfigDBHandler.isOptionEnabled(LoggingOptions.MESSAGE_DELETED, event.getGuild())) {
            return;
        }

        GuildMessageChannel system = getSystemChannel(event.getGuild());

        EmbedBuilder embbuild = EmbedUtils.getDefault(event.getGuild());

        embbuild.setColor(Color.red);
        embbuild.setTitle("Message deleted in " + event.getChannel().getAsMention());
        embbuild.setDescription("**Message: **" + event.getJumpUrl() + "\n**MessageId: **" + event.getMessageIdLong());

        system.sendMessageEmbeds(embbuild.build()).queue();

    }

    @Override
    public void onMessageBulkDelete(MessageBulkDeleteEvent event) {

        if (!LoggingConfigDBHandler.isOptionEnabled(LoggingOptions.MESSAGE_BULK_DELETED, event.getGuild())) {
            return;
        }

        GuildMessageChannel system = getSystemChannel(event.getGuild());

        EmbedBuilder embbuild = EmbedUtils.getDefault(event.getGuild());

        embbuild.setColor(Color.red);
        embbuild.setTitle("Messages deleted in " + event.getChannel().getAsMention());
        embbuild.setDescription("**Amount: **" + event.getMessageIds().size());

        system.sendMessageEmbeds(embbuild.build()).queue();

    }
}
