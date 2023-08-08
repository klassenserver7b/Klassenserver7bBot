
package de.klassenserver7b.k7bot.listener;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.klassenserver7b.k7bot.sql.LiteSQL;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.emoji.EmojiUnion;
import net.dv8tion.jda.api.events.message.react.GenericMessageReactionEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class ReactionListener extends ListenerAdapter implements InitRequiringListener {

	private final Logger log;

	public ReactionListener() {
		log = LoggerFactory.getLogger(this.getClass());
	}

	@Override
	public void onMessageReactionAdd(MessageReactionAddEvent event) {
		performAction(event, true);
	}

	@Override
	public void onMessageReactionRemove(MessageReactionRemoveEvent event) {
		performAction(event, false);
	}

	protected void performAction(GenericMessageReactionEvent event, boolean add) {
		if (event.getChannelType() == ChannelType.TEXT) {
			long guildid = event.getGuild().getIdLong();
			long channelid = event.getChannel().getIdLong();
			long messageid = event.getMessageIdLong();

			EmojiUnion emote = event.getEmoji();

			try (ResultSet set = LiteSQL.onQuery(
					"SELECT roleId FROM reactroles WHERE guildId = ? AND channelId = ? AND messageId = ? AND emote = ?;",
					guildid, channelid, messageid, emote.getName())) {

				if (set.next()) {
					long rollenid = set.getLong("roleId");

					Guild guild = event.getGuild();
					Member member = event.getMember();

					if (member == null) {
						return;
					}

					if (add) {
						guild.addRoleToMember(member, guild.getRoleById(rollenid)).queue();
					} else {
						guild.removeRoleFromMember(member, guild.getRoleById(rollenid)).queue();
					}
				}
			} catch (SQLException | IllegalArgumentException e) {
				log.error(e.getMessage(), e);
			}
		}
	}

	@Override
	public void initialize() {

	}
}