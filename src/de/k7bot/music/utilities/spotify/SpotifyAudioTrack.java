/**
 * 
 */
package de.k7bot.music.utilities.spotify;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Base64;
import java.util.Date;
import java.util.List;

import javax.naming.OperationNotSupportedException;

import org.apache.commons.codec.binary.Hex;
import org.apache.hc.client5.http.HttpHostConnectException;
import org.apache.hc.client5.http.HttpResponseException;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.BasicHttpClientResponseHandler;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.HttpHeaders;
import org.apache.hc.core5.http.io.entity.ByteArrayEntity;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.io.CloseMode;
import org.bouncycastle.crypto.CryptoException;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.io.Files;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.google.protobuf.InvalidProtocolBufferException;
import com.sedmelluq.discord.lavaplayer.container.mpeg.MpegAudioTrack;
import com.sedmelluq.discord.lavaplayer.source.local.LocalSeekableInputStream;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException.Severity;
import com.sedmelluq.discord.lavaplayer.tools.io.SeekableInputStream;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import com.sedmelluq.discord.lavaplayer.track.DelegatedAudioTrack;
import com.sedmelluq.discord.lavaplayer.track.playback.LocalAudioTrackExecutor;

import de.k7bot.lib.widevine4j.CDMDevice;
import de.k7bot.lib.widevine4j.CDMSession;
import de.k7bot.lib.widevine4j.ContentKey;
import de.k7bot.music.asms.SpotifyAudioSourceManager;
import io.seruco.encoding.base62.Base62;

/**
 * @author Felix
 *
 */
public class SpotifyAudioTrack extends DelegatedAudioTrack {

	private final SpotifyAudioSourceManager sasm;
	private final Logger log;
	private final CloseableHttpClient httpclient;

	/**
	 * 
	 */
	public SpotifyAudioTrack(AudioTrackInfo trackInfo, SpotifyAudioSourceManager sasm) {
		super(trackInfo);
		this.sasm = sasm;
		this.log = LoggerFactory.getLogger(getClass());
		httpclient = HttpClients.createSystem();
	}

	@Override
	public void process(LocalAudioTrackExecutor executor) throws Exception {

		File decr = decryptTrack(super.getIdentifier());
		decr.deleteOnExit();

		try (SeekableInputStream stream = new LocalSeekableInputStream(decr)) {
			new MpegAudioTrack(trackInfo, stream).process(executor);
		}

		httpclient.close(CloseMode.IMMEDIATE);
		decr.delete();

	}

	private File decryptTrack(String trackid) {

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

		File encryptedf = downloadFileFromURL(fileurl, fileid[1]);
		encryptedf.deleteOnExit();

		String[] filepath = encryptedf.getAbsolutePath().split("\\.");

		File outputfile;
		try {
			outputfile = File.createTempFile(filepath[0] + "_decrypted", "." + filepath[1]);
		} catch (IOException e1) {
			log.error(e.getMessage(), e);
			return null;
		}

		// DRM Conversion
		convert(encryptedf, outputfile, fileid[0]);

		// encryptedf.delete();

		return outputfile;
	}

	private boolean convert(File fin, File fout, String fileid) {

		File pvtkey = new File("resources/private_key.pem");
		File cli_id = new File("resources/client_id.bin");

		if (!pvtkey.exists() || !cli_id.exists()) {
			return false;
		}

		CDMDevice device;

		try {
			device = new CDMDevice(true, getBytesFromFile(cli_id), getBytesFromFile(pvtkey), null);
		} catch (IOException e) {
			log.error(e.getMessage(), e);
			return false;
		}

		byte[] pssh = getPSSHFromFileId(fileid);

		if (pssh == null) {
			return false;
		}

		CDMSession session = new CDMSession(device, pssh);

		try {
			session.updateCertificate(performLicenseRequest(CDMSession.CERTIFICATE_REQUEST));
		} catch (InvalidProtocolBufferException e1) {
			log.error(e1.getMessage(), e1);
			return false;
		}

		byte[] license;

		try {
			license = session.getLicenseRequest(true);
		} catch (CryptoException e) {
			log.error(e.getMessage(), e);
			return false;
		}

		List<ContentKey> keys;

		try {
			keys = session.decodeLicense(performLicenseRequest(license));
		} catch (InvalidProtocolBufferException | InvalidCipherTextException e) {
			log.error(e.getMessage(), e);
			return false;
		}

		try {

			ContentKey key = keys.get(0);
			StringBuilder output = new StringBuilder();
			for (byte b : key.kid())
				output.append(String.format("%02x", b));
			output.append(":");
			for (byte b : key.key())
				output.append(String.format("%02x", b));

			int status = decryptMP4File(output.toString(), fin, fout);
			log.debug("Decrypt finished with " + status);

			if (status == 0) {
				log.info("Decrypt finished successful! - file: " + fout.getAbsolutePath());
			} else {
				log.warn("Decrypt failed! - retrying");
				Thread.sleep(1000);
				status = decryptMP4File(output.toString(), fin, fout);
				if (status == 0) {
					log.info("Retrying decryption finished successful! - file: " + fout.getAbsolutePath());
				} else {
					log.warn("Retrying decryption failed! file: " + fout.getAbsolutePath());
				}
			}

			fin.delete();

		} catch (IOException | InterruptedException | OperationNotSupportedException e) {
			log.error(e.getMessage(), e);
			return false;
		}

		return true;
	}

	/**
	 * 
	 * @param contentkey
	 * @param fin
	 * @param fout
	 * @return
	 * @throws IOException
	 * @throws InterruptedException
	 * @throws OperationNotSupportedException
	 */
	protected int decryptMP4File(String contentkey, File fin, File outpath)
			throws IOException, InterruptedException, OperationNotSupportedException {

		String ospath = getOSPath();

		String pathstr = new File("").getAbsolutePath() + "/resources/sdk/" + ospath + "/bin/mp4decrypt";

		if (ospath.equals("win_x86_64")) {
			pathstr += ".exe";
		}

		if (!new File(pathstr).exists()) {
			System.err.println(pathstr);
			throw new IOException("mp4decrypt not found");
		}

		int exitCode = new ProcessBuilder()
				.command(
						new String[] { pathstr, "--key", contentkey, fin.getAbsolutePath(), outpath.getAbsolutePath() })
				.inheritIO().start().waitFor();
		return exitCode;
	}

	/**
	 *
	 * @param url
	 * @param extension
	 * @return
	 */
	private File downloadFileFromURL(String url, String extension) {

		try {

			final HttpGet httpget = new HttpGet(url);
			httpget.addHeader("Authorization", "Bearer " + sasm.getSpotifyInteract().getSpotifyApi().getAccessToken());

			byte[] bresponse;
			try {
				bresponse = httpclient.execute(httpget, response -> EntityUtils.toByteArray(response.getEntity()));
			} catch (HttpResponseException e) {
				log.warn("Invalid response from https://api.spotify.com/v1/storage-resolve/files/audio/interactive - "
						+ e.getMessage());
				return null;
			}

			File targetFile = File.createTempFile("K7Bot_Spotify_" + new Date().getTime(), "." + extension);
			targetFile.deleteOnExit();

			try (OutputStream outStream = new FileOutputStream(targetFile)) {
				outStream.write(bresponse);
			}

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

		final HttpGet httpget = new HttpGet(url);
		httpget.addHeader("Authorization", "Bearer " + sasm.getSpotifyInteract().getSpotifyApi().getAccessToken());

		try {

			String response = null;
			response = httpclient.execute(httpget, new BasicHttpClientResponseHandler());

			JsonElement elem = JsonParser.parseString(response);

			return elem.getAsJsonObject().get("cdnurl").getAsJsonArray().get(0).getAsString();

		} catch (HttpResponseException e) {
			log.warn("Invalid response from https://api.spotify.com/v1/storage-resolve/files/audio/interactive - "
					+ e.getMessage());
			return null;
		} catch (IOException | JsonSyntaxException e) {

			log.error(e.getMessage(), e);

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

			if (fid.isJsonNull()) {
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

		final HttpGet httpget = new HttpGet(url);
		httpget.addHeader("Authorization", "Bearer " + sasm.getSpotifyInteract().getSpotifyApi().getAccessToken());

		try {

			String response = httpclient.execute(httpget, new BasicHttpClientResponseHandler());

			JsonElement elem = JsonParser.parseString(response);

			return elem.getAsJsonObject().get("file").getAsJsonArray();

		} catch (HttpResponseException e) {
			log.warn("Invalid response from spclient.wg.spotify.com - " + e.getMessage());
			return null;
		} catch (IOException | JsonSyntaxException e) {
			log.error(e.getMessage(), e);
		}

		return null;

	}

	private byte[] getPSSHFromFileId(String fileid) {

		String baseurl = "https://seektables.scdn.co/seektable/%s.json";

		try {

			final HttpGet httpget = new HttpGet(new URI(String.format(baseurl, fileid)));

			httpget.setHeader(HttpHeaders.CONTENT_TYPE, "application/json");

			final String response = httpclient.execute(httpget, new BasicHttpClientResponseHandler());

			JsonElement elem = JsonParser.parseString(response);

			String resp = elem.getAsJsonObject().get("pssh").getAsString();

			return Base64.getDecoder().decode(resp);

		} catch (HttpHostConnectException e1) {
			log.warn("Invalid response from seektables.scdn.co" + e1.getMessage());
		} catch (IOException | JsonSyntaxException e) {
			log.error(e.getMessage(), e);
		} catch (URISyntaxException e) {
			log.error(e.getMessage(), e);
		}

		return null;
	}

	private byte[] performLicenseRequest(byte[] license) {

		String url = "https://api.spotify.com/v1/widevine-license/v1/audio/license";

		try (ByteArrayEntity byteentity = new ByteArrayEntity(license, ContentType.APPLICATION_FORM_URLENCODED)) {

			final HttpPost httppost = new HttpPost(url);
			httppost.setEntity(byteentity);
			httppost.setHeader(HttpHeaders.AUTHORIZATION,
					"Bearer " + sasm.getSpotifyInteract().getSpotifyApi().getAccessToken());

			return httpclient.execute(httppost, response -> EntityUtils.toByteArray(response.getEntity()));

		} catch (HttpHostConnectException e1) {
			log.warn("Invalid response from api.spotify.com/v1/widevine-license/v1/audio/license" + e1.getMessage());
		} catch (IOException | JsonSyntaxException e) {
			log.error(e.getMessage(), e);
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
	 * @return
	 * @throws OperationNotSupportedException
	 */
	public String getOSPath() throws OperationNotSupportedException {
		String os = System.getProperty("os.name").toLowerCase();
		if (os.contains("win")) {
			return "win_x86_64";
		} else if (os.contains("osx")) {
			return "macosx";
		} else if (os.contains("nix") || os.contains("aix") || os.contains("nux")) {
			return "linux_x86_64";
		}
		throw new OperationNotSupportedException("Invalid OS");
	}

	/**
	 * 
	 * @param f
	 * @return
	 * @throws IOException
	 */
	private byte[] getBytesFromFile(File f) throws IOException {

		return Files.toByteArray(f);

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
