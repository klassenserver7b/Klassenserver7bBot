package de.k7bot.music;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventListener;
import de.k7bot.Klassenserver7bbot;
import java.sql.ResultSet;
import java.sql.SQLException;
import net.dv8tion.jda.api.entities.Guild;

public class MusicController {
	private Guild guild;
	private AudioPlayer player;
	private Queue queue;

	public MusicController(Guild guild) {
		this.guild = guild;
		this.player = Klassenserver7bbot.INSTANCE.audioPlayerManager.createPlayer();
		this.queue = new Queue(this);

		this.guild.getAudioManager().setSendingHandler(new AudioPlayerSendHandler(this.player));
		this.player.addListener((AudioEventListener) new TrackScheduler());
		ResultSet set = Klassenserver7bbot.INSTANCE.getDB().onQuery("SELECT volume FROM botutil WHERE guildId = "+guild.getIdLong());
		try {
			if (set.next()) {
				try {
					int volume = set.getInt("volume");
					this.player.setVolume(volume);
				} catch (SQLException e) {
					e.printStackTrace();
				}
			} else {
				Klassenserver7bbot.INSTANCE.getDB().onUpdate("UPDATE botutil SET volume = 10 WHERE guildId = "+guild.getIdLong());
				this.player.setVolume(10);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public Guild getGuild() {
		return this.guild;
	}

	public AudioPlayer getPlayer() {
		return this.player;
	}

	public Queue getQueue() {
		return this.queue;
	}
}