package de.k7bot.music;

import java.util.concurrent.ConcurrentHashMap;

import de.k7bot.Klassenserver7bbot;

public class AudioPlayerUtil {

	private ConcurrentHashMap<Long, MusicController> controller = new ConcurrentHashMap<>();

	public static final int STANDARDVOLUME = 10;

	public MusicController getController(long guildid) {
		MusicController mc = null;

		if (this.controller.containsKey(Long.valueOf(guildid))) {
			mc = this.controller.get(Long.valueOf(guildid));
		} else {

			mc = new MusicController(Klassenserver7bbot.getInstance().getShardManager().getGuildById(guildid));

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

	public void stopAllTracks() {
		controller.values().forEach(contr -> {
			contr.getPlayer().stopTrack();
		});
	}
}