package de.k7bot.music.commands;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import de.k7bot.Klassenserver7bbot;
import de.k7bot.commands.types.ServerCommand;
import de.k7bot.music.MusicController;
import de.k7bot.music.MusicUtil;
import java.time.OffsetDateTime;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

public class TrackInfoCommand implements ServerCommand {
	public void performCommand(Member m, TextChannel channel, Message message) {
		
		MusicController controller = Klassenserver7bbot.INSTANCE.playerManager
				.getController(channel.getGuild().getIdLong());
		AudioPlayer player = controller.getPlayer();
		EmbedBuilder builder = new EmbedBuilder();
		MusicUtil.updateChannel(channel);
		AudioTrack track;
		if ((track = player.getPlayingTrack()) != null) {
			AudioTrackInfo info = track.getInfo();
			builder.setTimestamp(OffsetDateTime.now());
			builder.setFooter("Requested by @" + m.getEffectiveName());
			builder.setTitle("Es läuft: " + info.title);
			builder.addField("URL: ", info.uri, false);
			builder.addField("Autor: ", info.author, false);
			long sec = track.getPosition() / 1000L;
			long min = sec / 60L;
			long h = min / 60L;
			min %= 60L;
			sec %= 60L;
			long sekunden = info.length / 1000L;
			long minuten = sekunden / 60L;
			long stunden = minuten / 60L;
			minuten %= 60L;
			sekunden %= 60L;
			builder.addField("Länge: ",
					info.isStream ? "LiveStream"
							: (String.valueOf((h > 0L) ? (String.valueOf(h) + "h ") : "")
									+ ((min > 0L) ? (String.valueOf(min) + "min ") : "") + sec + "s" + " / "
									+ ((stunden > 0L) ? (String.valueOf(stunden) + "h ") : "")
									+ ((minuten > 0L) ? (String.valueOf(minuten) + "min ") : "") + sekunden + "s"),
					true);
			MusicUtil.sendEmbed(channel.getGuild().getIdLong(), builder);
		} else {
			builder.setDescription("Der Bot spielt gerade keinen Song");
			MusicUtil.sendEmbed(channel.getGuild().getIdLong(), builder);
		}
	}
	
	@Override
	public String gethelp() {
		String help = "Zeigt die Info zum aktuellen Track.";
		return help;
	}

	@Override
	public String getcategory() {
		String category = "Musik";
		return category;
	}
}