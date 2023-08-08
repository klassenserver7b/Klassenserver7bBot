package de.klassenserver7b.k7bot.commands.common;

import com.google.gson.JsonObject;

import de.klassenserver7b.k7bot.HelpCategories;
import de.klassenserver7b.k7bot.Klassenserver7bbot;
import de.klassenserver7b.k7bot.commands.types.ServerCommand;
import de.klassenserver7b.k7bot.util.EmbedUtils;
import de.klassenserver7b.k7bot.util.GenericMessageSendHandler;
import de.klassenserver7b.k7bot.util.errorhandler.SyntaxError;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.middleman.GuildMessageChannel;

public class TeacherCommand implements ServerCommand {

	private boolean isEnabled;

	@Override
	public String gethelp() {
		String help = "Zeigt kompletten Namen (inkl. Anrede) zum gewählten Lehrer an. \n - z.B. [prefix]teacher [Lehrerkürzel]";
		return help;
	}

	@Override
	public String[] getCommandStrings() {
		return new String[] { "teacher" };
	}

	@Override
	public HelpCategories getcategory() {
		return HelpCategories.UNKNOWN;
	}

	@Override
	public void performCommand(Member m, GuildMessageChannel channel, Message message) {

		String[] args = message.getContentStripped().split(" ");

		if (args.length > 1) {

			StringBuilder strbuild = new StringBuilder();

			JsonObject teacher = Klassenserver7bbot.getInstance().getTeacherList().get(args[1]).getAsJsonObject();

			strbuild.append("**Kürzel**: " + args[1]);
			strbuild.append("\n");
			strbuild.append("**Name: **");

			switch (teacher.get("gender").getAsString()) {
			case "female" -> {
				strbuild.append("Frau ");
			}
			case "male" -> {
				strbuild.append("Herr ");
			}
			}

			if (teacher.get("is_doctor").getAsBoolean()) {

				strbuild.append("Dr. ");

			}

			strbuild.append(teacher.get("full_name").getAsString().replaceAll("\"", ""));

			channel.sendMessageEmbeds(EmbedUtils.getBuilderOf(strbuild.toString(), channel.getGuild().getIdLong())
					.setFooter("requested by @" + m.getEffectiveName()).build()).queue();

		} else {

			SyntaxError.oncmdSyntaxError(new GenericMessageSendHandler(channel), "teacher [Lehrerkürzel]", m);

		}
	}

	@Override
	public boolean isEnabled() {
		return isEnabled;
	}

	@Override
	public void disableCommand() {
		isEnabled = false;
	}

	@Override
	public void enableCommand() {
		isEnabled = true;
	}

}
