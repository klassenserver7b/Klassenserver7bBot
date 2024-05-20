/**
 *
 */
package de.klassenserver7b.k7bot.util.customapis;

import de.klassenserver7b.k7bot.sql.LiteSQL;
import de.klassenserver7b.k7bot.util.customapis.types.LoopedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

        Long mindate = System.currentTimeMillis() - 1000 * 60 * 60 * 24 * 2; // 7 days

        int status = LiteSQL.onUpdate("DELETE FROM messagelogs WHERE timestamp < ?", mindate);

        if (status >= 0) {
            log.info("Removed {} lines from messagelogs", status);
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
