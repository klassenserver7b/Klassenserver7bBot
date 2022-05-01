package de.k7bot.commands;

import java.io.IOException;

import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;

import de.k7bot.Klassenserver7bbot;
import de.k7bot.commands.types.ServerCommand;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;

public class GithubRepoCommand implements ServerCommand{

	@Override
	public void performCommand(Member m, TextChannel channel, Message message) {
		
		GitHub ghub = Klassenserver7bbot.INSTANCE.getGitapi();
		
		GHRepository dcbot;
		try {
			
			dcbot = ghub.getRepositoryById(408473810);
			channel.sendMessage("The last Bot update was at the: "+dcbot.getUpdatedAt()).queue();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
	}
	
	@Override
	public String gethelp() {
		return null;
	}

	@Override
	public String getcategory() {
		return null;
	}

}
