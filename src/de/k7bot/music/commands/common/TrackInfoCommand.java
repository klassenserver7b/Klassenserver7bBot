package de.k7bot.music.commands.common;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;

import de.k7bot.HelpCategories;
import de.k7bot.Klassenserver7bbot;
import de.k7bot.commands.types.ServerCommand;
import de.k7bot.music.MusicController;
import de.k7bot.music.Queue;
import de.k7bot.music.utilities.MusicUtil;
import de.k7bot.music.utilities.SongJson;

import java.time.OffsetDateTime;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

public class TrackInfoCommand implements ServerCommand {

	@Override
	public String gethelp() {
		String help = "Zeigt die Info zum aktuellen Track.";
		return help;
	}

	@Override
	public HelpCategories getcategory() {
		return HelpCategories.MUSIK;
	}

	public void performCommand(Member m, TextChannel channel, Message message) {

		MusicController controller = Klassenserver7bbot.getInstance().getPlayerUtil()
				.getController(channel.getGuild().getIdLong());
		AudioPlayer player = controller.getPlayer();
		EmbedBuilder builder = new EmbedBuilder();
		MusicUtil.updateChannel(channel);

		if (player.getPlayingTrack() == null) {

			builder.setDescription("Der Bot spielt gerade keinen Song");
			MusicUtil.sendEmbed(channel.getGuild().getIdLong(), builder);

		}

		Queue queue = controller.getQueue();
		SongJson data = queue.getCurrentSongData();

		AudioTrack track = player.getPlayingTrack();
		AudioTrackInfo tinfo = track.getInfo();

		String title = tinfo.title;
		String author = tinfo.author;

		if (data.isDiscogsValidated()) {
			title = data.getTitle();
			author = data.getAuthorString();
		}

		builder.setTimestamp(OffsetDateTime.now());
		builder.setFooter("Requested by @" + m.getEffectiveName());
		builder.setTitle("Es läuft: " + title);
		builder.addField("URL: ", tinfo.uri, false);
		builder.addField("Autor: ", author, false);
		long sec = track.getPosition() / 1000L;
		long min = sec / 60L;
		long h = min / 60L;
		min %= 60L;
		sec %= 60L;
		long sekunden = track.getDuration() / 1000L;
		long minuten = sekunden / 60L;
		long stunden = minuten / 60L;
		minuten %= 60L;
		sekunden %= 60L;
		builder.addField("Länge: ",
				tinfo.isStream ? "LiveStream"
						: (String.valueOf((h > 0L) ? (String.valueOf(h) + "h ") : "")
								+ ((min > 0L) ? (String.valueOf(min) + "min ") : "") + sec + "s" + " / "
								+ ((stunden > 0L) ? (String.valueOf(stunden) + "h ") : "")
								+ ((minuten > 0L) ? (String.valueOf(minuten) + "min ") : "") + sekunden + "s"),
				true);
		MusicUtil.sendEmbed(channel.getGuild().getIdLong(), builder);
	}
}