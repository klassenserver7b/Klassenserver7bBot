/**
 *
 */
package de.klassenserver7b.k7bot.logging.commands.slash;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.klassenserver7b.k7bot.Klassenserver7bbot;
import de.klassenserver7b.k7bot.commands.types.TopLevelSlashCommand;
import de.klassenserver7b.k7bot.logging.commands.subcommandchain.LoggingConfigEmbedProvider;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

/**
 *
 */
public class LoggingConfigSlashCommand implements TopLevelSlashCommand {

	@SuppressWarnings("unused")
	private final Logger log;

	/**
	 *
	 */
	public LoggingConfigSlashCommand() {
		log = LoggerFactory.getLogger(getClass());
	}

	@Override
	public void performSlashCommand(SlashCommandInteraction event) {
		InteractionHook hook = event.deferReply().complete();

		Klassenserver7bbot.getInstance().getShardManager().addEventListener(new LoggingConfigEmbedProvider(hook));

	}

	@Override
	public SlashCommandData getCommandData() {
		return Commands.slash("loggingconfig", "get an embed to configure logging")
				.setDefaultPermissions(DefaultMemberPermissions.DISABLED).setGuildOnly(true);
	}

}
