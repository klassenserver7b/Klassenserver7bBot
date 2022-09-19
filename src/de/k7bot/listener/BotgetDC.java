package de.k7bot.listener;

import javax.annotation.Nonnull;

import de.k7bot.SQL.LiteSQL;
import net.dv8tion.jda.api.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.api.events.guild.UnavailableGuildLeaveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class BotgetDC extends ListenerAdapter {

	@Override
	public void onGuildLeave(@Nonnull GuildLeaveEvent event) {
		long guildid = event.getGuild().getIdLong();

		LiteSQL.onUpdate("DELETE * FROM hypixelnewschannels WHERE guildId = " + guildid);
		LiteSQL.onUpdate("DELETE * FROM musicchannel WHERE guildId = " + guildid);
		LiteSQL.onUpdate("DELETE * FROM statschannels WHERE guildId = " + guildid);
		LiteSQL.onUpdate("DELETE * FROM botutil WHERE guildId = " + guildid);
		LiteSQL.onUpdate("DELETE * FROM reactroles WHERE guildId = " + guildid);

	}

	@Override
	public void onUnavailableGuildLeave(@Nonnull UnavailableGuildLeaveEvent event) {
		long guildid = event.getGuildIdLong();

		LiteSQL.onUpdate("DELETE * FROM hypixelnewschannels WHERE guildId = " + guildid);
		LiteSQL.onUpdate("DELETE * FROM musicchannel WHERE guildId = " + guildid);
		LiteSQL.onUpdate("DELETE * FROM statschannels WHERE guildId = " + guildid);
		LiteSQL.onUpdate("DELETE * FROM botutil WHERE guildId = " + guildid);
		LiteSQL.onUpdate("DELETE * FROM reactroles WHERE guildId = " + guildid);
	}
}