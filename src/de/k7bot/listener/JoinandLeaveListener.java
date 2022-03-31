package de.k7bot.listener;

import java.time.OffsetDateTime;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class JoinandLeaveListener extends ListenerAdapter {
	public void onGuildMemberJoin(GuildMemberJoinEvent event) {
		TextChannel system = event.getGuild().getSystemChannel();
		TextChannel def = event.getGuild().getCommunityUpdatesChannel();
		String guildname = event.getGuild().getName();
		Member memb = event.getGuild().getMember(event.getUser());
		EmbedBuilder embbuild = new EmbedBuilder();
		embbuild.setTimestamp(OffsetDateTime.now());
		embbuild.setThumbnail(memb.getUser().getEffectiveAvatarUrl());
		embbuild.setTitle("@" + memb.getEffectiveName() + " joined :thumbsup:");
		embbuild.setFooter("Member joined");
		embbuild.setColor(58944);
		embbuild.setDescription(memb.getAsMention() + " joined");

		system.sendMessageEmbeds(embbuild.build(), new net.dv8tion.jda.api.entities.MessageEmbed[0]).queue();
		def.sendMessage("Willkommen auf dem " + guildname + " Server " + memb.getAsMention()).queue();
	}

	public void onGuildMemberRemove(GuildMemberRemoveEvent event) {
		TextChannel system = event.getGuild().getSystemChannel();
		TextChannel def = event.getGuild().getCommunityUpdatesChannel();
		User usr = event.getUser();
		EmbedBuilder embbuild = new EmbedBuilder();
		embbuild.setTimestamp(OffsetDateTime.now());
		embbuild.setThumbnail(usr.getEffectiveAvatarUrl());
		embbuild.setTitle("@" + usr.getName() + " leaved :sob:");
		embbuild.setFooter("Member leaved");
		embbuild.setColor(13565967);
		embbuild.setDescription(usr.getAsMention() + " leaved");

		system.sendMessageEmbeds(embbuild.build()).queue();
		def.sendMessage("Schade das du gehst " + usr.getAsMention()).queue();
	}
}
