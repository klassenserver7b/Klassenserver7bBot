/**
 *
 */
package de.k7bot.manage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.k7bot.Klassenserver7bbot;
import de.k7bot.sql.LiteSQL;
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

	private void reload() {

		ResultSet set = LiteSQL.onQuery("SELECT * FROM botutil;");

		try {
			while (set.next()) {
				Long guildid = set.getLong("guildId");
				String prefix = set.getString("prefix");

				if (guildid == 0) {
					continue;
				}

				if (prefix != null) {
					prefixl.put(guildid, prefix);
				} else {
					prefixl.put(guildid, "-");
				}

			}
		} catch (SQLException e) {
			log.error(e.getMessage(), e);
		}

		Klassenserver7bbot.getInstance().getShardManager().getShards().forEach(jda -> {

			for (Guild g : jda.getGuilds()) {

				if (!prefixl.containsKey(g.getIdLong())) {
					setInternalPrefix(g.getIdLong(), "-");
				}

			}

		});

	}

	private void setInternalPrefix(Long guildid, String newprefix) throws IllegalArgumentException {

		if (newprefix == null || newprefix.isBlank()) {
			throw new IllegalArgumentException("can't use a empty prefix", new Throwable().fillInStackTrace());
		}

		if (prefixl.containsKey(guildid)) {
			LiteSQL.onUpdate("UPDATE botutil SET prefix = ?", newprefix);
		} else {
			LiteSQL.onUpdate("INSERT INTO botutil(guildId, prefix) VALUES(?, ?)", guildid, newprefix);
		}

		prefixl.put(guildid, newprefix);

	}

	public void setPrefix(Long guildid, String newprefix) throws IllegalArgumentException {

		reload();

		if (newprefix == null || newprefix.isBlank()) {
			throw new IllegalArgumentException("can't use a empty prefix", new Throwable().fillInStackTrace());
		}

		if (prefixl.containsKey(guildid)) {
			LiteSQL.onUpdate("UPDATE botutil SET prefix = ?", newprefix);
		} else {
			LiteSQL.onUpdate("INSERT INTO botutil(guildId, prefix) VALUES(?, ?)", guildid, newprefix);
		}

		prefixl.put(guildid, newprefix);

	}

	public String getPrefix(Guild guild) {
		return this.prefixl.get(guild.getIdLong());
	}

	public String getPrefix(Long guildid) {
		return this.prefixl.get(guildid);
	}

}
