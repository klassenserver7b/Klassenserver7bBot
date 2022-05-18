package de.k7bot.util.commands;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.EnumSet;
import java.util.concurrent.TimeUnit;

import de.k7bot.Klassenserver7bbot;
import de.k7bot.commands.types.ServerCommand;
import de.k7bot.util.LiteSQL;
import de.k7bot.util.PermissionError;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.Category;

public class StatsCategoryCommand implements ServerCommand {

	@Override
	public void performCommand(Member m, TextChannel channel, Message message) {
		LiteSQL lsql = Klassenserver7bbot.INSTANCE.getDB();
		
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
	
	@Override
	public String gethelp() {
		String help = "Legt eine Kategorie mit dem Bot-Status (Online/Offline) an.\n - kann nur von Mitgliedern mit der Berechtigung 'Administrator' ausgeührt werden!";
		return help;
	}

	@Override
	public String getcategory() {
		String category = "Tools";
		return category;
	}

	public static void fillCategory(Category cat, boolean devmode) {
		if (!devmode) {
			cat.createVoiceChannel("🟢 Bot Online").complete();
		} else {
			cat.createVoiceChannel("🟠 Canary online").complete();
		}

		cat.getManager()
				.putPermissionOverride(cat.getGuild().getPublicRole(), null, EnumSet.of(Permission.VOICE_CONNECT))
				.complete();

	}

	public static void onStartup(boolean devmode) {
		LiteSQL lsql = Klassenserver7bbot.INSTANCE.getDB();
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
								chan.delete().complete();
							}
						});
					}
					fillCategory(cat, devmode);

				}
			} catch (SQLException e) {
				e.printStackTrace();
			}

		});
		Klassenserver7bbot.INSTANCE.imShutdown = false;
	}

	public static void onShutdown(boolean devmode) {
		LiteSQL lsql = Klassenserver7bbot.INSTANCE.getDB();
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
						cat.createVoiceChannel("🔴 Bot offline").complete();
					} else {
						cat.getChannels().forEach(chan -> {
							if (chan.getName().equalsIgnoreCase("🟠 Canary online")) {
								chan.delete().complete();
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