/**
 * 
 */
package de.k7bot.moderation.commands.generic;

import java.util.List;

import de.k7bot.util.GenericMessageSendHandler;
import de.k7bot.util.errorhandler.PermissionError;
import de.k7bot.util.errorhandler.SyntaxError;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

/**
 * @author felix
 *
 */
public abstract class GenericUserLogsCommand {

	/**
	 * 
	 * @param m
	 * @param channel
	 * @return
	 */
	protected boolean checkPermissions(Member m, TextChannel channel) {
		if (!m.hasPermission(Permission.KICK_MEMBERS)) {
			PermissionError.onPermissionError(m, channel);
			return false;
		}
		return true;
	}

	/**
	 * 
	 * @param channel
	 * @param message
	 * @param m
	 * @return
	 * @throws IllegalArgumentException
	 */
	protected List<Member> getMemberFromMessage(TextChannel channel, Message message, Member m)
			throws IllegalArgumentException {
		List<Member> memb = message.getMentions().getMembers();

		if (memb.isEmpty()) {
			SyntaxError.oncmdSyntaxError(new GenericMessageSendHandler(channel), "modlogs [@moderator]", m);
			throw new IllegalArgumentException();
		}

		return memb;
	}
	
	

}
