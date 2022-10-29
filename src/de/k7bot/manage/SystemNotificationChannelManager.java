package de.k7bot.manage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.k7bot.Klassenserver7bbot;
import de.k7bot.sql.LiteSQL;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

public class SystemNotificationChannelManager {

	private final ConcurrentHashMap<Guild, TextChannel> systemchannellist;
	private final Logger log;

	public SystemNotificationChannelManager() {

		systemchannellist = new ConcurrentHashMap<>();
		log = LoggerFactory.getLogger(this.getClass());
		reload();
	}

	private void reload() {

		ResultSet set = LiteSQL.onQuery("SELECT * FROM botutil;");

		try {
			while (set.next()) {
				Long guildid = set.getLong("guildId");
				Long systemchannel = set.getLong("syschannelId");

				if (guildid == 0) {
					continue;
				}

				Guild g = Klassenserver7bbot.getInstance().getShardManager().getGuildById(guildid);

				if (g == null) {
					continue;
				}

				TextChannel chan = g.getTextChannelById(systemchannel);

				if (chan == null) {
					systemchannellist.put(g, g.getSystemChannel());
					LiteSQL.onUpdate("UPDATE botutil SET syschannelId = ?;", g.getSystemChannel().getIdLong());
				} else {
					systemchannellist.put(g, chan);
				}

			}
		} catch (SQLException e) {
			log.error(e.getMessage(), e);
		}

	}

	/**
	 * Puts the given {@link net.dv8tion.jda.api.entities.TextChannel SystemChannel}
	 * into the Hashmap keyed by his {@link Guild}.
	 * 
	 * @param channel <br>
	 *                The {@link net.dv8tion.jda.api.entities.TextChannel
	 *                SystemChannel} wich u want to use in this {@link Guild}.
	 */
	public void insertChannel(TextChannel channel) {

		reload();

		Guild guild = channel.getGuild();

		if (systemchannellist.containsKey(guild)) {

			LiteSQL.onUpdate("UPDATE botutil SET syschannelId = ? WHERE guildId = ?;", channel.getIdLong(),
					guild.getIdLong());

		} else {

			LiteSQL.onUpdate("INSERT INTO botutil(guildId, syschannelId) VALUES(?, ?);", guild.getIdLong(),
					channel.getIdLong());

		}

		systemchannellist.put(guild, channel);

	}

	/**
	 * @param guild <br>
	 *              The {@link Guild} for which you want the SystemChannel.
	 * @return The {@link net.dv8tion.jda.api.entities.TextChannel SystemChannel}
	 *         for the Guild or {@code null} if no channel is listed.
	 */
	@Nullable
	public TextChannel getSysChannel(@Nonnull Guild guild) {

		if (systemchannellist.get(guild) == null) {
			return guild.getSystemChannel();
		}

		return systemchannellist.get(guild);
	}

	/**
	 * @param guildId <br>
	 *                The Id of the {@link Guild} for which you want the
	 *                SystemChannel.
	 * @return The {@link net.dv8tion.jda.api.entities.TextChannel SystemChannel}
	 *         for the Guild or {@code null} if no channel is listed.
	 */
	@Nullable
	public TextChannel getSysChannel(@Nonnull Long guildId) throws NullPointerException {

		Guild guild = Klassenserver7bbot.getInstance().getShardManager().getGuildById(guildId);

		return systemchannellist.get(guild);
	}

	/**
	 * Only used if you want the full ConcurrentHashMap! If you want only one
	 * {@link net.dv8tion.jda.api.entities.TextChannel SystemChannel} please use
	 * {@link SystemNotificationChannelManager#getSysChannel()}
	 * 
	 * @return The current HashMap of SystemChannels wich is used by the Bot.
	 */
	public ConcurrentHashMap<Guild, TextChannel> getHashMap() {
		reload();
		return systemchannellist;
	}
}
