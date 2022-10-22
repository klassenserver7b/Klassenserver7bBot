package de.k7bot.util.commands.common;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.EnumSet;
import java.util.concurrent.TimeUnit;

import de.k7bot.HelpCategories;
import de.k7bot.Klassenserver7bbot;
import de.k7bot.sql.LiteSQL;
import de.k7bot.util.errorhandler.PermissionError;
import de.k7bot.commands.types.ServerCommand;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.Category;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

public class StatsCategoryCommand implements ServerCommand {

	@Override
	public void performCommand(Member m, TextChannel channel, Message message) {

		if (m.hasPermission(Permission.ADMINISTRATOR)) {

			Guild guild = channel.getGuild();
			ResultSet set = LiteSQL.onQuery("SELECT * FROM statschannels WHERE guildId = ?;",
					channel.getGuild().getIdLong());

			try {
				if (!set.next()) {

					Category cat = guild.createCategory("botstatus").complete();
					cat.getManager().setPosition(0);
					long catid = cat.getIdLong();
					LiteSQL.onUpdate("INSERT INTO statschannels(guildId, categoryId) VALUES(?, ?);", guild.getIdLong(),
							catid);

					fillCategory(cat, Klassenserver7bbot.getInstance().isDevMode());

				} else {

					long catid = set.getLong("categoryId");
					channel.sendMessage("Category updated!").complete().delete().queueAfter(10, TimeUnit.SECONDS);
					Category cat = guild.getCategoryById(catid);
					cat.getChannels().forEach(chan -> {
						chan.delete().complete();
					});
					fillCategory(guild.getCategoryById(catid), Klassenserver7bbot.getInstance().isDevMode());

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
		String help = "Legt eine Kategorie mit dem Bot-Status (Online/Offline) an.\n - kann nur von Mitgliedern mit der Berechtigung 'Administrator' ausgeführt werden!";
		return help;
	}

	@Override
	public HelpCategories getcategory() {
		return HelpCategories.TOOLS;
	}

	public static void fillCategory(Category cat, boolean devmode) {
		if (!devmode) {
			cat.createVoiceChannel("🟢 Bot Online").complete();
		}

		cat.getManager()
				.putPermissionOverride(cat.getGuild().getPublicRole(), null, EnumSet.of(Permission.VOICE_CONNECT))
				.complete();

	}

	public static void onStartup(boolean devmode) {
		Klassenserver7bbot.getInstance().setEventBlocking(true);
		Klassenserver7bbot.getInstance().getShardManager().getGuilds().forEach(guild -> {
			ResultSet set = LiteSQL.onQuery("SELECT categoryId FROM statschannels WHERE guildId = ?;",
					guild.getIdLong());

			try {
				if (set.next()) {
					long catid = set.getLong("categoryId");
					Category cat = guild.getCategoryById(catid);

					if (!devmode) {
						cat.getChannels().forEach(chan -> {
							chan.delete().complete();

						});
					}
					fillCategory(cat, devmode);

				}
			} catch (SQLException e) {
				e.printStackTrace();
			}

		});
		Klassenserver7bbot.getInstance().setEventBlocking(false);
	}

	public static void onShutdown(boolean devmode) {
		Klassenserver7bbot.getInstance().getShardManager().getGuilds().forEach(guild -> {
			ResultSet set = LiteSQL.onQuery("SELECT categoryId FROM statschannels WHERE guildId = ?;",
					guild.getIdLong());
			try {
				if (set.next()) {
					long catid = set.getLong("categoryId");
					Category cat = guild.getCategoryById(catid);

					if (!devmode) {
						cat.getChannels().forEach(chan -> {
							chan.delete().complete();
						});
						cat.createVoiceChannel("🔴 Bot offline").complete();
					}
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		});
	}

}