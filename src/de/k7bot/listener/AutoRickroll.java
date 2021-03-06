package de.k7bot.listener;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import de.k7bot.Klassenserver7bbot;
import de.k7bot.music.AudioLoadResult;
import de.k7bot.music.MusicController;
import de.k7bot.music.Queue;
import net.dv8tion.jda.api.entities.AudioChannel;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceJoinEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.managers.AudioManager;

public class AutoRickroll extends ListenerAdapter {
	
	@Override
	public void onGuildVoiceJoin(GuildVoiceJoinEvent event) {
		if (event.getGuild().getIdLong() == 701341683325075477L && event.getMember().getIdLong() != 846296603139506187L
				&& Math.random() >= 0.95D) {
			AudioChannel vc = event.getChannelJoined();
			MusicController controller = Klassenserver7bbot.INSTANCE.playerManager
					.getController(vc.getGuild().getIdLong());
			AudioManager manager = vc.getGuild().getAudioManager();
			AudioPlayerManager apm = Klassenserver7bbot.INSTANCE.audioPlayerManager;
			AudioPlayer player = controller.getPlayer();
			Queue queue = controller.getQueue();

			String url = "https://www.youtube.com/watch?v=BBJa32lCaaY";

			if (player.getPlayingTrack() == null) {
				if (!queue.emptyQueueList()) {
					queue.clearQueue();
				}
				manager.openAudioConnection(vc);
				apm.loadItem(url, new AudioLoadResult(controller, url, false));
				player.setPaused(false);
			} else {

				if (!queue.emptyQueueList()) {
					queue.clearQueue();
				}
				player.stopTrack();
				apm.loadItem(url, new AudioLoadResult(controller, url, false));
				player.setPaused(false);
			}
		}
	}
}