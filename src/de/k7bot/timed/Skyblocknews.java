package de.k7bot.timed;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import com.google.gson.JsonArray;

import de.k7bot.Klassenserver7bbot;
import de.k7bot.util.LiteSQL;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import net.hypixel.api.HypixelAPI;

public class Skyblocknews {

	public static void onEventCheck() {
		HypixelAPI api = Klassenserver7bbot.INSTANCE.getHypixelAPI();
		LiteSQL lsql = Klassenserver7bbot.INSTANCE.getDB();

		try {

			JsonArray arr = api.getSkyBlockNews().get().getItems();
			List<String> datesdb = new ArrayList<>();
			List<String> dates = new ArrayList<>();
			List<TextChannel> chans = new ArrayList<>();

			ResultSet set = lsql.onQuery(
					"SELECT hypnewstime.datum, hypixelnewschannels.channelId, hypixelnewschannels.guildId FROM hypnewstime, hypixelnewschannels");

			try {
				while (set.next()) {

					String time = set.getString(1);
					long chan = set.getLong(2);
					long guildId = set.getLong(3);
					Guild guild = Klassenserver7bbot.INSTANCE.shardMan.getGuildById(guildId);

					datesdb.add(time);
					chans.add(guild.getTextChannelById(chan));

				}

				arr.forEach(json -> dates.add(json.getAsJsonObject().get("text").getAsString()));

				dates.forEach(str -> {

					if (!datesdb.contains(str)) {

						chans.forEach(chan -> {

							arr.forEach(json -> {
								if (json.getAsJsonObject().get("text").getAsString().equalsIgnoreCase(str)) {
									chan.sendMessage(json.getAsJsonObject().get("link").getAsString()).queue();
									lsql.onUpdate("INSERT INTO hypnewstime(datum) VALUES('" + str + "')");
								}
							});

						});

					}

				});

			} catch (SQLException e) {
				e.printStackTrace();
			}

		} catch (ExecutionException e) {
			System.err.println("Oh no, our API request failed!");
			e.getCause().printStackTrace();

		} catch (InterruptedException e) {

			System.err.println("Oh no, the news fetch thread was interrupted!");
			e.printStackTrace();

		}
	}
}