package de.klassenserver7b.k7bot.listener;

import de.klassenserver7b.k7bot.Klassenserver7bbot;
import de.klassenserver7b.k7bot.util.EmbedUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Invite;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.dv8tion.jda.api.entities.channel.middleman.GuildMessageChannel;
import net.dv8tion.jda.api.events.guild.invite.GuildInviteCreateEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class InviteListener extends ListenerAdapter {

	@Override
	public void onGuildInviteCreate(GuildInviteCreateEvent event) {

		Guild guild = event.getGuild();
		GuildChannel channel = event.getChannel();
		GuildMessageChannel system = Klassenserver7bbot.getInstance().getSysChannelMgr().getSysChannel(guild);
		Invite inv = event.getInvite();

		EmbedBuilder builder = EmbedUtils.getSuccessEmbed("**Invite: **\n " + inv.getUrl() + "\n\n **Channel: **\n"
				+ channel.getName() + "\n\n **Inviter: **\n@" + inv.getInviter().getName() + "\n\n "
				+ (inv.isExpanded() ? "**Is temporary: **\n" + inv.isTemporary() : ""), guild.getIdLong());

		builder.setTitle("Invite created for " + channel.getName());
		system.sendMessageEmbeds(builder.build()).queue();
	}

}