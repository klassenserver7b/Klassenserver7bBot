package de.k7bot.hypixel.commands;

import de.k7bot.Klassenserver7bbot;
import de.k7bot.commands.types.HypixelCommand;
import de.k7bot.util.LiteSQL;
import de.k7bot.util.SyntaxError;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;

public class HypixelNewsChannelCommand implements HypixelCommand {
	public void performHypixelCommand(Member m, TextChannel channel, Message message) {
		LiteSQL lsql = Klassenserver7bbot.INSTANCE.getDB();

		if (!message.getMentions().getChannels(TextChannel.class).isEmpty()) {

			TextChannel chan = message.getMentions().getChannels(TextChannel.class).get(0);
			long chanId = chan.getIdLong();
			Long guildId = chan.getGuild().getIdLong();
			ResultSet set = lsql.onQuery("select guildId from hypixelnewschannels");
			List<Long> guilds = new ArrayList<>();

			try {
				while (set.next()) {
					guilds.add(set.getLong("guildId"));
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