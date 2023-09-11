/**
 *
 */
package de.klassenserver7b.k7bot.manage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.klassenserver7b.k7bot.Klassenserver7bbot;
import de.klassenserver7b.k7bot.sql.LiteSQL;
import net.dv8tion.jda.api.entities.Guild;

/**
 * @author Klassenserver7b
 *
 */
public class PrefixManager {

	private HashMap<Long, String> prefixl;
	private final Logger log;

	public PrefixManager() {
		this.prefixl = new HashMap<>();
		log = LoggerFactory.getLogger(this.getClass());
		reload();
	}

	protected void reload() {

		try (ResultSet set = LiteSQL.onQuery("SELECT * FROM botutil;")) {

			while (set.next()) {
				Long guildid = set.getLong("guildId");
				String prefix = set.getString("prefix");

				if (guildid == 0) {
					continue;
				}

				assert prefix != null; // SET as NOT NULL AND DEFAULT '-' in DB

				prefixl.put(guildid, prefix);
			}

		} catch (SQLException e) {
			log.error(e.getMessage(), e);
		}

		Klassenserver7bbot.getInstance().getShardManager().getShards().forEach(jda -> {

			for (Guild g : jda.getGuilds()) {

				if (!prefixl.containsKey(g.getIdLong())) {
					try {
						setInternalPrefix(g.getIdLong(), "-");
					} catch (IllegalArgumentException e) {
						log.warn(e.getMessage(), e);
					}
				}

			}

		});

	}

	/**
	 * 
	 * @param guildid
	 * @param newprefix
	 * @throws IllegalArgumentException
	 */
	protected void setInternalPrefix(long guildid, String newprefix) throws IllegalArgumentException {

		if (newprefix == null || newprefix.isBlank()) {
			throw new IllegalArgumentException("can't use a empty prefix - guildid: " + guildid,
					new Throwable().fillInStackTrace());
		}

		applyPrefix(guildid, newprefix);

	}

	/**
	 * 
	 * @param guildid
	 * @param newprefix
	 * @throws IllegalArgumentException
	 */
	public void setPrefix(long guildid, String newprefix) throws IllegalArgumentException {

		reload();

		if (newprefix == null || newprefix.isBlank()) {
			throw new IllegalArgumentException("can't use a empty prefix", new Throwable().fillInStackTrace());
		}

		applyPrefix(guildid, newprefix);

	}

	protected void applyPrefix(long guildid, String prefix) {

		LiteSQL.onUpdate("INSERT OR REPLACE INTO botutil(guildId, prefix) VALUES(?, ?)", guildid, prefix);

		prefixl.put(guildid, prefix);
	}

	public String getPrefix(Guild guild) {
		return this.prefixl.get(guild.getIdLong());
	}

	public String getPrefix(Long guildid) {
		return this.prefixl.get(guildid);
	}

}
