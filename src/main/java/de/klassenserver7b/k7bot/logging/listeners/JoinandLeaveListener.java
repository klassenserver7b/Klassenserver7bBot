package de.klassenserver7b.k7bot.logging.listeners;

import de.klassenserver7b.k7bot.Klassenserver7bbot;
import de.klassenserver7b.k7bot.util.EmbedUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.middleman.GuildMessageChannel;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class JoinandLeaveListener extends ListenerAdapter {
	@Override
	public void onGuildMemberJoin(GuildMemberJoinEvent event) {
		GuildMessageChannel system = Klassenserver7bbot.getInstance().getSysChannelMgr().getSysChannel(event.getGuild());
		GuildMessageChannel def = event.getGuild().getDefaultChannel().asStandardGuildMessageChannel();
		String guildname = event.getGuild().getName();
		Member memb = event.getGuild().getMember(event.getUser());

		EmbedBuilder embbuild = EmbedUtils.getSuccessEmbed(memb.getAsMention() + " joined",
				event.getGuild().getIdLong());

		embbuild.setThumbnail(memb.getUser().getEffectiveAvatarUrl());
		embbuild.setTitle("@" + memb.getEffectiveName() + " joined :thumbsup:");

		system.sendMessageEmbeds(embbuild.build()).queue();

		if (def != null) {
			def.sendMessage("Willkommen auf dem " + guildname + " Server " + memb.getAsMention()).queue();
		}
	}

	@Override
	public void onGuildMemberRemove(GuildMemberRemoveEvent event) {

		GuildMessageChannel system = Klassenserver7bbot.getInstance().getSysChannelMgr().getSysChannel(event.getGuild());

		GuildMessageChannel def = event.getGuild().getCommunityUpdatesChannel();
		if (def == null) {
			def = event.getGuild().getDefaultChannel().asStandardGuildMessageChannel();
		}

		User usr = event.getUser();

		EmbedBuilder embbuild = EmbedUtils.getErrorEmbed(
				usr.getAsMention() + " known as " + usr.getEffectiveName() + " leaved", event.getGuild().getIdLong());

		embbuild.setThumbnail(usr.getEffectiveAvatarUrl());
		embbuild.setTitle("@" + usr.getName() + " leaved :sob:");

		system.sendMessageEmbeds(embbuild.build()).queue();
		def.sendMessage("Schade, dass du gehst " + usr.getEffectiveName()).queue();
	}
}
