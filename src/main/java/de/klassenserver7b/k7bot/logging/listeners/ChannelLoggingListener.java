
package de.klassenserver7b.k7bot.logging.listeners;

import de.klassenserver7b.k7bot.logging.LoggingBlocker;
import de.klassenserver7b.k7bot.logging.LoggingConfigDBHandler;
import de.klassenserver7b.k7bot.logging.LoggingOptions;
import de.klassenserver7b.k7bot.util.EmbedUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.Channel;
import net.dv8tion.jda.api.entities.channel.middleman.GuildMessageChannel;
import net.dv8tion.jda.api.events.channel.ChannelCreateEvent;
import net.dv8tion.jda.api.events.channel.ChannelDeleteEvent;
import net.dv8tion.jda.api.events.channel.update.ChannelUpdateNameEvent;
import net.dv8tion.jda.api.events.channel.update.ChannelUpdatePositionEvent;
import net.dv8tion.jda.api.events.channel.update.ChannelUpdateTypeEvent;

import javax.annotation.Nonnull;
import java.awt.*;

public class ChannelLoggingListener extends LoggingListener {

    public ChannelLoggingListener() {
        super();
    }

    @Override
    public void onChannelCreate(@Nonnull ChannelCreateEvent event) {

        if (isIgnoredEvent(LoggingOptions.CHANNEL_CREATE, event.getGuild(), event.getChannel().getIdLong())) {
            return;
        }

        Channel channel = event.getChannel();

        GuildMessageChannel system = getSystemChannel(event.getGuild());

        EmbedBuilder embbuild = EmbedUtils.getDefault(event.getGuild().getIdLong());

        embbuild.setColor(Color.green);
        embbuild.setTitle("Channel created: " + channel.getName());
        embbuild.setDescription("**Channel: **" + channel.getAsMention() + "\n**Type: **" + channel.getType()
                + "\n**ChannelId: **" + channel.getIdLong());

        system.sendMessageEmbeds(embbuild.build()).queue();

    }

    @Override
    public void onChannelDelete(@Nonnull ChannelDeleteEvent event) {

        if (isIgnoredEvent(LoggingOptions.CHANNEL_DELETE, event.getGuild(), event.getChannel().getIdLong())) {
            return;
        }

        Channel channel = event.getChannel();
        GuildMessageChannel system = getSystemChannel(event.getGuild());


        EmbedBuilder embbuild = EmbedUtils.getDefault(event.getGuild().getIdLong());

        embbuild.setColor(Color.red);
        embbuild.setTitle("Channel deleted: " + channel.getName());
        embbuild.setDescription("**Channel: **" + channel.getAsMention() + "\n**Type: **" + channel.getType()
                + "\n**ChannelId: **" + channel.getIdLong());

        system.sendMessageEmbeds(embbuild.build()).queue();

    }

    @Override
    public void onChannelUpdateName(@Nonnull ChannelUpdateNameEvent event) {

        if (isIgnoredEvent(LoggingOptions.CHANNEL_UPDATE_NAME, event.getGuild(), event.getChannel().getIdLong())) {
            return;
        }

        Channel channel = event.getChannel();
        GuildMessageChannel system = getSystemChannel(event.getGuild());

        EmbedBuilder embbuild = EmbedUtils.getDefault(event.getGuild().getIdLong());

        embbuild.setColor(Color.yellow);
        embbuild.setTitle("Channel name updated: " + channel.getAsMention());
        embbuild.setDescription("**Old name: **" + event.getOldValue() + "\n**New name: **" + event.getNewValue());

        system.sendMessageEmbeds(embbuild.build()).queue();

    }

    @Override
    public void onChannelUpdatePosition(@Nonnull ChannelUpdatePositionEvent event) {

        if (isIgnoredEvent(LoggingOptions.CHANNEL_UPDATE_POSITION, event.getGuild(), event.getChannel().getIdLong())) {
            return;
        }

        Channel channel = event.getChannel();
        GuildMessageChannel system = getSystemChannel(event.getGuild());

        EmbedBuilder embbuild = EmbedUtils.getDefault(event.getGuild().getIdLong());

        embbuild.setColor(Color.yellow);
        embbuild.setTitle("Channel position updated: " + channel.getAsMention());
        embbuild.setDescription("**Old position: **" + event.getOldValue() + "\n**New position: **" + event.getNewValue());

        system.sendMessageEmbeds(embbuild.build()).queue();

    }

    @Override
    public void onChannelUpdateType(@Nonnull ChannelUpdateTypeEvent event) {

        if (isIgnoredEvent(LoggingOptions.CHANNEL_UPDATE_TYPE, event.getGuild(), event.getChannel().getIdLong())) {
            return;
        }

        Channel channel = event.getChannel();
        GuildMessageChannel system = getSystemChannel(event.getGuild());

        EmbedBuilder embbuild = EmbedUtils.getDefault(event.getGuild().getIdLong());

        embbuild.setColor(Color.yellow);
        embbuild.setTitle("Channel type updated: " + channel.getAsMention());
        embbuild.setDescription("**Old type: **" + event.getOldValue() + "\n**New type: **" + event.getNewValue());

        system.sendMessageEmbeds(embbuild.build()).queue();

    }

    protected boolean isIgnoredEvent(LoggingOptions option, Guild guild, long channelId) {

        if (LoggingBlocker.getInstance().isBlocked(channelId)) {
            LoggingBlocker.getInstance().unblock(channelId);
            return true;
        }

        return !LoggingConfigDBHandler.isOptionEnabled(option, guild);

    }
}
