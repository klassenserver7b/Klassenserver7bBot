package de.klassenserver7b.k7bot.music.utilities;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.TimeUnit;

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
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import de.klassenserver7b.k7bot.Klassenserver7bbot;
import de.klassenserver7b.k7bot.music.lavaplayer.MusicController;
import de.klassenserver7b.k7bot.music.utilities.spotify.SpotifyAudioTrack;
import de.klassenserver7b.k7bot.sql.LiteSQL;
import de.klassenserver7b.k7bot.util.GenericMessageSendHandler;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.managers.AudioManager;
import net.dv8tion.jda.api.utils.FileUpload;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;
import se.michaelthelin.spotify.model_objects.specification.Image;
import se.michaelthelin.spotify.model_objects.specification.Track;

/**
 *
 * @author Klassenserver7b
 *
 */
public class MusicUtil {

	private static final Logger log = LoggerFactory.getLogger(MusicUtil.class);

	/**
	 *
	 * @param channel
	 */
	public static void updateChannel(TextChannel channel) {

		try (ResultSet set = LiteSQL.onQuery("SELECT * FROM musicutil WHERE guildId = ?;",
				channel.getGuild().getIdLong())) {

			if (set.next()) {
				LiteSQL.onUpdate("UPDATE musicutil SET channelId = ? WHERE guildId = ?;", channel.getIdLong(),
						channel.getGuild().getIdLong());
			} else {
				LiteSQL.onUpdate("INSERT INTO musicutil(guildId, channelId, volume) VALUES(?, ?, ?);",
						channel.getGuild().getIdLong(), channel.getIdLong(), 10);
			}
		} catch (SQLException e) {
			log.error(e.getMessage(), e);
		}
	}

	/**
	 *
	 * @param hook
	 */
	public static void updateChannel(InteractionHook hook) {

		TextChannel channel = (TextChannel) hook.getInteraction().getMessageChannel();

		updateChannel(channel);
	}

	/**
	 *
	 * @param guildid
	 * @param builder
	 */
	public static void sendIconEmbed(long guildid, EmbedBuilder builder, AudioTrack track) {

		try (ResultSet set = LiteSQL.onQuery("SELECT channelId FROM musicutil WHERE guildId = ?", guildid)) {

			if (!set.next()) {
				return;
			}

			long channelid = set.getLong("channelId");
			Guild guild;

			if ((guild = Klassenserver7bbot.getInstance().getShardManager().getGuildById(guildid)) == null) {
				return;
			}

			TextChannel channel;

			if ((channel = guild.getTextChannelById(channelid)) != null) {

				@SuppressWarnings("resource")
				FileUpload up = setIcons(track);

				if (up == null) {
					channel.sendMessageEmbeds(builder.build()).queue();
				} else {
					builder.setImage("attachment://thumbnail.png");
					channel.sendFiles(up).setEmbeds(builder.build()).queue();
					up.close();
				}

			}

		} catch (SQLException | IOException e) {
			log.error(e.getMessage(), e);
		}
	}

	/**
	 *
	 * @param guildid
	 * @param builder
	 */
	public static void sendEmbed(long guildid, EmbedBuilder builder) {

		try (ResultSet set = LiteSQL.onQuery("SELECT channelId FROM musicutil WHERE guildId = ?", guildid)) {

			if (!set.next()) {
				return;
			}
			long channelid = set.getLong("channelId");
			Guild guild;

			if ((guild = Klassenserver7bbot.getInstance().getShardManager().getGuildById(guildid)) == null) {
				return;
			}

			TextChannel channel;
			if ((channel = guild.getTextChannelById(channelid)) != null) {
				channel.sendMessageEmbeds(builder.build()).queue();

			}

		} catch (SQLException e) {
			log.error(e.getMessage(), e);
		}
	}

	/**
	 *
	 * @param m
	 * @return
	 */
	public static boolean membHasVcConnection(Member m) {

		GuildVoiceState state;
		if ((state = m.getVoiceState()) != null) {
			if (state.getChannel() != null) {
				return true;
			}
		}
		return false;
	}

	/**
	 *
	 * @param m
	 * @return
	 */
	public static AudioChannel getMembVcConnection(Member m) {

		GuildVoiceState state;
		if ((state = m.getVoiceState()) != null) {
			AudioChannel vc;
			if ((vc = state.getChannel()) != null) {
				return vc;
			}
		}
		return null;
	}

	/**
	 *
	 * @param ac
	 * @return
	 */
	public static int isConnectedtoChannel(AudioChannel ac) {
		AudioManager audioman = ac.getGuild().getAudioManager();

		if (audioman.getConnectedChannel() == null) {
			return 2;
		}

		if ((ac.getIdLong() == audioman.getConnectedChannel().getIdLong())) {
			return 1;
		}

		return 0;
	}

	/**
	 *
	 * @param sendHandler
	 * @param m
	 * @return
	 */
	public static boolean checkDefaultConditions(GenericMessageSendHandler sendHandler, Member m) {

		if (!MusicUtil.membHasVcConnection(m)) {
			sendHandler.sendMessage("You are not in a voicechannel" + m.getAsMention()).complete().delete()
					.queueAfter(10L, TimeUnit.SECONDS);
			return false;
		}

		return checkChannelConnection(sendHandler, m, true);
	}

	/**
	 *
	 * @param sendHandler
	 * @param m
	 * @return
	 */
	public static boolean checkConditions(GenericMessageSendHandler sendHandler, Member m) {

		if (!MusicUtil.membHasVcConnection(m)) {
			sendHandler.sendMessage("You are not in a voicechannel" + m.getAsMention()).complete().delete()
					.queueAfter(10L, TimeUnit.SECONDS);
			return false;
		}

		return checkChannelConnection(sendHandler, m, false);
	}

	protected static boolean checkChannelConnection(GenericMessageSendHandler sendHandler, Member m,
			boolean checkBeforePlay) {

		AudioChannel vc = MusicUtil.getMembVcConnection(m);

		switch (MusicUtil.isConnectedtoChannel(vc)) {
		case 0: {
			sendHandler.sendMessage("You are not connected to the music playing VoiceChannel" + m.getAsMention())
					.complete().delete().queueAfter(10L, TimeUnit.SECONDS);
			return false;
		}
		case 2: {

			if (checkBeforePlay) {
				return true;
			}

			sendHandler.sendMessage("The Bot isn't playing a Song use -p [Song] to play one" + m.getAsMention())
					.complete().delete().queueAfter(10L, TimeUnit.SECONDS);
			return false;
		}
		}

		return true;

	}

	/**
	 *
	 * @param m
	 * @return
	 */
	public static boolean checkConditions(Member m) {

		if (!MusicUtil.membHasVcConnection(m)) {
			return false;
		}

		AudioChannel vc = MusicUtil.getMembVcConnection(m);

		switch (MusicUtil.isConnectedtoChannel(vc)) {
		case 0: {
			return false;
		}
		case 2: {
			return false;
		}
		}

		return true;
	}

	/**
	 *
	 * @param c
	 * @param m
	 * @return
	 */
	public static boolean isPlayingSong(TextChannel c, Member m) {

		MusicController controller = Klassenserver7bbot.getInstance().getPlayerUtil()
				.getController(c.getGuild().getIdLong());

		if (controller == null) {
			c.sendMessage("The Bot isn't playing a Song use -p [Song] to play one" + m.getAsMention()).complete()
					.delete().queueAfter(10L, TimeUnit.SECONDS);
			return false;
		}

		AudioPlayer player = controller.getPlayer();

		if (player.getPlayingTrack() == null) {
			c.sendMessage("The Bot isn't playing a Song use -p [Song] to play one" + m.getAsMention()).complete()
					.delete().queueAfter(10L, TimeUnit.SECONDS);
			return false;
		}

		return true;

	}

	private static FileUpload setIcons(AudioTrack track) {

		if (track instanceof YoutubeAudioTrack) {
			return loadYTIcon(track.getIdentifier());
		}

		if (track instanceof SpotifyAudioTrack) {
			return loadSpotifyIcon(track.getIdentifier());
		}
		return null;

	}

	@SuppressWarnings("resource")
	private static FileUpload loadSpotifyIcon(String songid) {

		final HttpGet httpget = new HttpGet("https://open.spotify.com/get_access_token");

		try (final CloseableHttpClient client = HttpClients.createSystem();
				final CloseableHttpResponse response = client.execute(httpget)) {

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

	@SuppressWarnings("resource")
	private static FileUpload loadYTIcon(String videoId) {

		try {
			InputStream file = (new URL("https://img.youtube.com/vi/" + videoId + "/maxresdefault.jpg")).openStream();

			return FileUpload.fromData(file, "thumbnail.jpg");

		} catch (IOException e) {

			log.warn("No maxresdefault.jpg available");

			try (InputStream file = (new URL("https://img.youtube.com/vi/" + videoId + "/hqdefault.jpg"))
					.openStream()) {

				return FileUpload.fromData(file, "thumbnail.jpg");

			} catch (IOException e1) {
				log.error(e.getMessage(), e);
			}

		}

		return null;
	}
}