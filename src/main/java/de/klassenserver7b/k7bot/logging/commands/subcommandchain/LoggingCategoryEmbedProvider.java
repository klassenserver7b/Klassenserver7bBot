/**
 * 
 */
package de.klassenserver7b.k7bot.logging.commands.subcommandchain;

import java.awt.Color;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import de.klassenserver7b.k7bot.Klassenserver7bbot;
import de.klassenserver7b.k7bot.logging.LoggingOptions;
import de.klassenserver7b.k7bot.util.EmbedUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.ItemComponent;
import net.dv8tion.jda.api.interactions.components.LayoutComponent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu;

/**
 * 
 */
public class LoggingCategoryEmbedProvider extends ListenerAdapter {

	/**
	 * 
	 */
	public LoggingCategoryEmbedProvider(InteractionHook hook) {
	}

	@Override
	public void onStringSelectInteraction(StringSelectInteractionEvent event) {

		if (event.getComponentId().equals("logging-choose-category")) {
			int idRange = Integer.parseInt(event.getValues().get(0).replace("logging-catid-", ""));
			String catname = LoggingOptions.byId(idRange).toString();

			List<Integer> catids = Arrays.asList(LoggingOptions.values()).stream()
					.filter(opt -> (opt.getId() > idRange && opt.getId() < idRange + 10)).map(opt -> opt.getId())
					.toList();

			InteractionHook hook = event.deferEdit().complete();

			EmbedBuilder embbuild = EmbedUtils.getDefault();
			embbuild.setColor(Color.blue);
			embbuild.setTitle("LoggingConfig - " + catname);

			StringBuilder strbuild = new StringBuilder();
			strbuild.append("Option - State");
			strbuild.append("\n\n");

			for (int catid : catids) {

				strbuild.append(LoggingOptions.byId(catid).toString());

				// TODO implement State
				strbuild.append(" - STATE");

				strbuild.append("\n");

			}

			embbuild.setDescription(strbuild);

			List<ItemComponent> buttonRow = new LinkedList<>();

			buttonRow.add(Button.success("logging-cat-enableall" + catname, "Enable All"));
			buttonRow.add(Button.danger("logging-cat-disableall" + catname, "Disable All"));

			StringSelectMenu.Builder strSelectBuilder = StringSelectMenu.create("logging-single-select");

			for (int catid : catids) {

				strSelectBuilder.addOption(LoggingOptions.byId(catid).toString(), "logging-single-toggle-" + catid);
			}

			List<LayoutComponent> rows = new LinkedList<>();
			rows.add(ActionRow.of(buttonRow));
			rows.add(ActionRow.of(strSelectBuilder.build()));

			hook.editOriginalEmbeds(embbuild.build()).setComponents(rows).queue();

		}

		Klassenserver7bbot.getInstance().getShardManager().removeEventListener(this);
	}

}
