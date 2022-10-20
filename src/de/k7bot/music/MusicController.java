package de.k7bot.music;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventListener;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import de.k7bot.Klassenserver7bbot;
import de.k7bot.sql.LiteSQL;

import java.sql.ResultSet;
import java.sql.SQLException;
import net.dv8tion.jda.api.entities.Guild;

public class MusicController {
	private Guild guild;
	private AudioPlayer player;
	private Queue queue;

	private interface TrackOperation {
		void execute(AudioTrack track);
	}

	public MusicController(Guild guild) {
		this.guild = guild;
		this.player = Klassenserver7bbot.getInstance().getAudioPlayerManager().createPlayer();
		this.queue = new Queue(this);

		this.guild.getAudioManager().setSendingHandler(new AudioPlayerSendHandler(this.player));
		this.player.addListener((AudioEventListener) new TrackScheduler());
		ResultSet set = LiteSQL.onQuery("SELECT volume FROM musicutil WHERE guildId = ?", guild.getIdLong());
		try {
			if (set.next()) {
				try {
					int volume = set.getInt("volume");
					this.player.setVolume(volume);
				} catch (SQLException e) {
					e.printStackTrace();
				}
			} else {
				LiteSQL.onUpdate("UPDATE musicutil SET volume = 10 WHERE guildId = ?", guild.getIdLong());
				this.player.setVolume(10);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void forward(int duration) {
		forPlayingTrack(track -> {
			track.setPosition(track.getPosition() + duration);
		});
	}

	public void back(int duration) {
		forPlayingTrack(track -> {
			track.setPosition(Math.max(0, track.getPosition() - duration));
		});
	}

	public void seek(long position) {
		forPlayingTrack(track -> {
			track.setPosition(position);
		});
	}

	private void forPlayingTrack(TrackOperation operation) {
		AudioTrack track = player.getPlayingTrack();

		if (track != null) {
			operation.execute(track);
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