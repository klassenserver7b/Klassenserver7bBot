package de.klassenserver7b.k7bot.commands.common;

import de.klassenserver7b.k7bot.HelpCategories;
import de.klassenserver7b.k7bot.Klassenserver7bbot;
import de.klassenserver7b.k7bot.commands.types.ServerCommand;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.middleman.GuildMessageChannel;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class GithubRepoCommand implements ServerCommand { 

 	private boolean isEnabled;

	private final Logger log;

	@Override
	public String gethelp() {
		return null;
	}

	@Override
	public String[] getCommandStrings() {
		return new String[] { "repo" };
	}

	@Override
	public HelpCategories getcategory() {
		return HelpCategories.UNKNOWN;
	}

	public GithubRepoCommand() {
		log = LoggerFactory.getLogger(this.getClass());
	}

	@Override
	public void performCommand(Member m, GuildMessageChannel channel, Message message) {

		GitHub ghub = Klassenserver7bbot.getInstance().getGitapi();

		try {

			GHRepository dcbot = ghub.getRepository("klassenserver7b/Klassenserver7bBot");

			channel.sendMessage("The last Bot update was at the: " + dcbot.getUpdatedAt()).queue();

		} catch (IOException e) {
			log.error(e.getMessage(), e);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}

	}
	
	@Override
	public boolean isEnabled() {
		return isEnabled;
	}

	@Override
	public void disableCommand() {
		isEnabled = false;
	}

	@Override
	public void enableCommand() {
		isEnabled = true;
	}

}
