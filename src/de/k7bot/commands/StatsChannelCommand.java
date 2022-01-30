package de.k7bot.commands;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.EnumSet;
import java.util.concurrent.TimeUnit;

import de.k7bot.Klassenserver7bbot;
import de.k7bot.commands.types.ServerCommand;
import de.k7bot.manage.LiteSQL;
import de.k7bot.manage.PermissionError;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.Category;

public class StatsChannelCommand implements ServerCommand {
	static LiteSQL lsql = Klassenserver7bbot.INSTANCE.getDB();

	@Override
	public void performCommand(Member m, TextChannel channel, Message message) {
		message.delete().queue();
		if (m.hasPermission(Permission.ADMINISTRATOR)) {

			Guild guild = channel.getGuild();
			ResultSet set = lsql
					.onQuery("SELECT * FROM statschannels WHERE guildId = " + channel.getGuild().getIdLong());

			try {
				if (!set.next()) {

					Category cat = guild.createCategory("botstatus").complete();
					cat.getManager().setPosition(0);
					long catid = cat.getIdLong();
					lsql.onUpdate("INSERT INTO statschannels(guildId, categoryId) VALUES(" + guild.getIdLong() + ", "
							+ catid + ")");

					fillCategory(cat, Klassenserver7bbot.INSTANCE.indev);

				} else {

					long catid = set.getLong("categoryId");
					channel.sendMessage("Category updated!").complete().delete().queueAfter(10, TimeUnit.SECONDS);
					Category cat = guild.getCategoryById(catid);
					cat.getChannels().forEach(chan -> {
						chan.delete().complete();
					});
					fillCategory(guild.getCategoryById(catid), Klassenserver7bbot.INSTANCE.indev);

				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		} else {
			PermissionError.onPermissionError(m, channel);
		}

	}

	public static void fillCategory(Category cat, boolean devmode) {
		if (!devmode) {
			cat.createVoiceChannel("🟢 Bot Online").queue();
		} else {
			cat.createVoiceChannel("🟠 Canary online").queue();
		}

		cat.getManager()
				.putPermissionOverride(cat.getGuild().getPublicRole(), null, EnumSet.of(Permission.VOICE_CONNECT))
				.queue();

	}

	public static void onStartup(boolean devmode) {
		Klassenserver7bbot.INSTANCE.imShutdown = true;
		Klassenserver7bbot.INSTANCE.shardMan.getGuilds().forEach(guild -> {
			ResultSet set = lsql.onQuery("SELECT categoryId FROM statschannels WHERE guildId = " + guild.getIdLong());

			try {
				if (set.next()) {
					long catid = set.getLong("categoryId");
					Category cat = guild.getCategoryById(catid);

					if (!devmode) {
						cat.getChannels().forEach(chan -> {
							chan.delete().complete();

						});
					} else {
						cat.getChannels().forEach(chan -> {
							if (chan.getName().equalsIgnoreCase("🟠 Canary online")) {
								chan.delete().queue();
							}
						});
					}
					fillCategory(cat, devmode);

				}
			} catch (SQLException e) {
				e.printStackTrace();
			}

		});
		Klassenserver7bbot.INSTANCE.imShutdown = true;
	}

	public static void onShutdown(boolean devmode) {
		Klassenserver7bbot.INSTANCE.shardMan.getGuilds().forEach(guild -> {
			ResultSet set = lsql.onQuery("SELECT categoryId FROM statschannels WHERE guildId = " + guild.getIdLong());
			try {
				if (set.next()) {
					long catid = set.getLong("categoryId");
					Category cat = guild.getCategoryById(catid);

					if (!devmode) {
						cat.getChannels().forEach(chan -> {
							chan.delete().complete();
						});
						cat.createVoiceChannel("🔴 Bot offline").queue();
					} else {
						cat.getChannels().forEach(chan -> {
							if (chan.getName().equalsIgnoreCase("🟠 Canary online")) {
								chan.delete().queue();
							}
						});
					}
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		});
	}

}