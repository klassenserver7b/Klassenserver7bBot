/**
 *
 */
package de.k7bot.music.utilities.spotify;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;

import org.apache.commons.codec.binary.Hex;
import org.apache.hc.client5.http.HttpResponseException;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.entity.EntityBuilder;
import org.apache.hc.client5.http.impl.classic.BasicHttpClientResponseHandler;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.HttpEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioTrack;
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeSearchMusicProvider;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException.Severity;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import com.sedmelluq.discord.lavaplayer.track.BasicAudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.DelegatedAudioTrack;
import com.sedmelluq.discord.lavaplayer.track.playback.LocalAudioTrackExecutor;

import de.k7bot.music.asms.SpotifyAudioSourceManager;
import de.k7bot.util.EntityHttpCLientResponseHandler;
import de.k7bot.util.HttpUtilities;
import io.seruco.encoding.base62.Base62;

/**
 * @author Felix
 *
 */
public class SpotifyAudioTrack extends DelegatedAudioTrack {

	private final SpotifyAudioSourceManager sasm;
	private final Logger log;
	private final YoutubeAudioSourceManager ytm;

	/**
	 * @param trackInfo
	 */
	public SpotifyAudioTrack(AudioTrackInfo trackInfo, SpotifyAudioSourceManager sasm) {
		super(trackInfo);
		this.sasm = sasm;
		ytm = new YoutubeAudioSourceManager();
		this.log = LoggerFactory.getLogger(getClass());
	}

	@Override
	public void process(LocalAudioTrackExecutor executor) throws Exception {

		sasm.hashCode();

		// File decr = decryptTrack(super.getIdentifier());

		BasicAudioPlaylist pl = (BasicAudioPlaylist) new YoutubeSearchMusicProvider().loadSearchMusicResult(
				super.getInfo().author + " - " + super.getInfo().title, SpotifyAudioTrack.this::buildTrackFromInfo);
		YoutubeAudioTrack track = (YoutubeAudioTrack) pl.getTracks().get(0);

		track.process(executor);

	}

	private YoutubeAudioTrack buildTrackFromInfo(AudioTrackInfo info) {
		return new YoutubeAudioTrack(info, ytm);
	}

	@SuppressWarnings("unused")
	private File decryptTrack(String trackid) {
		new AudioTrackInfo(trackid, trackid, 0, trackid, false, trackid);

		FriendlyException e = new FriendlyException("Error on loading Track " + trackid, Severity.COMMON,
				new Throwable("Error on loading Track " + trackid));

		String hexid = convertTrackIdtoHex(trackid);

		JsonArray files = getFilesFromHexId(hexid);

		if (files == null || files.isJsonNull()) {
			throw e;
		}

		String[] fileid = getFileIdFromFilesArray(files);

		if (fileid == null) {
			throw e;
		}

		String fileurl = getFileURLFromFileId(fileid[0]);

		File decryptedf = downloadFileFromURL(fileurl, fileid[1]);
		decryptedf.deleteOnExit();

		// DRM Conversion
		String license = getLicense();

		File drmconverted = decryptedf;

		decryptedf.delete();

		return drmconverted;
	}

	private String getLicense() {

		try {

			final CloseableHttpClient httpclient = HttpClients.createSystem();
			final HttpPost httppost = new HttpPost("https://api.spotify.com/v1/widevine-license/v1/audio/license");
			httppost.addHeader("Authorization", "Bearer " + sasm.getSpotifyInteract().getSpotifyApi().getAccessToken());

			EntityBuilder builder = EntityBuilder.create();

			builder.setContentType(ContentType.DEFAULT_TEXT);
			builder.setText("\b×+K\r\n" + "I\r\n"
					+ "3\bÉÏÃøK¯ºWÌå7Äspotify\"ÉÏÃøK¯ºWÌå7Ä Ë)ìÈkêGP	È¥\f.Ïç}* ¿08ñÿÃ\bB÷*\r\n"
					+ "spotify.comO-'ÙaYz|½*44hëRÀ(RÔ¯e(+s[Î»í=4»lâ|½mKÔãÒ?ÂKÛQ¦McÌ%¹+ÁÚÂn¶\"DËt\"¼5z8øÁÿ½ïF[ï²ÚÉ]\r\n"
					+ "7É;ÙÊ Q7Ä<]&Ùy8£ï@G§¢LRoýø¸õâ¸rbÀÅÖI°Í¯Öy¨mf´¢dûá´e'«¨.è\bøóób*@SÆÿÿ`ä~ïªìs¥Q°CÊ3FXA°Í×oýSpoL4;GòvÉñ)pDÜ");

			httppost.setEntity(builder.build());

			String response = null;
			try {
				response = httpclient.execute(httppost, new BasicHttpClientResponseHandler());
			} catch (HttpResponseException e) {
				log.warn("Invalid response from Spotify License Server - " + e.getMessage());
				return null;
			}

			httpclient.close();

			return response;

		} catch (IOException e) {
			log.error(e.getMessage(), e);
		}

		return null;

	}

	/**
	 *
	 * @param url
	 * @param extension
	 * @return
	 */
	private File downloadFileFromURL(String url, String extension) {

		try {

			final CloseableHttpClient httpclient = HttpClients.createSystem();
			final HttpGet httpget = new HttpGet(url);
			httpget.addHeader("Authorization", "Bearer " + sasm.getSpotifyInteract().getSpotifyApi().getAccessToken());

			HttpEntity response = null;
			try {
				response = httpclient.execute(httpget, new EntityHttpCLientResponseHandler());
			} catch (HttpResponseException e) {
				log.warn("Invalid response from https://api.spotify.com/v1/storage-resolve/files/audio/interactive - "
						+ e.getMessage());
				return null;
			}
			InputStream filein = response.getContent();

			File targetFile = File.createTempFile("K7Bot_Spotify_" + new Date().getTime(), "." + extension);
			targetFile.deleteOnExit();

			OutputStream outStream = new FileOutputStream(targetFile);

			byte[] buffer = new byte[8 * 1024];
			int bytesRead;
			while ((bytesRead = filein.read(buffer)) != -1) {
				outStream.write(buffer, 0, bytesRead);
			}

			outStream.close();
			filein.close();

			response.close();
			httpclient.close();

			return targetFile;

		} catch (IOException e) {
			log.error(e.getMessage(), e);
		}

		return null;

	}

	/**
	 *
	 * @param fileid
	 * @return
	 */
	private String getFileURLFromFileId(String fileid) {

		String url = "https://api.spotify.com/v1/storage-resolve/files/audio/interactive/";
		url += fileid;
		url += "?product=9&alt=json";

		final CloseableHttpClient httpclient = HttpClients.createSystem();
		final HttpGet httpget = new HttpGet(url);
		httpget.addHeader("Authorization", "Bearer " + sasm.getSpotifyInteract().getSpotifyApi().getAccessToken());

		try {

			String response = null;
			response = httpclient.execute(httpget, new BasicHttpClientResponseHandler());

			JsonElement elem = JsonParser.parseString(response);

			httpclient.close();

			return elem.getAsJsonObject().get("cdnurl").getAsJsonArray().get(0).getAsString();

		} catch (HttpResponseException e) {
			log.warn("Invalid response from https://api.spotify.com/v1/storage-resolve/files/audio/interactive - "
					+ e.getMessage());
			return null;
		} catch (IOException | JsonSyntaxException e) {

			log.error(e.getMessage(), e);

			HttpUtilities.closeHttpClient(httpclient);
		}

		return null;

	}

	/**
	 *
	 * @param array
	 * @return
	 */
	private String[] getFileIdFromFilesArray(JsonArray array) {

		for (JsonElement elem : array) {

			if (elem == null || elem.isJsonNull()) {
				return null;
			}

			JsonElement fid = elem.getAsJsonObject().get("file_id");

			if (fid == null || fid.isJsonNull()) {
				return null;
			}

			JsonElement format = elem.getAsJsonObject().get("format");

			if (fid == null || fid.isJsonNull()) {
				return null;
			}

			if (format.getAsString().startsWith("MP4")) {
				return new String[] { fid.getAsString(), format.getAsString().split("_")[0] };
			}

		}

		return null;

	}

	/**
	 *
	 * @param hexid
	 * @return
	 */
	private JsonArray getFilesFromHexId(String hexid) {

		String url = "https://spclient.wg.spotify.com/metadata/4/track/";
		url += hexid;
		url += "?market=from_token&alt=json";

		final CloseableHttpClient httpclient = HttpClients.createSystem();
		final HttpGet httpget = new HttpGet(url);
		httpget.addHeader("Authorization", "Bearer " + sasm.getSpotifyInteract().getSpotifyApi().getAccessToken());

		try {

			String response = httpclient.execute(httpget, new BasicHttpClientResponseHandler());

			JsonElement elem = JsonParser.parseString(response);

			httpclient.close();

			return elem.getAsJsonObject().get("file").getAsJsonArray();

		} catch (HttpResponseException e) {
			log.warn("Invalid response from spclient.wg.spotify.com - " + e.getMessage());
			return null;
		} catch (IOException | JsonSyntaxException e) {
			log.error(e.getMessage(), e);

			HttpUtilities.closeHttpClient(httpclient);
		}

		return null;

	}

	/**
	 *
	 * @param trackid
	 * @return
	 */
	private String convertTrackIdtoHex(String trackid) {

		String caseswitchedid = switchCasing(trackid);
		caseswitchedid = caseswitchedid.trim();

		Base62 base62 = Base62.createInstance();

		byte[] decoded = base62.decode(caseswitchedid.getBytes());

		if (decoded[0] == 0) {
			return Hex.encodeHexString(decoded).substring(2);
		}

		return Hex.encodeHexString(decoded);

	}

	/**
	 *
	 * @param str
	 * @return
	 */
	private String switchCasing(String str) {

		String s = "";
		int i = 0;
		while (i < str.length()) {
			Character n = str.charAt(i);
			if (n == Character.toUpperCase(n)) {
				// *Call* toLowerCase
				n = Character.toLowerCase(n);
			} else {
				// *Call* toUpperCase
				n = Character.toUpperCase(n);
			}

			i += 1;
			s += n;
		}

		return s;
	}

}
