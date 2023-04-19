
package de.k7bot.music.lavaplayer;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.time.OffsetDateTime;

import org.apache.hc.core5.http.ParseException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.source.local.LocalAudioTrack;
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;

import de.k7bot.Klassenserver7bbot;
import de.k7bot.music.commands.common.SkipCommand;
import de.k7bot.music.utilities.MusicUtil;
import de.k7bot.music.utilities.SongJson;
import de.k7bot.music.utilities.spotify.SpotifyAudioTrack;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.managers.AudioManager;
import net.dv8tion.jda.api.utils.FileUpload;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;
import se.michaelthelin.spotify.model_objects.specification.Image;
import se.michaelthelin.spotify.model_objects.specification.Track;

public class TrackScheduler extends AudioEventAdapter {

	private final Logger log;
	public static boolean next = false;

	public TrackScheduler() {
		log = LoggerFactory.getLogger(this.getClass());
	}

	@Override
	public void onPlayerPause(AudioPlayer player) {
	}

	@Override
	public void onPlayerResume(AudioPlayer player) {

	}

	@Override
	public void onTrackStart(AudioPlayer player, AudioTrack track) {

		if (!SkipCommand.onskip) {

			long guildid = Klassenserver7bbot.getInstance().getPlayerUtil().getGuildbyPlayerHash(player.hashCode());

			MusicController controller = Klassenserver7bbot.getInstance().getPlayerUtil().getController(guildid);
			Queue queue = controller.getQueue();

			AudioTrackInfo info = track.getInfo();

			SongJson jsinfo = null;
			if (track instanceof YoutubeAudioTrack) {
				jsinfo = queue.getCurrentSongData();
			}

			EmbedBuilder builder = new EmbedBuilder();

			String author = (jsinfo == null ? info.author : jsinfo.getAuthorString());
			String title = (jsinfo == null ? info.title : jsinfo.getTitle());

			builder.setColor(Color.decode("#00e640"));
			builder.setTimestamp(OffsetDateTime.now());
			builder.setDescription(" Jetzt läuft: " + title);

			long sekunden = info.length / 1000L;
			long minuten = sekunden / 60L;
			long stunden = minuten / 60L;
			minuten %= 60L;
			sekunden %= 60L;

			String url = info.uri;

			if (!(track instanceof LocalAudioTrack)) {
				builder.addField("Name", "[" + author + " - " + title + "](" + url + ")", false);
			} else {
				builder.addField("Name", author + " - " + title, false);
			}
			builder.addField("Länge: ",
					info.isStream ? "LiveStream"
							: (((stunden > 0L) ? (stunden + "h ") : "") + ((minuten > 0L) ? (minuten + "min ") : "")
									+ sekunden + "s"),
					true);

			FileUpload up = setIcons(track);

			if (up == null) {
				MusicUtil.sendEmbed(guildid, builder);
			} else {
				MusicUtil.sendEmbed(guildid, builder, up);
			}

		}
	}

	@Override
	public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {

		long guildid = Klassenserver7bbot.getInstance().getPlayerUtil().getGuildbyPlayerHash(player.hashCode());
		Guild guild = Klassenserver7bbot.getInstance().getShardManager().getGuildById(guildid);
		MusicController controller = Klassenserver7bbot.getInstance().getPlayerUtil().getController(guildid);
		Queue queue = controller.getQueue();

		if (track instanceof SpotifyAudioTrack) {
			new File(track.getIdentifier()).delete();
		}

		AudioManager manager;
		if (guild == null || (manager = guild.getAudioManager()) == null) {
			return;
		}

		if (endReason.mayStartNext) {

			if (queue.next(track) || player.getPlayingTrack() != null) {
				return;
			}

			player.stopTrack();
			queue.clearQueue();
			manager.closeAudioConnection();

		} else {

			if (queue.isemptyQueueList() && !next && !queue.isLooped()) {

				player.stopTrack();
				manager.closeAudioConnection();

			}

		}

	}

	private FileUpload setIcons(AudioTrack track) {

		if (track instanceof YoutubeAudioTrack) {
			return loadYT(track.getIdentifier());
		}

		if (track instanceof SpotifyAudioTrack) {
			return loadSpotify(track.getIdentifier());
		}
		return null;

	}

	private FileUpload loadSpotify(String songid) {

		final CloseableHttpClient client = HttpClients.createSystem();
		final HttpGet httpget = new HttpGet("https://open.spotify.com/get_access_token");

		try {

			final CloseableHttpResponse response = client.execute(httpget);

			if (response.getStatusLine().getStatusCode() != 200) {
				return null;

			}

			JsonObject resp = JsonParser.parseString(EntityUtils.toString(response.getEntity())).getAsJsonObject();

			String token = resp.get("accessToken").getAsString();

			SpotifyApi api = SpotifyApi.builder().setAccessToken(token).build();

			Track t = api.getTrack(songid).build().execute();

			Image[] images = t.getAlbum().getImages();

			Image img = images[0];

			for (Image imgs : images) {
				if (imgs.getHeight() > img.getHeight()) {
					img = imgs;
				}
			}

			InputStream file = (new URL(img.getUrl())).openStream();
			return FileUpload.fromData(file, "thumbnail.jpg");

		} catch (IOException | ParseException | SpotifyWebApiException e) {
			log.error(e.getMessage(), e);
			return null;
		}

	}

	private FileUpload loadYT(String videoId) {

		try {

			InputStream file;

			try {

				file = (new URL("https://img.youtube.com/vi/" + videoId + "/maxresdefault.jpg")).openStream();

			} catch (Exception ex) {

				log.warn("No maxresdefault.jpg available");

				file = (new URL("https://img.youtube.com/vi/" + videoId + "/hqdefault.jpg")).openStream();

			}

			return FileUpload.fromData(file, "thumbnail.jpg");

		} catch (IOException e) {
			log.error(e.getMessage(), e);
		}

		return null;
	}
}