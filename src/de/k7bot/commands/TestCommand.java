package de.k7bot.commands;

import java.util.List;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import de.k7bot.commands.types.ServerCommand;
import de.k7bot.util.SpotifyConverter;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;

public class TestCommand implements ServerCommand {

	@Override
	public String gethelp() {
		return null;
	}

	@Override
	public String getcategory() {
		return null;
	}

	@Override
	public void performCommand(Member m, TextChannel channel, Message message) {
		
		SpotifyConverter conv = new SpotifyConverter();
		channel.sendTyping().queue();
		
		List<AudioTrack> results = conv.convertPlaylist(message.getContentDisplay().split(" ")[1].replaceAll("https://open.spotify.com/playlist/", ""));
		
		results.forEach(res ->{
			
			System.out.println(res.getInfo().title);
			
		});
		
		
	}

}
