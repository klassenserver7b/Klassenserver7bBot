package de.klassenserver7b.k7bot.listener;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.klassenserver7b.k7bot.sql.LiteSQL;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.middleman.GuildMessageChannel;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class MemesReact extends ListenerAdapter {

	private final Logger log;

	public MemesReact() {
		log = LoggerFactory.getLogger(getClass());
	}

	@Override
	public void onMessageReceived(MessageReceivedEvent event) {
		if (event.isFromType(ChannelType.TEXT)) {

			GuildMessageChannel chan = event.getChannel().asGuildMessageChannel();
			long chanid = chan.getIdLong();

			try (ResultSet set = LiteSQL.onQuery("SELECT channelId FROM memechannels WHERE channelId=?", chanid)) {

				long messid = event.getMessage().getIdLong();

				if (set.next()) {
					react(messid, chan);
				}

			} catch (SQLException e) {
				log.error(e.getMessage(), e);
			}

		}
	}

	public void react(long messid, GuildMessageChannel chan) {

		chan.addReactionById(messid, Emoji.fromFormatted("U+2B06")).queue();

		chan.addReactionById(messid, Emoji.fromFormatted("U+2B07")).queue();
	}
}