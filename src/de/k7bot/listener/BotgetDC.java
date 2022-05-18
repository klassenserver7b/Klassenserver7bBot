package de.k7bot.listener;

import javax.annotation.Nonnull;

import de.k7bot.Klassenserver7bbot;
import de.k7bot.util.LiteSQL;
import net.dv8tion.jda.api.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.api.events.guild.UnavailableGuildLeaveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class BotgetDC extends ListenerAdapter {
	LiteSQL lsql = Klassenserver7bbot.INSTANCE.getDB();

	@Override
	public void onGuildLeave(@Nonnull GuildLeaveEvent event) {
		long guildid = event.getGuild().getIdLong();

		lsql.onUpdate("DELETE * FROM hypixelnewschannels WHERE guildId = " + guildid);
		lsql.onUpdate("DELETE * FROM musicchannel WHERE guildId = " + guildid);
		lsql.onUpdate("DELETE * FROM statschannels WHERE guildId = " + guildid);
		lsql.onUpdate("DELETE * FROM botutil WHERE guildId = " + guildid);
		lsql.onUpdate("DELETE * FROM reactroles WHERE guildId = " + guildid);

	}

	@Override
	public void onUnavailableGuildLeave(@Nonnull UnavailableGuildLeaveEvent event) {
		long guildid = event.getGuildIdLong();

		lsql.onUpdate("DELETE * FROM hypixelnewschannels WHERE guildId = " + guildid);
		lsql.onUpdate("DELETE * FROM musicchannel WHERE guildId = " + guildid);
		lsql.onUpdate("DELETE * FROM statschannels WHERE guildId = " + guildid);
		lsql.onUpdate("DELETE * FROM botutil WHERE guildId = " + guildid);
		lsql.onUpdate("DELETE * FROM reactroles WHERE guildId = " + guildid);
	}
}