/**
 * 
 */
package de.k7bot.music.asms;

import static com.sedmelluq.discord.lavaplayer.tools.FriendlyException.Severity.SUSPICIOUS;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sedmelluq.discord.lavaplayer.container.MediaContainerDescriptor;
import com.sedmelluq.discord.lavaplayer.container.MediaContainerDetection;
import com.sedmelluq.discord.lavaplayer.container.MediaContainerDetectionResult;
import com.sedmelluq.discord.lavaplayer.container.MediaContainerHints;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.local.LocalAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.local.LocalAudioTrack;
import com.sedmelluq.discord.lavaplayer.source.local.LocalSeekableInputStream;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioItem;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioReference;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import com.sedmelluq.discord.lavaplayer.track.BasicAudioPlaylist;

/**
 * @author Felix
 *
 */
public class ExtendedLocalAudioSourceManager extends LocalAudioSourceManager implements AudioSourceManager {

	private final Logger log;

	/**
	 * 
	 */
	public ExtendedLocalAudioSourceManager() {
		super();
		log = LoggerFactory.getLogger(this.getClass());
	}

	@Override
	public AudioItem loadItem(AudioPlayerManager manager, AudioReference reference) {

		if (reference == null || reference.identifier == null) {
			return null;
		}
		File file = new File(reference.identifier);

		if (!file.exists()) {
			return null;
		}

		if (file.isFile() && file.canRead()) {
			return handleLoadResult(detectContainerForFile(reference, file));
		}
		if (file.isDirectory() && file.canRead()) {
			return loadPlaylist(manager, file);
		}
		return null;
	}

	private MediaContainerDetectionResult detectContainerForFile(AudioReference reference, File file) {
		try (LocalSeekableInputStream inputStream = new LocalSeekableInputStream(file)) {
			int lastDotIndex = file.getName().lastIndexOf('.');
			String fileExtension = lastDotIndex >= 0 ? file.getName().substring(lastDotIndex + 1) : null;

			return new MediaContainerDetection(containerRegistry, reference, inputStream,
					MediaContainerHints.from(null, fileExtension)).detectContainer();
		} catch (IOException e) {
			throw new FriendlyException("Failed to open file for reading.", SUSPICIOUS, e);
		}
	}

	public AudioPlaylist loadPlaylist(AudioPlayerManager manager, File folder) {
		ArrayList<AudioTrack> audioFiles = new ArrayList<>();

		for (File af : folder.listFiles()) {
			try {
				AudioTrack t = (AudioTrack) super.loadItem(manager,
						new AudioReference(af.getAbsolutePath(), af.getName()));

				if (t != null) {
					audioFiles.add(t);
				}
			} catch (FriendlyException e) {
				log.debug("couldn't load all files!");
			}

		}

		return new BasicAudioPlaylist("locals", audioFiles, null, false);
	}

	@Override
	public boolean isTrackEncodable(AudioTrack track) {
		return true;
	}

	@Override
	public void encodeTrack(AudioTrack track, DataOutput output) throws IOException {
		encodeTrackFactory(((LocalAudioTrack) track).getContainerTrackFactory(), output);

	}

	@Override
	public AudioTrack decodeTrack(AudioTrackInfo trackInfo, DataInput input) throws IOException {
		MediaContainerDescriptor containerTrackFactory = decodeTrackFactory(input);

		if (containerTrackFactory != null) {
			return new LocalAudioTrack(trackInfo, containerTrackFactory, this);
		}

		return null;
	}

	@Override
	public void shutdown() {
		// nothing to do here
	}

	@Override
	public String getSourceName() {
		return "Local Folder";
	}

}
