/**
 *
 */
package de.klassenserver7b.k7bot.util.customapis;

import de.klassenserver7b.k7bot.sql.LiteSQL;
import de.klassenserver7b.k7bot.util.customapis.types.LoopedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.OffsetDateTime;

/**
 *
 */
public class DBAutodelete implements LoopedEvent {

    private final Logger log;

    /**
     *
     */
    public DBAutodelete() {
        log = LoggerFactory.getLogger(getClass());
    }

    @Override
    public int checkforUpdates() {

        Long mindate = OffsetDateTime.now().minusDays(2).toEpochSecond();
        Long guildId = 0L;

        int status = LiteSQL.onUpdate("DELETE * FROM messagelogs WHERE guildId=? AND timestamp < ?", guildId, mindate);

        if (status >= 0) {
            log.debug("Removed " + status + "lines from messagelogs");
        }

        return status;

    }

    @Override
    public boolean isAvailable() {
        return true;
    }

    @Override
    public void shutdown() {
        // NOTHING to do here

    }

    @Override
    public boolean restart() {
        // NOTHING to do here
        return true;
    }

    @Override
    public String getIdentifier() {
        return "db_autodelete";
    }

}
