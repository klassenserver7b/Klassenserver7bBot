package de.k7bot.hypixel.commands;

import de.k7bot.sql.LiteSQL;
import de.k7bot.util.errorhandler.SyntaxError;
import de.k7bot.commands.types.HypixelCommand;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

public class HypixelNewsChannelCommand implements HypixelCommand {
	
	private final Logger log;
	
	public HypixelNewsChannelCommand() {
		log = LoggerFactory.getLogger(this.getClass());
	}
	
	public void performHypixelCommand(Member m, TextChannel channel, Message message) {

		if (!message.getMentions().getChannels(TextChannel.class).isEmpty()) {

			TextChannel chan = message.getMentions().getChannels(TextChannel.class).get(0);
			long chanId = chan.getIdLong();
			Long guildId = chan.getGuild().getIdLong();
			ResultSet set = LiteSQL.onQuery("select guildId from hypixelnewschannels;");
			List<Long> guilds = new ArrayList<>();

			try {
				while (set.next()) {
					guilds.add(set.getLong("guildId"));
				}

				if (guilds.contains(guildId)) {
					LiteSQL.onUpdate("UPDATE hypixelnewschannels SET channelId = ? WHERE guildId = ?;", chanId,
							guildId);
					channel.sendMessage("Newschannel was sucsessfully updated to " + chan.getAsMention()).queue();
				} else {
					LiteSQL.onUpdate("INSERT INTO hypixelnewschannels(guildId, channelId) VALUES(?, ?);", guildId,
							chanId);
					channel.sendMessage("Newschannel was sucsessful set to " + chan.getAsMention()).queue();
				}

			} catch (SQLException e) {
				log.error(e.getMessage(),e);
			}
		} else {

			SyntaxError.oncmdSyntaxError(channel, "hypixel newschannel #channel", m);
		}
	}
}