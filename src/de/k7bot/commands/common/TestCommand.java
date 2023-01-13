package de.k7bot.commands.common;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.TargetDataLine;

import de.k7bot.HelpCategories;
import de.k7bot.commands.types.ServerCommand;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

public class TestCommand implements ServerCommand {

	@Override
	public String gethelp() {
		return null;
	}

	@Override
	public HelpCategories getcategory() {
		return HelpCategories.UNKNOWN;
	}

	@Override
	public void performCommand(Member m, TextChannel channel, Message message) {

		String[] args = message.getContentDisplay().split(" ");

		Mixer.Info minfo = getMusicMixerInfo();

		if (minfo == null) {
			return;
		}

		System.out.println(minfo.getName());

		Mixer mix = AudioSystem.getMixer(minfo);

		try {
			mix.open();
		} catch (LineUnavailableException e1) {
			e1.printStackTrace();
		}

		for (javax.sound.sampled.Line.Info info : mix.getTargetLineInfo()) {
			System.out.println(info);
		}

		if (args.length > 1) {
			try {
				saveAudio(mix, Long.valueOf(args[1]));
				return;
			} catch (NumberFormatException e) {
				saveAudio(mix);
				return;
			}
		}
		saveAudio(mix);
		return;

	}

	public Mixer.Info getMusicMixerInfo() {

//		return null;

//		return AudioSystem.getMixerInfo()[19];
//
		Mixer.Info[] mixerInfo = AudioSystem.getMixerInfo();
		for (Mixer.Info info : mixerInfo) {
			System.out.println(info);
			if (info.getName().startsWith("CABLE Output")) {
				return info;
			}
		}
		return null;
	}

	public void saveAudio(Mixer mixer, long frames) {
		saveAudioofTime(mixer, frames);
	}

	public void saveAudio(Mixer mix) {
		saveAudioofTime(mix, 400000);
	}

	public void saveAudioOnLine(TargetDataLine l) {
		try {

			l.open();

			ByteArrayOutputStream out = new ByteArrayOutputStream();
			int numBytesRead;
			byte[] data = new byte[l.getBufferSize() / 5];

			// Begin audio capture.
			l.start();

			// Here, stopped is a global boolean set by another thread.
			while (l.getFramePosition() < 400000) {
				// Read the next chunk of data from the TargetDataLine.
				numBytesRead = l.read(data, 0, data.length);
				// Save this chunk of data.
				out.write(data, 0, numBytesRead);
			}

			File f = new File("audio", "recordin-"
					+ OffsetDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm")) + ".raw");
			f.createNewFile();
			f.deleteOnExit();

			FileOutputStream outputStream = new FileOutputStream(f);
			out.writeTo(outputStream);

			l.close();

		} catch (LineUnavailableException | IOException e) {
			e.printStackTrace();
		}
	}

	private void saveAudioofTime(Mixer mixer, long frames) {

		System.out.println("AudioSave started");

		for (javax.sound.sampled.Line.Info info : mixer.getTargetLineInfo()) {
			System.out.println(info);
			try {
				TargetDataLine l = (TargetDataLine) AudioSystem.getLine(info);

				l.open();

				ByteArrayOutputStream out = new ByteArrayOutputStream();
				int numBytesRead;
				byte[] data = new byte[l.getBufferSize() / 5];

				// Begin audio capture.
				l.start();

				// Here, stopped is a global boolean set by another thread.
				while (l.getFramePosition() < frames) {
					// Read the next chunk of data from the TargetDataLine.
					numBytesRead = l.read(data, 0, data.length);
					// Save this chunk of data.
					out.write(data, 0, numBytesRead);
				}

				File f = new File("audio", "recordin-"
						+ OffsetDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm")) + ".raw");
				f.createNewFile();
				f.deleteOnExit();

				FileOutputStream outputStream = new FileOutputStream(f);
				out.writeTo(outputStream);

				l.close();

			} catch (LineUnavailableException | IOException e) {
				e.printStackTrace();
			}
		}

		System.out.println("AudioSave finished");
	}
}
