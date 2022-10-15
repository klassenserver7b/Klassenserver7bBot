package de.k7bot.util.commands;

import de.k7bot.sql.LiteSQL;
import de.k7bot.util.errorhandler.PermissionError;
import de.k7bot.util.errorhandler.SyntaxError;
import de.k7bot.commands.types.ServerCommand;

import java.util.List;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.emoji.CustomEmoji;
import net.dv8tion.jda.api.entities.emoji.Emoji;

public class ReactRolesCommand implements ServerCommand {
	public void performCommand(Member m, TextChannel channel, Message message) {

		if (m.hasPermission(Permission.MANAGE_ROLES)) {
			String[] args = message.getContentDisplay().split(" ");

			if (args.length == 5) {

				List<TextChannel> channels = message.getMentions().getChannels(TextChannel.class);
				List<Role> roles = message.getMentions().getRoles();
				List<CustomEmoji> emotes = message.getMentions().getCustomEmojis();

				if (!channels.isEmpty() && !roles.isEmpty()) {
					TextChannel tc = channels.get(0);
					Role role = roles.get(0);
					String MessageIdString = args[2];

					try {
						long MessageId = Long.parseLong(MessageIdString);

						if (!emotes.isEmpty()) {
							CustomEmoji emote = emotes.get(0);

							tc.addReactionById(MessageId, emote).queue();

							LiteSQL.onUpdate(
									"INSERT INTO reactroles(guildid, channelid, messageid, emote, roleid) VALUES(?, ?, ?, ?, ?);",
									channel.getGuild().getIdLong(), tc.getIdLong(), MessageId, emote.getIdLong(),
									role.getIdLong());
						} else {
							String utfemote = args[3];
							tc.addReactionById(MessageId, Emoji.fromUnicode(utfemote)).queue();

							LiteSQL.onUpdate(
									"INSERT INTO reactroles(guildid, channelid, messageid, emote, roleid) VALUES(?, ?, ?, ?, ?);",
									channel.getGuild().getIdLong(), tc.getIdLong(), MessageId, utfemote,
									role.getIdLong());
						}
					} catch (NumberFormatException e) {
						e.printStackTrace();
					}
				}
			} else {

				SyntaxError.oncmdSyntaxError(channel, "reactrole [#channel] [messageId] [:emote:] [@role]", m);
			}
		} else {
			PermissionError.onPermissionError(m, channel);
		}
	}

	@Override
	public String gethelp() {
		String help = "Erstellt eine Reactionrole für die ausgewählte Nachricht mit dem ausgewählten Emote.\n - z.B. [prefix]reactrole #channel [messageId] :emote: @role";
		return help;
	}

	@Override
	public String getcategory() {
		String category = "Tools";
		return category;
	}
}