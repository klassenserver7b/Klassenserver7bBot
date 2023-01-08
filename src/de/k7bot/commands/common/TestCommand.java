package de.k7bot.commands.common;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

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

	}

	public void audioStuff(Member m, TextChannel channel, Message message) {
		Mixer.Info[] mixerInfo = AudioSystem.getMixerInfo();
		Mixer mixer = AudioSystem.getMixer(mixerInfo[11]);
		for (javax.sound.sampled.Line.Info info : mixer.getTargetLineInfo()) {
			try {
				TargetDataLine l = (TargetDataLine) AudioSystem.getLine(info);

				l.open();

				ByteArrayOutputStream out = new ByteArrayOutputStream();
				int numBytesRead;
				byte[] data = new byte[l.getBufferSize() / 5];

				// Begin audio capture.
				l.start();

				// Here, stopped is a global boolean set by another thread.
				while (l.getFramePosition() < 100000) {
					// Read the next chunk of data from the TargetDataLine.
					numBytesRead = l.read(data, 0, data.length);
					// Save this chunk of data.
					out.write(data, 0, numBytesRead);
				}

				File f = File.createTempFile("recording", "raw");
				f.deleteOnExit();

				FileOutputStream outputStream = new FileOutputStream(f);
				out.writeTo(outputStream);

			} catch (LineUnavailableException | IOException e) {
				e.printStackTrace();
			}
		}
	}
}
