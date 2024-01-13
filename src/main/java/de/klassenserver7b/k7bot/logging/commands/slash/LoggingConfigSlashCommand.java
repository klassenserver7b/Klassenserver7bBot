/**
 *
 */
package de.klassenserver7b.k7bot.logging.commands.slash;

import java.awt.Color;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.klassenserver7b.k7bot.Klassenserver7bbot;
import de.klassenserver7b.k7bot.commands.types.TopLevelSlashCommand;
import de.klassenserver7b.k7bot.logging.LoggingOptions;
import de.klassenserver7b.k7bot.logging.commands.subcommandchain.LoggingCategoryEmbedProvider;
import de.klassenserver7b.k7bot.util.EmbedUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.interactions.components.ItemComponent;
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu;

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

		EmbedBuilder embbuild = EmbedUtils.getDefault();
		embbuild.setTitle("Logging Config");
		embbuild.setColor(Color.blue);

		StringBuilder strbuild = new StringBuilder();
		strbuild.append("Please select the type of Logging you want to configure");
		strbuild.append("\n\n");
		strbuild.append("Available are:");
		strbuild.append("\n");

		for (LoggingOptions option : LoggingOptions.values()) {
			if (option.getId() % 10 == 0) {
				strbuild.append(option.toString());
				strbuild.append(",");
				strbuild.append("\n");
			}
		}

		embbuild.setDescription(strbuild);

		MessageEmbed embed = embbuild.build();

		List<ItemComponent> ar = new LinkedList<>();

		StringSelectMenu.Builder strSelectBuilder = StringSelectMenu.create("logging-choose-category");

		for (LoggingOptions option : LoggingOptions.values()) {
			if (option.getId() % 10 == 0) {
				strSelectBuilder.addOption(option.toString(), "logging-catid-" + option.getId());
			}
		}

		ar.add(strSelectBuilder.build());

		hook.sendMessageEmbeds(embed).addActionRow(ar).queue();

		Klassenserver7bbot.getInstance().getShardManager().addEventListener(new LoggingCategoryEmbedProvider(hook));

	}

	@Override
	public SlashCommandData getCommandData() {
		return Commands.slash("loggingconfig", "get an embed to configure logging").setGuildOnly(true);
	}

}
