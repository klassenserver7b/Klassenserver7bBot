package de.k7bot.moderation;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentHashMap.KeySetView;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import de.k7bot.Klassenserver7bbot;
import de.k7bot.util.LiteSQL;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;

public class SystemNotificationChannelHolder {

	private final ConcurrentHashMap<Guild, TextChannel> systemchannellist = new ConcurrentHashMap<>();

	/**
	 * Synchronises the Local SystemchannelList with the database and checks if
	 * there are new Guilds and lists their SystemChannels.
	 */
	public void checkSysChannelList() {

		LiteSQL sqlite = Klassenserver7bbot.INSTANCE.getDB();

		try {
			ResultSet set = sqlite.onQuery("SELECT guildId, syschannelId FROM botutil");
			while (set.next()) {

				Guild guild = Klassenserver7bbot.INSTANCE.shardMan.getGuildById(set.getLong("guildId"));

				if (guild != null && !this.systemchannellist.containsKey(guild)) {

					TextChannel channel = guild.getTextChannelById(set.getLong("syschannelId"));

						if (channel != null) {
							this.systemchannellist.put(guild, channel);
						} else {
							this.systemchannellist.put(guild, guild.getSystemChannel());
							sqlite.onUpdate("UPDATE botutil SET syschannelId='" + guild.getSystemChannel().getIdLong()
									+ "' WHERE guildId=" + set.getLong("guildId"));
						}


				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		Klassenserver7bbot.INSTANCE.shardMan.getGuilds().forEach(gu -> {

			if (!this.systemchannellist.containsKey(gu)) {
				this.systemchannellist.put(gu, gu.getSystemChannel());
				sqlite.onUpdate("INSERT INTO botutil(guildId, syschannelId) VALUES(" + gu.getIdLong() + ", "
						+ gu.getSystemChannel().getIdLong() + ")");
			}

		});

		List<Guild> listedguilds = Klassenserver7bbot.INSTANCE.shardMan.getGuilds();
		KeySetView<Guild, TextChannel> alreadylisted = systemchannellist.keySet();

		listedguilds.forEach(guild -> {

			if ((!alreadylisted.contains(guild)) || (systemchannellist.get(guild) == null)) {
				systemchannellist.put(guild, guild.getSystemChannel());
			}

		});

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
		checkSysChannelList();
		Guild guild = channel.getGuild();

		if (!systemchannellist.containsKey(guild)) {
			
			systemchannellist.put(guild, channel);
			Klassenserver7bbot.INSTANCE.getDB().onUpdate("INSERT INTO botutil(guildId, syschannelId) VALUES("
					+ guild.getIdLong() + ", " + channel.getIdLong() + ")");
			
		} else {
			
			systemchannellist.replace(guild, channel);
			Klassenserver7bbot.INSTANCE.getDB().onUpdate("UPDATE botutil SET syschannelId = " + channel.getIdLong()
					+ " WHERE guildId = " + guild.getIdLong());
		}

	}

	/**
	 * @param guild <br>
	 *              The {@link Guild} for which you want the SystemChannel.
	 * @return The {@link net.dv8tion.jda.api.entities.TextChannel SystemChannel}
	 *         for the Guild or {@code null} if no channel is listed.
	 */
	@Nullable
	public TextChannel getSysChannel(@Nonnull Guild guild) {

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

		Guild guild = Klassenserver7bbot.INSTANCE.shardMan.getGuildById(guildId);

		return systemchannellist.get(guild);
	}

	/**
	 * Only used if you want the full ConcurrentHashMap! If you want only one
	 * {@link net.dv8tion.jda.api.entities.TextChannel SystemChannel} please use
	 * {@link SystemNotificationChannelHolder#getSysChannel()}
	 * 
	 * @return The current HashMap of SystemChannels wich is used by the Bot.
	 */
	public ConcurrentHashMap<Guild, TextChannel> getHashMap() {
		return systemchannellist;
	}
}
