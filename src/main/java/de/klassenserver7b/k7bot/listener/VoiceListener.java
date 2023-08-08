
package de.klassenserver7b.k7bot.listener;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.klassenserver7b.k7bot.Klassenserver7bbot;
import de.klassenserver7b.k7bot.sql.LiteSQL;
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

	private final Logger log;

	public VoiceListener() {
		log = LoggerFactory.getLogger(this.getClass());
	}

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
			LiteSQL.onUpdate("INSERT INTO createdprivatevcs(guildId, channelId) VALUES(?, ?);",
					vc.getGuild().getIdLong(), vc.getIdLong());
			Klassenserver7bbot.getInstance().getMainLogger().info("Created custom VoiceChannel for Member: "
					+ member.getEffectiveName() + " with the following Channel-ID: " + vc.getIdLong());
		}
	}

	public void onLeave(AudioChannel audioChannel) {
		if (audioChannel.getMembers().size() <= 0) {

			try (ResultSet set = LiteSQL.onQuery("SELECT channelId FROM createdprivatevcs;")) {

				while (set.next()) {
					this.tempchannels.add(set.getLong("channelId"));
				}
				if (this.tempchannels.contains(audioChannel.getIdLong())) {
					audioChannel.delete().queue();
					LiteSQL.onUpdate("DELETE FROM createdprivatevcs WHERE channelId = ? AND guildId=?;",
							audioChannel.getIdLong(), audioChannel.getGuild().getIdLong());
					this.tempchannels.clear();
					Klassenserver7bbot.getInstance().getMainLogger().info("Removed custom VoiceChannel with the Name: "
							+ audioChannel.getName() + " and the following ID: " + audioChannel.getIdLong());
				}

			} catch (SQLException e) {

				log.error(e.getMessage(), e);
			}
		}
	}
}
