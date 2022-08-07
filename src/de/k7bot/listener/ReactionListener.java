
package de.k7bot.listener;

import de.k7bot.Klassenserver7bbot;

import java.sql.ResultSet;
import java.sql.SQLException;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.emoji.EmojiUnion;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.RestAction;

public class ReactionListener extends ListenerAdapter {
	public void onMessageReactionAdd(MessageReactionAddEvent event) {
		if (event.getChannelType() == ChannelType.TEXT && !event.getUser().isBot()) {
			long guildid = event.getGuild().getIdLong();
			long channelid = event.getChannel().getIdLong();
			long messageid = event.getMessageIdLong();

			EmojiUnion emote = event.getEmoji();

			ResultSet set = Klassenserver7bbot.INSTANCE.getDB()
					.onQuery("SELECT roleId FROM reactroles WHERE guildId = " + guildid + " AND channelId = "
							+ channelid + " AND messageId = " + messageid + " AND emote = '" + emote.getName() + "'");
			try {
				if (set.next()) {
					long rollenid = set.getLong("roleId");

					Guild guildmanager = event.getGuild();

					guildmanager.addRoleToMember(event.getMember(), guildmanager.getRoleById(rollenid)).queue();
				}

			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	public void onMessageReactionRemove(MessageReactionRemoveEvent event) {
		if (event.getChannelType() == ChannelType.TEXT) {
			long guildid = event.getGuild().getIdLong();
			long channelid = event.getChannel().getIdLong();
			long messageid = event.getMessageIdLong();
			
			EmojiUnion emote = event.getEmoji();

			ResultSet set = Klassenserver7bbot.INSTANCE.getDB()
					.onQuery("SELECT roleId FROM reactroles WHERE guildId = " + guildid + " AND channelId = "
							+ channelid + " AND messageId = " + messageid + " AND emote = '" + emote.getName() + "'");
			try {
				if (set.next()) {
					long rollenid = set.getLong("roleId");

					Guild guildmanager = event.getGuild();
					RestAction<Member> member = event.retrieveMember();
					Member memb = (Member) member.complete();

					guildmanager.removeRoleFromMember(memb, guildmanager.getRoleById(rollenid)).queue();
				}
			} catch (SQLException | IllegalArgumentException e) {
				e.printStackTrace();
			}
		}
	}
}