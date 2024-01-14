/**
 *
 */
package de.klassenserver7b.k7bot.util;

import de.klassenserver7b.k7bot.Klassenserver7bbot;
import de.klassenserver7b.k7bot.logging.LoggingBlocker;
import de.klassenserver7b.k7bot.sql.LiteSQL;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.channel.concrete.Category;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.EnumSet;

/**
 * @author Klassenserver7b
 */
public class StatsCategoryUtil {

    private static final Logger log = LoggerFactory.getLogger(StatsCategoryUtil.class);

    public static void fillCategory(Category cat, boolean devmode) {
        if (!devmode) {
            VoiceChannel vc = cat.createVoiceChannel("🟢 Bot Online").complete();
            LoggingBlocker.getInstance().block(vc.getIdLong());
        }

        cat.getManager()
                .putPermissionOverride(cat.getGuild().getPublicRole(), null, EnumSet.of(Permission.VOICE_CONNECT))
                .complete();

    }

    public static void onStartup(boolean devmode) {
        Klassenserver7bbot.getInstance().getShardManager().getGuilds().forEach(guild -> {
            try (ResultSet set = LiteSQL.onQuery("SELECT categoryId FROM statschannels WHERE guildId = ?;",
                    guild.getIdLong())) {

                if (set.next()) {
                    long catid = set.getLong("categoryId");
                    Category cat = guild.getCategoryById(catid);

                    if (!devmode) {
                        cat.getChannels().forEach(chan -> {
                            LoggingBlocker.getInstance().block(chan.getIdLong());
                            chan.delete().complete();

                        });
                    }
                    fillCategory(cat, devmode);

                }
            } catch (SQLException e) {
                log.error(e.getMessage(), e);
            }

        });
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
                            LoggingBlocker.getInstance().block(chan.getIdLong());
                            chan.delete().complete();
                        });
                        VoiceChannel vc = cat.createVoiceChannel("🔴 Bot offline").complete();
                        LoggingBlocker.getInstance().block(vc.getIdLong());
                    }
                }
            } catch (SQLException e) {
                log.error(e.getMessage(), e);
            }
        });
    }

}
