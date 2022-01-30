package de.k7bot.hypixel.commands;

import de.k7bot.Klassenserver7bbot;
import de.k7bot.commands.types.HypixelCommand;
import de.k7bot.manage.LiteSQL;
import de.k7bot.manage.SyntaxError;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;

public class HypixelNewsChannelCommand implements HypixelCommand {
	public void performHypixelCommand(Member m, TextChannel channel, Message message) {
		message.delete().queue();
		LiteSQL lsql = Klassenserver7bbot.INSTANCE.getDB();

		if (!message.getMentionedChannels().isEmpty()) {

			TextChannel chan = message.getMentionedChannels().get(0);
			Long chanId = Long.valueOf(chan.getIdLong());
			Long guildId = Long.valueOf(chan.getGuild().getIdLong());
			ResultSet set = lsql.onQuery("select guildId from hypixelnewschannels");
			List<Long> guilds = new ArrayList<>();

			try {
				while (set.next()) {
					guilds.add(Long.valueOf(set.getLong("guildId")));
				}

				if (guilds.contains(guildId)) {
					lsql.onUpdate(
							"UPDATE hypixelnewschannels SET channelId = " + chanId + " WHERE guildId = " + guildId);
					channel.sendMessage("Newschannel was sucsessfully updated to " + chan.getAsMention()).queue();
				} else {
					lsql.onUpdate("INSERT INTO hypixelnewschannels(guildId, channelId) VALUES(" + guildId + ", "
							+ chanId + ")");
					channel.sendMessage("Newschannel was sucsessful set to " + chan.getAsMention()).queue();
				}

			} catch (SQLException e) {
				e.printStackTrace();
			}
		} else {

			SyntaxError.oncmdSyntaxError(channel, "hypixel newschannel #channel", m);
		}
	}
}