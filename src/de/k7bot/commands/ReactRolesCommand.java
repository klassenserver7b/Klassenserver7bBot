package de.k7bot.commands;

import de.k7bot.Klassenserver7bbot;
import de.k7bot.commands.types.ServerCommand;
import de.k7bot.util.LiteSQL;
import de.k7bot.util.PermissionError;
import de.k7bot.util.SyntaxError;

import java.util.List;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Emote;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;

public class ReactRolesCommand implements ServerCommand {
	public void performCommand(Member m, TextChannel channel, Message message) {

		LiteSQL lsql = Klassenserver7bbot.INSTANCE.getDB();

		if (m.hasPermission(Permission.MANAGE_ROLES)) {
			String[] args = message.getContentDisplay().split(" ");

			if (args.length == 5) {

				List<TextChannel> channels = message.getMentionedChannels();
				List<Role> roles = message.getMentionedRoles();
				List<Emote> emotes = message.getEmotes();

				if (!channels.isEmpty() && !roles.isEmpty()) {
					TextChannel tc = channels.get(0);
					Role role = roles.get(0);
					String MessageIdString = args[2];

					try {
						long MessageId = Long.parseLong(MessageIdString);

						if (!emotes.isEmpty()) {
							Emote emote = emotes.get(0);

							tc.addReactionById(MessageId, emote).queue();

							lsql.onUpdate("INSERT INTO reactroles(guildid, channelid, messageid, emote, roleid) VALUES("
									+ channel.getGuild().getIdLong() + ", " + tc.getIdLong() + ", " + MessageId + ", '"
									+ emote.getId() + "', " + role.getIdLong() + ")");
						} else {
							String utfemote = args[3];
							tc.addReactionById(MessageId, utfemote).queue();

							lsql.onUpdate("INSERT INTO reactroles(guildid, channelid, messageid, emote, roleid) VALUES("
									+ channel.getGuild().getIdLong() + ", " + tc.getIdLong() + ", " + MessageId + ", '"
									+ utfemote + "', " + role.getIdLong() + ")");
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