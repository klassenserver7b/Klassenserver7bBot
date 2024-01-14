
package de.klassenserver7b.k7bot.logging.listeners;

import java.awt.Color;

import de.klassenserver7b.k7bot.Klassenserver7bbot;
import de.klassenserver7b.k7bot.util.EmbedUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.middleman.GuildMessageChannel;
import net.dv8tion.jda.api.events.guild.GuildBanEvent;
import net.dv8tion.jda.api.events.guild.GuildUnbanEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class BanListener extends ListenerAdapter {
	@Override
	public void onGuildBan(GuildBanEvent event) {
		Guild guild = event.getGuild();
		GuildMessageChannel system = Klassenserver7bbot.getInstance().getSysChannelMgr().getSysChannel(guild);
		User user = event.getUser();
		EmbedBuilder builder = EmbedUtils.getBuilderOf(Color.red, "**User: **\n @" + user.getName(), guild.getIdLong());
		builder.setThumbnail(user.getEffectiveAvatarUrl());
		builder.setTitle("User banned: " + user.getName());
		system.sendMessageEmbeds(builder.build()).queue();
	}

	@Override
	public void onGuildUnban(GuildUnbanEvent event) {
		Guild guild = event.getGuild();
		GuildMessageChannel system = Klassenserver7bbot.getInstance().getSysChannelMgr().getSysChannel(guild);
		User user = event.getUser();
		EmbedBuilder builder = EmbedUtils.getBuilderOf(Color.green, "**User: **\n @" + user.getName(),
				guild.getIdLong());
		builder.setThumbnail(user.getEffectiveAvatarUrl());
		builder.setTitle("User unbanned: " + user.getName());
		system.sendMessageEmbeds(builder.build()).queue();
	}
}