/**
 *
 */
package de.klassenserver7b.k7bot.logging;

import de.klassenserver7b.k7bot.sql.LiteSQL;
import net.dv8tion.jda.api.entities.Guild;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 */
public abstract class LoggingConfigDBHandler {

    private static final Logger log = LoggerFactory.getLogger(LoggingConfigDBHandler.class);

    public static int insertGuild(long guildId) {
        return LiteSQL.onUpdate("INSERT OR IGNORE INTO loggingConfig(guildId) VALUES(?);", guildId);
    }

    public static int enableOption(LoggingOptions option, long guildId) {

        insertGuild(guildId);

        if (isOptionDisabled(option, guildId)) {
            return LiteSQL.onUpdate(
                    "UPDATE loggingConfig SET optionJson = json_insert(optionJson,'$[#]',?) WHERE guildId=?;",
                    option.getId(), guildId);

        }

        return 0;
    }

    public static int disableOption(LoggingOptions option, long guildId) {

        insertGuild(guildId);

        return LiteSQL.onUpdate(
                "UPDATE loggingConfig SET optionJson = (SELECT json_group_array(value) FROM json_each(optionJson) WHERE value != ?) WHERE guildId = ?;",
                option.getId(), guildId);
    }

    public static boolean isOptionDisabled(LoggingOptions option, Guild guild) {
        return isOptionDisabled(option, guild.getIdLong());
    }

    public static boolean isOptionDisabled(LoggingOptions option, long guildId) {

        insertGuild(guildId);

        try (ResultSet set = LiteSQL.onQuery(
                "SELECT IIF((SELECT (SELECT 1 FROM json_each(optionJson) WHERE value = ?) FROM loggingConfig WHERE guildId = ?), True, False) result;",
                option.getId(), guildId)) {

            return !set.getBoolean("result");

        } catch (SQLException e) {
            log.error(e.getMessage(), e);
        }

        return false;

    }

    /**
     * Toggles the key and returns the new state of the {@link LoggingOptions};
     *
     * @param option
     * @param guildId
     * @return
     */
    public static boolean toggleOption(LoggingOptions option, long guildId) {

        if (LoggingOptions.UNKNOWN == option) {
            return false;
        }

        if (!isOptionDisabled(option, guildId)) {
            disableOption(option, guildId);
            return false;
        } else {
            enableOption(option, guildId);
            return true;

        }

    }
}
