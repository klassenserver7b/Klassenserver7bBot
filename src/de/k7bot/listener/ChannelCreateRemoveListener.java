
package de.k7bot.listener;

import java.time.OffsetDateTime;

import javax.annotation.Nonnull;

import de.k7bot.Klassenserver7bbot;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Channel;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.channel.ChannelCreateEvent;
import net.dv8tion.jda.api.events.channel.ChannelDeleteEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class ChannelCreateRemoveListener extends ListenerAdapter {

	@Override
	public void onChannelCreate(@Nonnull ChannelCreateEvent event) {
		Klassenserver7bbot.INSTANCE.getMainLogger().debug("ChannelCreateEvent");
		if (!Klassenserver7bbot.INSTANCE.imShutdown) {
			Channel channel = event.getChannel();
			Guild guild = event.getGuild();
			TextChannel system = guild.getSystemChannel();

			EmbedBuilder builder = new EmbedBuilder();
			builder.setTimestamp(OffsetDateTime.now());
			builder.setFooter(guild.getName());
			builder.setColor(58944);
			builder.setTitle("Channel created: " + channel.getName());
			builder.setDescription("**Channel: **\n #" + channel.getName() + "\n\n **Type: **\n"
					+ channel.getType().toString() + "\n\n **ChannelId: ** \n" + channel.getIdLong());
			system.sendMessageEmbeds(builder.build()).queue();
		}
	}

	@Override
	public void onChannelDelete(@Nonnull ChannelDeleteEvent event) {
		Klassenserver7bbot.INSTANCE.getMainLogger().debug("ChannelRemoveEvent");
		if (!Klassenserver7bbot.INSTANCE.imShutdown) {
			Channel channel = event.getChannel();
			Guild guild = event.getGuild();
			TextChannel system = guild.getSystemChannel();

			EmbedBuilder builder = new EmbedBuilder();
			builder.setTimestamp(OffsetDateTime.now());
			builder.setFooter(guild.getName());
			builder.setColor(13565967);
			builder.setTitle("Channel deleted: " + channel.getName());
			builder.setDescription(
					"**Channel: **\n #" + channel.getName() + "\n\n **ChannelId: ** \n" + channel.getIdLong());
			system.sendMessageEmbeds(builder.build()).queue();
		}
	}
}
