package de.k7bot.music.commands.common;

import java.awt.Color;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;

import de.k7bot.HelpCategories;
import de.k7bot.Klassenserver7bbot;
import de.k7bot.commands.types.ServerCommand;
import de.k7bot.music.AudioLoadResult;
import de.k7bot.music.MusicController;
import de.k7bot.music.utilities.AudioLoadOption;
import de.k7bot.music.utilities.MusicUtil;
import de.k7bot.music.utilities.spotify.SpotifyAudioSourceManager;
import de.k7bot.util.GenericMessageSendHandler;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;
import net.dv8tion.jda.api.managers.AudioManager;

@Deprecated
public class OldAddQueueTrackCommand implements ServerCommand {

	@Deprecated
	private final Logger log = LoggerFactory.getLogger(this.getClass());

	@Override
	@Deprecated
	public String gethelp() {
		return "Lädt den/die ausgewählte/-n Track / Livestream / Playlist und fügt ihn/sie der aktuellen Queue hinzu.\n - z.B. [prefix]addtoqueue [url / YouTube Suchbegriff]";
	}

	@Override
	@Deprecated
	public HelpCategories getcategory() {
		return HelpCategories.MUSIK;
	}

	@Override
	@Deprecated
	public void performCommand(Member m, TextChannel channel, Message message) {

		String[] args = message.getContentDisplay().split(" ");

		if (!MusicUtil.checkConditions(new GenericMessageSendHandler(channel), m)) {
			return;
		}

		AudioChannel vc = MusicUtil.getMembVcConnection(m);

		if (args.length > 1) {

			MusicController controller = Klassenserver7bbot.getInstance().getPlayerUtil()
					.getController(vc.getGuild().getIdLong());
			AudioManager manager = vc.getGuild().getAudioManager();
			AudioPlayerManager apm = new DefaultAudioPlayerManager();

			apm.registerSourceManager(new SpotifyAudioSourceManager());

			AudioSourceManagers.registerRemoteSources(apm);
			AudioSourceManagers.registerLocalSource(apm);

			if (!manager.isConnected() || controller.getPlayer().getPlayingTrack() == null) {

				channel.sendMessageEmbeds(new EmbedBuilder().setFooter("requested by @" + m.getEffectiveName())
						.setTitle("Invalid Command Usage").setColor(Color.decode("#ff0000"))
						.setDescription(
								"The Bot isn't connected to a voicechannel / isn't playing a Song!\nPLEASE USE `"
										+ Klassenserver7bbot.getInstance().getPrefixMgr()
												.getPrefix(vc.getGuild().getIdLong())
										+ "play` INSTEAD!")
						.build()).complete().delete().queueAfter(20, TimeUnit.SECONDS);

				return;

			}

			StringBuilder strBuilder = new StringBuilder();

			for (int i = 1; i < args.length; i++) {
				strBuilder.append(args[i]);
				strBuilder.append(" ");
			}

			String url = strBuilder.toString().trim();

			if (url.equalsIgnoreCase("party")) {

				url = "https://www.youtube.com/playlist?list=PLAzC6gV-_NVO39WbWU5K76kczhRuKOV9F";
				OldPlayCommand.party = true;

			} else if (url.startsWith("lf: ")) {

				url = url.substring(4);

			} else if (!(url.startsWith("http") || url.startsWith("scsearch: ") || url.startsWith("ytsearch: "))) {
				url = "ytsearch: " + url;
			}

			Klassenserver7bbot.getInstance().getMainLogger()
					.info("Bot startet searching a track: no current track -> new Track(channelName = " + vc.getName()
							+ ", url = " + url + ")");

			try {
				apm.loadItem(url, new AudioLoadResult(controller, url, AudioLoadOption.APPEND));
			} catch (FriendlyException e) {
				log.error(e.getMessage(), e);
			}

		} else {

			channel.sendMessage("You are not in a voicechannel" + m.getAsMention()).complete().delete().queueAfter(10L,
					TimeUnit.SECONDS);
		}

	}
}
