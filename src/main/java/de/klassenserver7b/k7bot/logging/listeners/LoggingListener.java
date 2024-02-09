/**
 *
 */
package de.klassenserver7b.k7bot.logging.listeners;

import de.klassenserver7b.k7bot.Klassenserver7bbot;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.middleman.GuildMessageChannel;
import net.dv8tion.jda.api.events.guild.GenericGuildEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 *
 */
public abstract class LoggingListener extends ListenerAdapter {

    /**
     *
     */
    public LoggingListener() {
        super();
    }

    public GuildMessageChannel getSystemChannel(GenericGuildEvent event) {
        return Klassenserver7bbot.getInstance().getSysChannelMgr().getSysChannel(event.getGuild());
    }

    public GuildMessageChannel getSystemChannel(Guild guild) {
        return Klassenserver7bbot.getInstance().getSysChannelMgr().getSysChannel(guild);
    }

    public GuildMessageChannel getDefaultChannel(GenericGuildEvent event) {
        return Objects.requireNonNull(event.getGuild().getDefaultChannel()).asStandardGuildMessageChannel();
    }

    public static Collection<Object> getDefault() {

        List<Object> listeners = new ArrayList<>();

        listeners.add(new MemberLoggingListener());
        listeners.add(new ModerationLoggingListener());
        listeners.add(new RoleLoggingListener());
        listeners.add(new ChannelLoggingListener());
        listeners.add(new MessageLoggingListener());
        listeners.add(new VoiceLoggingListener());
        listeners.add(new InviteLoggingListener());
        listeners.add(new EmojiLoggingListener());
        listeners.add(new EventLoggingListener());

        return listeners;

    }
}
