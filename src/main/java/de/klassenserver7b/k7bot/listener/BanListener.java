
package de.klassenserver7b.k7bot.listener;

import java.time.OffsetDateTime;

import de.klassenserver7b.k7bot.Klassenserver7bbot;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.guild.GuildBanEvent;
import net.dv8tion.jda.api.events.guild.GuildUnbanEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class BanListener extends ListenerAdapter {
	@Override
	public void onGuildBan(GuildBanEvent event) {
		Guild guild = event.getGuild();
		TextChannel system = Klassenserver7bbot.getInstance().getsyschannell().getSysChannel(guild);
		User user = event.getUser();
		EmbedBuilder builder = new EmbedBuilder();
		builder.setTimestamp(OffsetDateTime.now());
		builder.setThumbnail(user.getEffectiveAvatarUrl());
		builder.setFooter(guild.getName());
		builder.setColor(13565967);
		builder.setTitle("User banned: " + user.getName());
		builder.setDescription("**User: **\n @" + user.getName());
		system.sendMessageEmbeds(builder.build()).queue();
	}

	@Override
	public void onGuildUnban(GuildUnbanEvent event) {
		Guild guild = event.getGuild();
		TextChannel system = Klassenserver7bbot.getInstance().getsyschannell().getSysChannel(guild);
		User user = event.getUser();
		EmbedBuilder builder = new EmbedBuilder();
		builder.setTimestamp(OffsetDateTime.now());
		builder.setThumbnail(user.getEffectiveAvatarUrl());
		builder.setFooter(guild.getName());
		builder.setColor(58944);
		builder.setTitle("User unbanned: " + user.getName());
		builder.setDescription("**User: **\n @" + user.getName());
		system.sendMessageEmbeds(builder.build()).queue();
	}
}