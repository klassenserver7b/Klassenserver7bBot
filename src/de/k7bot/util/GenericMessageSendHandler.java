/**
 *
 */
package de.k7bot.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

import javax.annotation.Nonnull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.requests.FluentRestAction;
import net.dv8tion.jda.api.utils.FileUpload;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;

/**
 * @author K7
 *
 */
public class GenericMessageSendHandler {

	private final InteractionHook hook;
	private final TextChannel channel;

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	private static final int HookId = 1;
	private static final int ChannelId = 2;
	private final int selectedid;

	/**
	 *
	 * @param hook
	 */
	public GenericMessageSendHandler(@Nonnull InteractionHook hook) {
		Objects.requireNonNull(hook, "@Nonnull required parameter is null: Textchannel");
		this.hook = hook;
		this.channel = null;
		selectedid = HookId;
	}

	/**
	 *
	 * @param channel
	 */
	public GenericMessageSendHandler(@Nonnull TextChannel channel) {
		Objects.requireNonNull(channel, "@Nonnull required parameter is null: Textchannel");
		this.channel = channel;
		this.hook = null;
		selectedid = ChannelId;
	}

	public FluentRestAction<Message, ?> sendMessage(@Nonnull CharSequence data) {
		try (MessageCreateData messdata = new MessageCreateBuilder().addContent(data.toString()).build()) {
			return sendMessage(messdata);
		}
	}

	public FluentRestAction<Message, ?> sendMessage(@Nonnull MessageCreateData data) {
		try {
			switch (selectedid) {
			case HookId -> {
				return hook.sendMessage(data);
			}
			case ChannelId -> {
				return channel.sendMessage(data);
			}
			}
		}
		catch (NullPointerException e) {
			onNPE(e);
		}
		return null;
	}

	public FluentRestAction<Message, ?> sendMessageEmbeds(@Nonnull MessageEmbed embed) {
		List<MessageEmbed> embedlist = new ArrayList<>();
		embedlist.add(embed);
		return sendMessageEmbeds(embedlist);
	}

	public FluentRestAction<Message, ?> sendMessageEmbeds(@Nonnull MessageEmbed... embeds) {
		List<MessageEmbed> embedlist = Arrays.asList(embeds);
		return sendMessageEmbeds(embedlist);
	}

	public FluentRestAction<Message, ?> sendMessageEmbeds(@Nonnull Collection<? extends MessageEmbed> embeds) {
		try {
			switch (selectedid) {
			case HookId -> {
				return hook.sendMessageEmbeds(embeds);
			}
			case ChannelId -> {
				return channel.sendMessageEmbeds(embeds);
			}
			}
		}
		catch (NullPointerException e) {
			onNPE(e);
		}
		return null;
	}

	public FluentRestAction<Message, ?> sendFiles(@Nonnull FileUpload file, @Nonnull FileUpload... files) {
		List<FileUpload> list = Arrays.asList(files);
		list.add(0, file);
		return sendFiles(list);
	}

	public FluentRestAction<Message, ?> sendFiles(@Nonnull Collection<? extends FileUpload> files) {
		try {
			switch (selectedid) {
			case HookId -> {
				return hook.sendFiles(files);
			}
			case ChannelId -> {
				return channel.sendFiles(files);
			}
			}
		}
		catch (NullPointerException e) {
			onNPE(e);
		}
		return null;
	}

	public FluentRestAction<Message, ?> sendMessageFormat(@Nonnull String format, @Nonnull Object... objects) {
		try {
			switch (selectedid) {
			case HookId -> {
				return hook.sendMessageFormat(format, objects);
			}
			case ChannelId -> {
				return channel.sendMessageFormat(format, objects);
			}
			}
		}
		catch (NullPointerException e) {
			onNPE(e);
		}
		return null;
	}

	public void sendTyping() {

		switch (selectedid) {
		case ChannelId -> {
			channel.sendTyping().queue();
		}
		}
		return;
	}

	public void onNPE(NullPointerException e) {
		log.error(e.getMessage(), e);
	}

	public Class<?> getSelectedClass() {
		switch (selectedid) {
		case HookId -> {
			return hook.getClass();
		}
		case ChannelId -> {
			return channel.getClass();
		}
		default -> {
			return null;
		}
		}
	}

}
