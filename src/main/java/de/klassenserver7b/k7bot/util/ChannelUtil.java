package de.klassenserver7b.k7bot.util;

import de.klassenserver7b.k7bot.Klassenserver7bbot;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.middleman.GuildMessageChannel;
import net.dv8tion.jda.api.events.guild.GenericGuildEvent;

import java.util.Objects;

public abstract class ChannelUtil {
    public static GuildMessageChannel getSystemChannel(GenericGuildEvent event) {
        return Klassenserver7bbot.getInstance().getSysChannelMgr().getSysChannel(event.getGuild());
    }

    public static GuildMessageChannel getSystemChannel(Guild guild) {
        return Klassenserver7bbot.getInstance().getSysChannelMgr().getSysChannel(guild);
    }

    public static GuildMessageChannel getDefaultChannel(GenericGuildEvent event) {
        return Objects.requireNonNull(event.getGuild().getDefaultChannel()).asStandardGuildMessageChannel();
    }
}
