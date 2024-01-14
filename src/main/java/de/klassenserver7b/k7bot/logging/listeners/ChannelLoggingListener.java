
package de.klassenserver7b.k7bot.logging.listeners;

import javax.annotation.Nonnull;

import de.klassenserver7b.k7bot.Klassenserver7bbot;
import de.klassenserver7b.k7bot.util.EmbedUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.Channel;
import net.dv8tion.jda.api.entities.channel.middleman.GuildMessageChannel;
import net.dv8tion.jda.api.events.channel.ChannelCreateEvent;
import net.dv8tion.jda.api.events.channel.ChannelDeleteEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class ChannelLoggingListener extends ListenerAdapter {

	@Override
	public void onChannelCreate(@Nonnull ChannelCreateEvent event) {
		Klassenserver7bbot.getInstance().getMainLogger().debug("ChannelCreateEvent");
		if (!Klassenserver7bbot.getInstance().isEventBlocked()) {
			Channel channel = event.getChannel();
			Guild guild = event.getGuild();
			GuildMessageChannel system = Klassenserver7bbot.getInstance().getSysChannelMgr().getSysChannel(guild);

			EmbedBuilder builder = EmbedUtils.getSuccessEmbed(
					"**Channel: **\n #" + channel.getName() + "\n\n **Type: **\n" + channel.getType().toString()
							+ "\n\n **ChannelId: ** \n" + channel.getIdLong(),
					event.getGuild().getIdLong());

			builder.setTitle("Channel created: " + channel.getName());
			system.sendMessageEmbeds(builder.build()).queue();
		}
	}

	@Override
	public void onChannelDelete(@Nonnull ChannelDeleteEvent event) {
		Klassenserver7bbot.getInstance().getMainLogger().debug("ChannelRemoveEvent");
		if (!Klassenserver7bbot.getInstance().isEventBlocked()) {
			Channel channel = event.getChannel();
			Guild guild = event.getGuild();
			GuildMessageChannel system = Klassenserver7bbot.getInstance().getSysChannelMgr().getSysChannel(guild);

			EmbedBuilder builder = EmbedUtils.getErrorEmbed(
					"**Channel: **\n #" + channel.getName() + "\n\n **ChannelId: ** \n" + channel.getIdLong(),
					event.getGuild().getIdLong());
			builder.setTitle("Channel deleted: " + channel.getName());
			system.sendMessageEmbeds(builder.build()).queue();
		}
	}
}
