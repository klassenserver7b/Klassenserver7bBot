package de.k7bot.music;

import de.k7bot.Klassenserver7bbot;
import java.util.concurrent.ConcurrentHashMap;

public class PlayerManager {
	public ConcurrentHashMap<Long, MusicController> controller = new ConcurrentHashMap<>();

	public MusicController getController(long guildid) {
		MusicController mc = null;

		if (this.controller.containsKey(Long.valueOf(guildid))) {
			mc = this.controller.get(Long.valueOf(guildid));
		} else {

			mc = new MusicController(Klassenserver7bbot.INSTANCE.shardMan.getGuildById(guildid));

			this.controller.put(Long.valueOf(guildid), mc);
		}

		return mc;
	}

	public long getGuildbyPlayerHash(int hash) {
		for (MusicController controller : this.controller.values()) {
			if (controller.getPlayer().hashCode() == hash) {
				return controller.getGuild().getIdLong();
			}
		}
		return -1L;
	}
}