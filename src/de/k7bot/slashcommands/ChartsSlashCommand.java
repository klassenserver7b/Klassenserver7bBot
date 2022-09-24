package de.k7bot.slashcommands;

import java.awt.Color;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.Map.Entry;

import de.k7bot.commands.types.SlashCommand;
import de.k7bot.music.ChartList;
import de.k7bot.util.Cell;
import de.k7bot.util.TableMessage;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;

public class ChartsSlashCommand implements SlashCommand {

	@Override
	public void performSlashCommand(SlashCommandInteraction event) {
		InteractionHook hook = event.deferReply(false).complete();
		OptionMapping timeopt = event.getOption("time");
		OptionMapping timeunitopt = event.getOption("timeunit");
		OptionMapping guildopt = event.getOption("guild");

		HashMap<String, Long> charts = provideOptionselectedCharts(timeopt, timeunitopt, guildopt, event.getGuild(),
				hook);

		if (charts != null) {
			if (charts.isEmpty()) {
				hook.sendMessage(
						"There are no Charts for the selected Options! (or something went wrong but who could think this 😅)")
						.queue();
			} else {

				sendMessage(hook, charts);

			}
		}

	}

	private HashMap<String, Long> provideOptionselectedCharts(OptionMapping timeopt, OptionMapping timeunitopt,
			OptionMapping guildopt, Guild guild, InteractionHook hook) {

		HashMap<String, Long> sheduledcharts = new HashMap<>();
		ChartList chartlist = new ChartList();

		if (guildopt != null && guildopt.getAsBoolean()) {

			if (timeopt != null) {

				if (timeunitopt != null) {

					Long time = timeopt.getAsLong();

					switch (timeunitopt.getAsString().toLowerCase()) {
					case "days": {
						sheduledcharts = chartlist.getcharts(guild, time, ChronoUnit.DAYS);
						break;
					}
					case "months": {
						sheduledcharts = chartlist.getcharts(guild, time, ChronoUnit.MONTHS);
						break;
					}
					case "years": {
						sheduledcharts = chartlist.getcharts(guild, time, ChronoUnit.YEARS);
						break;
					}
					default:
						hook.sendMessage("Using the option \"time\" requires a valid TimeUnit").queue();
						break;
					}
				}

			} else {
				sheduledcharts = chartlist.getcharts(guild);
			}

		} else {

			if (timeopt != null) {

				if (timeunitopt != null) {

					Long time = timeopt.getAsLong();
					
					switch (timeunitopt.getAsString().toLowerCase()) {
					case "days": {
						sheduledcharts = chartlist.getcharts(time, ChronoUnit.DAYS);
						break;
					}
					case "months": {
						sheduledcharts = chartlist.getcharts(time, ChronoUnit.MONTHS);
						break;
					}
					case "years": {
						sheduledcharts = chartlist.getcharts(time, ChronoUnit.YEARS);
						break;
					}
					default:
						hook.sendMessage("Using the option \"time\" requires a valid TimeUnit").queue();
						break;
					}
				}
			} else {

				sheduledcharts = chartlist.getcharts();

			}

		}

		return sheduledcharts;

	}

	private void sendMessage(InteractionHook hook, HashMap<String, Long> charts) {

		List<Entry<String, Long>> orderedcharts = new ArrayList<>(charts.entrySet());
		orderedcharts.sort(Map.Entry.comparingByValue());

		TableMessage table = new TableMessage();
		TableMessage countingtable = new TableMessage();

		table.addHeadline("Songname", "Author", "Times played");
		countingtable.addHeadline("Songname", "Author", "Times played");

		Set<String> keys = charts.keySet();
		TreeMap<Long, Set<String>> treeMap = new TreeMap<>();
		for (String key : keys) {
			Long value = charts.get(key);
			Set<String> values;
			if (treeMap.containsKey(value)) {
				values = treeMap.get(value);
				values.add(key);
			} else {
				values = new HashSet<>();
				values.add(key);
			}

			treeMap.put(value, values);
		}
		Set<Long> treeValues = treeMap.keySet();

		List<Long> reverseKeys = new LinkedList<>(treeValues);
		Collections.reverse(reverseKeys);
		
		for (Long Long : reverseKeys) {
			Set<String> values = treeMap.get(Long);

			for (String title : values) {

				String[] titleparts = title.split("%%SPLITTER%%");

				String songauthor;
				String songname;

				if (titleparts.length > 1) {
					songname = titleparts[1];
					songauthor = titleparts[0];
				} else {
					songname = titleparts[0];
					songauthor = "";
				}

				if (songauthor.equalsIgnoreCase("")) {

					String[] split = songname.split(" - ");
					songname = split[1];
					songauthor = split[0];

				}

				if (!(countingtable.addRow(Cell.of(songname), Cell.of(songauthor), Cell.of(String.valueOf(Long)))
						.build().length() >= 4096)) {

					countingtable.addRow(Cell.of(songname), Cell.of(songauthor), Cell.of(String.valueOf(Long)));
					table.addRow(Cell.of(songname), Cell.of(songauthor), Cell.of(String.valueOf(Long)));

				} else {

					break;

				}

			}
		}
		sendEmbed(hook, table.build(), Color.decode("#2ff538"));
	}

	public void sendEmbed(InteractionHook hook, String formatedcharts, Color col) {

		hook.setEphemeral(false);
		EmbedBuilder builder = new EmbedBuilder();
		builder.setFooter("requested by slashcommand");
		builder.setTimestamp(OffsetDateTime.now());
		builder.setTitle("Your requested Charts");

		if (col != null) {
			builder.setColor(col);
		} else {
			builder.setColor(Color.decode("#2c82c9"));
		}

		builder.setDescription(formatedcharts);

		hook.sendMessageEmbeds(builder.build()).queue();

	}

}
