/**
 * 
 */
package de.k7bot.music.commands.generic;

import java.awt.Color;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import de.k7bot.util.Cell;
import de.k7bot.util.GenericMessageSendHandler;
import de.k7bot.util.TableMessage;
import net.dv8tion.jda.api.EmbedBuilder;

/**
 * @author felix
 *
 */
public abstract class GenericChartsCommand {

	protected void sendMessage(GenericMessageSendHandler sendHandler, HashMap<String, Long> charts) {

		List<Entry<String, Long>> orderedcharts = new ArrayList<>(charts.entrySet());
		orderedcharts.sort(Map.Entry.comparingByValue());

		TableMessage table = new TableMessage();
		table.automaticLineBreaks(1);
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
		sendEmbed(sendHandler, table.build(), Color.decode("#2ff538"));
	}

	public void sendEmbed(GenericMessageSendHandler sendHandler, String formatedcharts, Color col) {

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

		sendHandler.sendMessageEmbeds(builder.build()).queue();

	}

}
