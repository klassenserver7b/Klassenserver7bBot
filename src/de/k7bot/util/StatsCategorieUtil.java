/**
 *
 */
package de.k7bot.util;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.EnumSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.k7bot.Klassenserver7bbot;
import de.k7bot.sql.LiteSQL;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.channel.concrete.Category;

/**
 * @author Klassenserver7b
 *
 */
public class StatsCategorieUtil {

	private static final Logger log = LoggerFactory.getLogger(StatsCategorieUtil.class);

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
				log.error(e.getMessage(), e);
			}

		});
		Klassenserver7bbot.getInstance().setEventBlocking(false);
	}

	public static void onShutdown(boolean devmode) {
		Klassenserver7bbot.getInstance().getShardManager().getGuilds().forEach(guild -> {

			try (ResultSet set = LiteSQL.onQuery("SELECT categoryId FROM statschannels WHERE guildId = ?;",
					guild.getIdLong())) {

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
				log.error(e.getMessage(), e);
			}
		});
	}

}
