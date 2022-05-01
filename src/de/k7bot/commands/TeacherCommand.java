package de.k7bot.commands;

import java.time.OffsetDateTime;

import com.google.gson.JsonObject;

import de.k7bot.Klassenserver7bbot;
import de.k7bot.commands.types.ServerCommand;
import de.k7bot.util.SyntaxError;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;

public class TeacherCommand implements ServerCommand {

	@Override
	public void performCommand(Member m, TextChannel channel, Message message) {

		String[] args = message.getContentStripped().split(" ");

		if (args.length > 1) {

			EmbedBuilder builder = new EmbedBuilder();
			StringBuilder strbuild = new StringBuilder();

			JsonObject teacher = Klassenserver7bbot.teacherslist.get(args[1]).getAsJsonObject();

			builder.setFooter("requested by @" + m.getEffectiveName());
			builder.setTimestamp(OffsetDateTime.now());

			strbuild.append("**Kürzel**: " + args[1]);
			strbuild.append("\n");
			strbuild.append("**Name: **");

			switch (teacher.get("gender").getAsString()) {
			case "female": {
				strbuild.append("Frau ");
			}
			case "male": {
				strbuild.append("Herr ");
			}
			default:

			}

			if (teacher.get("is_doctor").getAsBoolean()) {

				strbuild.append("Dr. ");

			}

			strbuild.append(teacher.get("full_name").getAsString().replaceAll("\"", ""));

			builder.setDescription(strbuild.toString());

			channel.sendMessageEmbeds(builder.build()).queue();

		} else {

			SyntaxError.oncmdSyntaxError(channel, "teacher [Lehrerkürzel]", m);

		}
	}

	@Override
	public String gethelp() {
		String help = "Zeigt kompletten Namen (inkl. Doktortitel) zum gewählen Lehrer an. \n - z.B. [prefix]teacher [Leherkürzel]";

		return help;
	}

	@Override
	public String getcategory() {
		String category = "Allgemein";
		return category;
	}

}
