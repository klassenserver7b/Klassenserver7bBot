package de.k7bot.util.commands;

import java.awt.Color;
import java.time.OffsetDateTime;
import java.util.concurrent.TimeUnit;

import de.k7bot.commands.types.ServerCommand;
import de.k7bot.util.PermissionError;
import de.k7bot.util.SyntaxError;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;

public class RoleCreation implements ServerCommand {

	@Override
	public void performCommand(Member m, TextChannel channel, Message message) {

		if (m.hasPermission(Permission.MANAGE_ROLES)) {
			// -createrole <name> #ffffff

			Guild guild = channel.getGuild();
			String[] args = message.getContentDisplay().split(" ");
			int length = args.length;

			if (length > 1) {

				StringBuilder builder = new StringBuilder();

				if (args[length - 1].startsWith("#") && length > 2) {

					for (int i = 1; i < length - 1; i++) {
						builder.append(args[i] + " ");
					}

					String hexCode = args[length - 1];

					String roleName = builder.toString().trim();

					channel.sendTyping().queue();

					guild.createRole().queue(role -> {

						Color color = Color.decode(hexCode);
						role.getManager().setName(roleName).setColor(color).setPermissions(Permission.MESSAGE_HISTORY,
								Permission.MESSAGE_ADD_REACTION, Permission.VOICE_CONNECT).queue();
						EmbedBuilder embed = new EmbedBuilder();
						embed.setTimestamp(OffsetDateTime.now());
						embed.setTitle("Role created");
						embed.setFooter("requested by @" + m.getEffectiveName());
						embed.setDescription("role " + roleName + "created");
						embed.setColor(color);

						channel.sendMessageEmbeds(embed.build()).complete().delete().queueAfter(10, TimeUnit.SECONDS);
					});

				} else {

					for (int i = 1; i < length; i++) {
						builder.append(args[i] + " ");
					}

					String roleName = builder.toString().trim();

					channel.sendTyping().queue();

					guild.createRole().queue(role -> {
						role.getManager().setName(roleName).setPermissions(Permission.MESSAGE_HISTORY,
								Permission.MESSAGE_ADD_REACTION, Permission.VOICE_CONNECT).queue();
						EmbedBuilder embed = new EmbedBuilder();
						embed.setTimestamp(OffsetDateTime.now());
						embed.setTitle("Role created");
						embed.setFooter("requested by @" + m.getEffectiveName());
						embed.setDescription("role " + roleName + "created");

						channel.sendMessageEmbeds(embed.build()).complete().delete().queueAfter(10, TimeUnit.SECONDS);
					});

				}

			} else {
				SyntaxError.oncmdSyntaxError(channel, "createrole [name] (#color)", m);
			}

		} else {
			PermissionError.onPermissionError(m, channel);
		}
	}
	
	@Override
	public String gethelp() {
		String help = "Erstellt eine Rolle mit dem gewählten Namen und ggf. der gewählten Farbe.\n - kann nur von Mitgliedern mit der Berechtigung 'Manage-Roles' ausgeführt werden!\n - z.B. [prefix]createrole [test] <#ffffff>";
		return help;
	}

	@Override
	public String getcategory() {
String category = "Tools";
		return category;
	}

}