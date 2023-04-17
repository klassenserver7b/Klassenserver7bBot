package de.k7bot.commands.common;

import java.io.IOException;

import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.k7bot.HelpCategories;
import de.k7bot.Klassenserver7bbot;
import de.k7bot.commands.types.ServerCommand;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

public class GithubRepoCommand implements ServerCommand {

	private final Logger log;

	public GithubRepoCommand() {
		log = LoggerFactory.getLogger(this.getClass());
	}

	@Override
	public void performCommand(Member m, TextChannel channel, Message message) {

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
	public String gethelp() {
		return null;
	}

	@Override
	public HelpCategories getcategory() {
		return HelpCategories.UNKNOWN;
	}

}
