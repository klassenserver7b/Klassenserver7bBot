
package de.klassenserver7b.k7bot.listener;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.klassenserver7b.k7bot.Klassenserver7bbot;
import de.klassenserver7b.k7bot.sql.LiteSQL;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageReaction;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.UserSnowflake;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.dv8tion.jda.api.entities.channel.middleman.GuildMessageChannel;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.entities.emoji.EmojiUnion;
import net.dv8tion.jda.api.events.message.react.GenericMessageReactionEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class ReactRoleListener extends ListenerAdapter implements InitRequiringListener {

	private final Logger log;

	public ReactRoleListener() {
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
			long guildId = event.getGuild().getIdLong();
			long channelId = event.getChannel().getIdLong();
			long messageId = event.getMessageIdLong();

			EmojiUnion emote = event.getEmoji();

			try (ResultSet set = LiteSQL.onQuery(
					"SELECT roleId FROM reactroles WHERE guildId = ? AND channelId = ? AND messageId = ? AND emote = ?;",
					guildId, channelId, messageId, emote.getName())) {

				if (set.next()) {
					long rollenid = set.getLong("roleId");

					Guild guild = event.getGuild();
					Member member = event.getMember();

					if (member == null) {
						return;
					}

					if (add) {
						guild.addRoleToMember(member, guild.getRoleById(rollenid)).queue();

						LiteSQL.onUpdate(
								"INSERT OR REPLACE INTO userreacts(userId, guildId, messageId, emote) VALUES(?,?,?,?);",
								event.getUserIdLong(), guildId, messageId, emote.getName());
					} else {
						guild.removeRoleFromMember(member, guild.getRoleById(rollenid)).queue();

						LiteSQL.onUpdate(
								"REMOVE FROM userreacts WHERE userId = ? AND guildId = ? AND messageId = ? AND emote = ?;",
								event.getUserIdLong(), guildId, messageId, emote.getName());
					}
				}
			} catch (SQLException | IllegalArgumentException e) {
				log.error(e.getMessage(), e);
			}
		}
	}

	/**
	 * Initializes the Listener (checks for reactions happened in off time)
	 * 
	 * @return {@link CompletableFuture} which retuns the "exit code" of the
	 *         inmitialization
	 */
	@Override
	public CompletableFuture<Integer> initialize() {

		CompletableFuture<Integer> completableFuture = new CompletableFuture<>();

		Executors.newCachedThreadPool().submit(new ReactRoleRunnable(completableFuture));

		return completableFuture;

	}

	/**
	 * SubClass representing the {@link Runnable} for the {@link CompletableFuture}
	 * of {@link ReactRoleListener#initialize()}
	 * 
	 * @author K7
	 */
	protected class ReactRoleRunnable implements Runnable {

		private final ResultSet reactroles;
		private final CompletableFuture<Integer> completableFuture;

		public ReactRoleRunnable(CompletableFuture<Integer> future) {
			this.completableFuture = future;
			this.reactroles = LiteSQL.onQuery("SELECT channelId, messageId, emote, roleId FROM reactroles;");

		}

		@Override
		public void run() {

			try {

				// loop through all registered reactionroles
				while (reactroles.next()) {

					/*
					 * retrieving the GuildCHannel which should always be a GuildMessageChannel
					 * (can't create reactions in other than that)
					 */
					GuildChannel gchan = Klassenserver7bbot.getInstance().getShardManager()
							.getGuildChannelById(reactroles.getLong("channelId"));

					assert gchan instanceof GuildMessageChannel;

					GuildMessageChannel messchan = (GuildMessageChannel) gchan;

					// get Objects from db data
					long messageId = reactroles.getLong("messageId");
					Message mess = messchan.getHistoryAfter(messageId, 1).complete().getMessageById(messageId);
					Guild guild = mess.getGuild();
					Role role = guild.getRoleById(reactroles.getLong("roleId"));

					String emoji = reactroles.getString("emote");

					MessageReaction reaction = mess.getReaction(Emoji.fromFormatted(emoji));

					List<Long> userIds = new ArrayList<>();
					for (User u : reaction.retrieveUsers().complete()) {
						userIds.add(u.getIdLong());
					}

					String sqlstr = "SELECT userId, state from userreacts WHERE messageId = ? AND emote = ?;";

					try (ResultSet oldUserReactData = LiteSQL.onQuery(sqlstr, messageId, emoji);) {

						// loop through all data logged while the bot was running and resolving changes
						while (oldUserReactData.next()) {
							long dbUserId = oldUserReactData.getLong("userId");

							/*
							 * Remove roles, db entry, userIds entry from the users that have removed their
							 * reaction
							 */
							if (checkRoleRemove(userIds, messageId, emoji, role, guild, dbUserId)) {
								userIds.remove(dbUserId);
							}
						}
					}

					//Add roles to every user which wasn't logged but has now reacted
					addRoles(userIds, messageId, emoji, role, guild);

				}
			} catch (SQLException e) {
				log.error(e.getMessage(), e);
				completableFuture.complete(1);
				return;
			}

			completableFuture.complete(0);

		}

		/**
		 * 
		 * Remove roles from all users that have removed their reaction and remove their
		 * db entry
		 *
		 * @param userIds   {@link List} with all users that have currently reacted
		 * @param messageId {@link Message} of the ReactRole
		 * @param emoji     {@link String Emoji} the users have/had to react with
		 * @param role      {@link Role} that should get removed
		 * @param guild     {@link Guild} the Guild of the Message
		 * @param dbUserId  {@link Long} The {@link UserSnowflake} of the user which
		 *                  should be checked
		 * 
		 * @return A boolean representing if the user has removed their reaction (true
		 *         means 'has removed')
		 */
		protected boolean checkRoleRemove(List<Long> userIds, long messageId, String emoji, Role role, Guild guild,
				long dbUserId) {

			if (!userIds.contains(dbUserId)) {
				guild.removeRoleFromMember(UserSnowflake.fromId(dbUserId), role).queue();
				LiteSQL.onUpdate("REMOVE FROM userreacts WHERE userId = ? AND messageId = ? AND emote=?", dbUserId,
						messageId, emoji);
				return true;
			}

			return false;

		}

		/**
		 * 
		 * Add roles from all users that have added a reaction and add a db entry for
		 * them
		 * 
		 * @param userIds   {@link List} with all users that have currently reacted
		 * @param messageId {@link Message} of the ReactRole
		 * @param emoji     {@link String Emoji} the users have/had to react with
		 * @param role      {@link Role} that should be granted
		 * @param guild     {@link Guild} the Guild of the Message
		 */
		protected void addRoles(List<Long> userIds, long messageId, String emoji, Role role, Guild guild) {
			for (long userId : userIds) {

				guild.addRoleToMember(UserSnowflake.fromId(userId), role).queue();
				LiteSQL.onUpdate(
						"INSERT OR REPLACE INTO userreacts(userId, guildId, messageId, emote) VALUES(?,?,?,?);", userId,
						guild.getIdLong(), messageId, emoji);

			}
		}

	}

}