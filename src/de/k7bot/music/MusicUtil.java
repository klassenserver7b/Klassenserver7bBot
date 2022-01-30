package de.k7bot.music;

import de.k7bot.Klassenserver7bbot;
import de.k7bot.manage.LiteSQL;
import java.sql.ResultSet;
import java.sql.SQLException;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;

public class MusicUtil {
	LiteSQL lsql = Klassenserver7bbot.INSTANCE.getDB();

	public void updateChannel(TextChannel channel) {

		ResultSet set = lsql.onQuery("SELECT * FROM musicchannel WHERE guildId = " + channel.getGuild().getIdLong());

		try {
			if (set.next()) {
				lsql.onUpdate("UPDATE musicchannel SET channelId = " + channel.getIdLong() + " WHERE guildId = "
						+ channel.getGuild().getIdLong());
			} else {
				lsql.onUpdate("INSERT INTO musicchannel(guildId, channelId) VALUES(" + channel.getGuild().getIdLong()
						+ "," + channel.getIdLong() + ")");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void sendEmbed(long guildid, EmbedBuilder builder) {
		ResultSet set = lsql.onQuery("SELECT * FROM musicchannel WHERE guildId = " + guildid);

		try {
			if (set.next()) {
				long channelid = set.getLong("channelId");
				Guild guild;
				if ((guild = Klassenserver7bbot.INSTANCE.shardMan.getGuildById(guildid)) != null) {
					TextChannel channel;
					if ((channel = guild.getTextChannelById(channelid)) != null) {
						channel.sendMessageEmbeds(builder.build())
								.queue();
					}
				}

			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}