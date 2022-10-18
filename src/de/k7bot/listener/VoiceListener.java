
package de.k7bot.listener;

import de.k7bot.Klassenserver7bbot;
import de.k7bot.sql.LiteSQL;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.Category;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;
import net.dv8tion.jda.api.entities.channel.unions.AudioChannelUnion;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceUpdateEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class VoiceListener extends ListenerAdapter {
	public List<Long> tempchannels = new ArrayList<>();

	@Override
	public void onGuildVoiceUpdate(GuildVoiceUpdateEvent event) {

		AudioChannelUnion oldchan = event.getChannelLeft();
		AudioChannelUnion newchan = event.getChannelJoined();

		if (oldchan == null) {
			onJoin(newchan, event.getMember());
			return;
		}
		if (newchan == null) {
			onLeave(oldchan);
			return;
		}

		onLeave(oldchan);
		onJoin(newchan, event.getMember());

	}

	public void onJoin(AudioChannel audioChannel, Member member) {
		if (audioChannel.getIdLong() == 841212695259775027L) {
			VoiceChannel voice = (VoiceChannel) audioChannel;
			Category cat = voice.getParentCategory();
			VoiceChannel vc = cat.createVoiceChannel(member.getEffectiveName() + "s Voicechannel").complete();
			vc.getManager().setUserLimit(voice.getUserLimit()).queue();
			Guild controller = vc.getGuild();
			controller.moveVoiceMember(member, vc).queue();
			LiteSQL.onUpdate("INSERT INTO createdprivatevcs(channelId) VALUES(?);", vc.getIdLong());
			Klassenserver7bbot.INSTANCE.getMainLogger().info("Created custom VoiceChannel for Member: "
					+ member.getEffectiveName() + " with the following Channel-ID: " + vc.getIdLong());
		}
	}

	public void onLeave(AudioChannel audioChannel) {
		if (audioChannel.getMembers().size() <= 0) {

			ResultSet set = LiteSQL.onQuery("SELECT channelId FROM createdprivatevcs;");

			try {
				while (set.next()) {
					this.tempchannels.add(set.getLong("channelId"));
				}
				if (this.tempchannels.contains(audioChannel.getIdLong())) {
					audioChannel.delete().queue();
					LiteSQL.onUpdate("DELETE FROM createdprivatevcs WHERE channelId = ?;", audioChannel.getIdLong());
					this.tempchannels.clear();
					Klassenserver7bbot.INSTANCE.getMainLogger().info("Removed custom VoiceChannel with the Name: "
							+ audioChannel.getName() + " and the following ID: " + audioChannel.getIdLong());
				}

			} catch (SQLException e) {

				e.printStackTrace();
			}
		}
	}
}
