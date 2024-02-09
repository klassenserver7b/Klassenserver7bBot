package de.klassenserver7b.k7bot.listener;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioSourceManager;
import de.klassenserver7b.k7bot.Klassenserver7bbot;
import de.klassenserver7b.k7bot.music.asms.ExtendedLocalAudioSourceManager;
import de.klassenserver7b.k7bot.music.lavaplayer.AudioLoadResult;
import de.klassenserver7b.k7bot.music.lavaplayer.MusicController;
import de.klassenserver7b.k7bot.music.lavaplayer.Queue;
import de.klassenserver7b.k7bot.music.utilities.AudioLoadOption;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceUpdateEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.managers.AudioManager;

public class AutoRickroll extends ListenerAdapter {
	private final AudioPlayerManager apm;

	private static final String RickRollUrl = "https://www.youtube.com/watch?v=BBJa32lCaaY";

	public AutoRickroll() {
		this.apm = new DefaultAudioPlayerManager();

		apm.registerSourceManager(new YoutubeAudioSourceManager());
		apm.registerSourceManager(new ExtendedLocalAudioSourceManager());
	}

	@Override
	public void onGuildVoiceUpdate(GuildVoiceUpdateEvent event) {
		
		if (event.getChannelLeft() == null && event.getGuild().getIdLong() == 701341683325075477L
				&& event.getMember().getIdLong() != event.getGuild().getSelfMember().getUser().getIdLong()
				&& Math.random() >= 0.995D) {
			
			AudioChannel vc = event.getChannelJoined();
			MusicController controller = Klassenserver7bbot.getInstance().getPlayerUtil()
					.getController(vc.getGuild().getIdLong());
			AudioManager manager = vc.getGuild().getAudioManager();

			AudioPlayer player = controller.getPlayer();
			Queue queue = controller.getQueue();

			if (player.getPlayingTrack() == null) {
				
				if (!queue.isemptyQueueList()) {
					queue.clearQueue();
				}
				manager.openAudioConnection(vc);
				apm.loadItem(RickRollUrl, new AudioLoadResult(controller, RickRollUrl, AudioLoadOption.REPLACE));
				player.setPaused(false);
				queue.next(null);
				
			} else {

				if (!queue.isemptyQueueList()) {
					queue.clearQueue();
				}

				player.stopTrack();
				apm.loadItem(RickRollUrl, new AudioLoadResult(controller, RickRollUrl, AudioLoadOption.NEXT));
				queue.next(null);
				player.setPaused(false);
			}
		}
	}
}