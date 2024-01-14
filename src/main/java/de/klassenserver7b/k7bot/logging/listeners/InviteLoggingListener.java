package de.klassenserver7b.k7bot.logging.listeners;

import de.klassenserver7b.k7bot.logging.LoggingConfigDBHandler;
import de.klassenserver7b.k7bot.logging.LoggingOptions;
import de.klassenserver7b.k7bot.util.EmbedUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Invite;
import net.dv8tion.jda.api.entities.channel.middleman.GuildMessageChannel;
import net.dv8tion.jda.api.events.guild.invite.GuildInviteCreateEvent;
import net.dv8tion.jda.api.events.guild.invite.GuildInviteDeleteEvent;

import java.awt.*;
import java.time.OffsetDateTime;

public class InviteLoggingListener extends LoggingListener {

    public InviteLoggingListener() {
        super();
    }

    @Override
    public void onGuildInviteCreate(GuildInviteCreateEvent event) {

        if (!LoggingConfigDBHandler.isOptionEnabled(LoggingOptions.INVITE_CREATE, event.getGuild())) {
            return;
        }

        GuildMessageChannel system = getSystemChannel(event);
        Invite inv = event.getInvite();

        EmbedBuilder embbuild = EmbedUtils.getDefault(event.getGuild());

        embbuild.setTitle("Invite created for " + event.getChannel().getName());
        embbuild.setColor(Color.green);

        embbuild.setDescription(
                "**Invite: **" + inv.getUrl()
                        + "\n*Channel: **" + event.getChannel().getAsMention()
                        + (inv.getInviter() != null ? "\n**Inviter: **" + inv.getInviter().getAsMention() : "")
                        + "\n**Is Expanded: **" + inv.isExpanded()
                        + "\n**Expands: **<t:" + OffsetDateTime.now().plusSeconds(inv.getMaxAge()).toEpochSecond() + ">"
                        + "\n**Users are temporary: **" + inv.isTemporary()
        );

        system.sendMessageEmbeds(embbuild.build()).queue();
    }

    @Override
    public void onGuildInviteDelete(GuildInviteDeleteEvent event) {

        if (!LoggingConfigDBHandler.isOptionEnabled(LoggingOptions.INVITE_DELETE, event.getGuild())) {
            return;
        }

        GuildMessageChannel system = getSystemChannel(event);

        EmbedBuilder embbuild = EmbedUtils.getDefault(event.getGuild());

        embbuild.setTitle("Invite deleted for " + event.getChannel().getName());
        embbuild.setColor(Color.red);
        embbuild.setDescription("**Invite: **" + event.getUrl()
                + "\n*Channel: **" + event.getChannel().getAsMention());

        system.sendMessageEmbeds(embbuild.build()).queue();
    }

}