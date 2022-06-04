package de.k7bot.music.commands;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.awt.Color;

import de.k7bot.commands.types.ServerCommand;
import de.k7bot.music.ChartList;
import de.k7bot.util.Cell;
import de.k7bot.util.TableMessage;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;

public class OverallChartsCommand implements ServerCommand {

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

		ChartList chartlist = new ChartList();
		Map<String, Long> musiclist = chartlist.getcharts();

		List<Entry<String, Long>> orderedcharts = new ArrayList<>(musiclist.entrySet());

		orderedcharts.sort(Map.Entry.comparingByValue());

		TableMessage table = new TableMessage();
		TableMessage countingtable = new TableMessage();

		table.addHeadline("Songname", "Author", "Times played");
		countingtable.addHeadline("Songname", "Author", "Times played");

		Set<String> keys = musiclist.keySet();
		TreeMap<Long, Set<String>> treeMap = new TreeMap<>();
		for (String key : keys) {
			Long value = musiclist.get(key);
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

				String songname = titleparts[0];
				String songauthor;

				if (titleparts.length > 1) {
					songauthor = titleparts[1];
				} else {
					songauthor = "";
				}

				if (songauthor.equalsIgnoreCase("")) {

					String[] split = songname.split(" - ");
					songname = split[0];
					songauthor = split[1];

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
		
		sendEmbed(channel, m, table.build(), Color.decode("#2ff538"));
	}


	public void sendEmbed(TextChannel chan, Member requester, String formatedcharts, Color col) {

		EmbedBuilder builder = new EmbedBuilder();
		builder.setFooter("requested by @" + requester.getEffectiveName());
		builder.setTimestamp(OffsetDateTime.now());
		builder.setTitle("The overall Charts");

		if (col != null) {
			builder.setColor(col);
		} else {
			builder.setColor(Color.decode("#2c82c9"));
		}

		builder.setDescription(formatedcharts);

		chan.sendMessageEmbeds(builder.build()).queue();

	}

}
