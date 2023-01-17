/**
 *
 */
package de.k7bot.util.customapis;

import java.awt.Color;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.kohsuke.github.GHCommit;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;
import org.kohsuke.github.HttpException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.k7bot.Klassenserver7bbot;
import de.k7bot.sql.LiteSQL;
import de.k7bot.subscriptions.types.SubscriptionTarget;
import de.k7bot.util.customapis.types.InternalAPI;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;

/**
 * @author Klassenserver7b
 *
 */
public class GitHubAPI implements InternalAPI {

	private final GitHub gh;
	private final Logger log;

	public GitHubAPI() {

		gh = Klassenserver7bbot.getInstance().getGitapi();
		log = LoggerFactory.getLogger(this.getClass().getCanonicalName());

	}

	@Override
	public void checkforUpdates() {

		List<String> newcommits = getNewCommits();

		if (newcommits == null || newcommits.isEmpty()) {
			return;
		}

		for (String s : newcommits) {

			String[] split = s.split("\n");

			EmbedBuilder b = new EmbedBuilder();
			b.setColor(Color.decode("#7289da"));
			b.setTitle(split[0]);

			String body = "";

			for (int i = 2; i < split.length; i++) {
				body += split[i] + "\n";
			}

			b.setDescription(body);

			MessageCreateBuilder messb = new MessageCreateBuilder();
			messb.setEmbeds(b.build());

			Klassenserver7bbot.getInstance().getSubscriptionManager()
					.provideSubscriptionNotification(SubscriptionTarget.BOT_NEWS, messb.build());

		}

	}

	private List<String> getNewCommits() {

		ResultSet set = LiteSQL.onQuery("SELECT * FROM githubinteractions;");

		GHRepository repo;
		List<GHCommit> commitl;
		String dbid;

		try {

			repo = gh.getRepository("klassenserver7b/Klassenserver7bBot");
			commitl = repo.listCommits().toList();

			if (!set.next() && !commitl.isEmpty()) {

				LiteSQL.onUpdate("INSERT INTO githubinteractions(lastcommit) VALUES(?);", commitl.get(0).getSHA1());
				dbid = "";

			} else {

				dbid = set.getString("lastcommit");
			}

		} catch (HttpException e1) {
			log.warn("Github Connection failed!");
			return null;
		} catch (IOException | SQLException e) {
			log.error(e.getMessage(), e);
			return null;
		}

		if (commitl.isEmpty()) {
			return null;
		}

		String commitid = commitl.get(0).getSHA1();

		if (commitid.equalsIgnoreCase(dbid)) {
			return null;
		}

		try {
			LiteSQL.onUpdate("UPDATE githubinteractions SET lastcommit = ?;", commitid);
			return getCommitsSince(commitl, dbid);
		} catch (IOException e) {
			log.error(e.getMessage(), e);
			return null;
		}

	}

	private List<String> getCommitsSince(List<GHCommit> comms, String sha) throws IOException {

		List<String> messages = new ArrayList<>();

		for (GHCommit c : comms) {

			if (c.getSHA1().equalsIgnoreCase(sha)) {
				break;
			}

			messages.add(c.getCommitShortInfo().getMessage());

		}

		return messages;

	}

	@Override
	public void shutdown() {
		log.debug("GitHubAPI disabled");
	}

}
