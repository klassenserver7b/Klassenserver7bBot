/**
 * 
 */
package de.k7bot.commands.slash;

import java.awt.Color;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

import de.k7bot.Klassenserver7bbot;
import de.k7bot.commands.types.TopLevelSlashCommand;
import de.k7bot.util.GenericMessageSendHandler;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

/**
 * @author felix
 *
 */
public class VotingCommand implements TopLevelSlashCommand {

	private static final String[] numbers = new String[] { "", "1️⃣", "2️⃣", "3️⃣", "4️⃣", "5️⃣", "6️⃣", "7️⃣", "8️⃣",
			"9️⃣" };

	/**
	 * 
	 */
	public VotingCommand() {
	}

	@Override
	public void performSlashCommand(SlashCommandInteraction event) {

		if (!(event.getUser().getIdLong() == 675828196389683223l || event.getUser().getIdLong() == 672514862101954570l
				|| event.getUser().getIdLong() == Klassenserver7bbot.getInstance().getOwnerId())) {
			event.replyEmbeds(
					new EmbedBuilder().setColor(Color.red).setDescription("You are not allowed to do this").build())
					.setEphemeral(true).queue();
			return;
		}
		LinkedHashMap<String, User> names = new LinkedHashMap<>();

		for (int i = 0; i < 6; i++) {
			OptionMapping mapping = event.getOption("user" + i);
			if (mapping != null) {
				names.put(mapping.getAsMember().getEffectiveName(), mapping.getAsUser());
			}

		}

		EmbedBuilder builder = new EmbedBuilder();
		builder.setTitle("Wer ist der dümmste?");
		builder.setColor(Color.cyan);
		builder.setFooter("GameMaster: " + event.getUser().getName());
		builder.setTimestamp(OffsetDateTime.now());

		StringBuilder strbuilder = new StringBuilder();
		for (int i = 1; i <= names.size(); i++) {
			strbuilder.append(i);
			strbuilder.append(" - ");
			strbuilder.append(names.keySet().stream().toList().get(i - 1));
			strbuilder.append("\n");
		}

		builder.setDescription(strbuilder.toString());

		InteractionHook hook = event.deferReply(false).complete();

		Message mess = hook.sendMessageEmbeds(builder.build()).complete();

		for (int i = 1; i <= names.size(); i++) {
			mess.addReaction(Emoji.fromFormatted(numbers[i])).queue();
		}

		event.getUser().openPrivateChannel().complete().sendMessageEmbeds(builder.build()).queue();

		Klassenserver7bbot.getInstance().getShardManager()
				.addEventListener(new VoteReactionListener(mess.getIdLong(), event.getUser(), names,
						new GenericMessageSendHandler(hook)));

	}

	@Override
	public SlashCommandData getCommandData() {

		ArrayList<OptionData> options = new ArrayList<>();

		for (int i = 0; i < 6; i++) {
			options.add(new OptionData(OptionType.USER, "user" + i, "an user"));
		}

		return Commands.slash("voting", "starts a voting").addOptions(options);
	}

}

class VoteReactionListener extends ListenerAdapter {

	private Long messageId;
	private User gameMaster;
	private LinkedHashMap<String, User> users;
	private HashMap<User, String> votings;
	private GenericMessageSendHandler channel;

	public VoteReactionListener(Long messageId, User gameMaster, LinkedHashMap<String, User> users,
			GenericMessageSendHandler channel) {
		this.messageId = messageId;
		this.users = users;
		this.gameMaster = gameMaster;
		this.channel = channel;
		votings = new HashMap<>();
	}

	@Override
	public void onMessageReactionAdd(MessageReactionAddEvent event) {

		if (event.getMessageIdLong() != messageId) {
			return;
		}

		User u = event.getUser();

		if (event.getGuild().getSelfMember().getUser().getIdLong() == u.getIdLong()) {
			return;
		}

		if (!votings.containsKey(u) && users.values().contains(u)) {
			votings.put(u, event.getEmoji().getName());
		}

		event.getReaction().removeReaction(u).queue();

		if (votings.size() >= users.size()) {

			StringBuilder build = new StringBuilder();

			votings.forEach((user, emojiname) -> {

				build.append(user.getName());
				build.append(" - ");
				build.append(emojiname);
				build.append("\n");

			});

			channel.sendMessage("Voting beendet!");
			gameMaster.openPrivateChannel().complete()
					.sendMessageEmbeds(new EmbedBuilder().setDescription(build.toString()).build()).queue();

			Klassenserver7bbot.getInstance().getShardManager().removeEventListener(this);

		}

	}

}
