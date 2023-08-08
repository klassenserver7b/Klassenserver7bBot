/**
 * 
 */
package de.klassenserver7b.k7bot.util;

import java.awt.Color;
import java.time.OffsetDateTime;

import de.klassenserver7b.k7bot.Klassenserver7bbot;
import net.dv8tion.jda.api.EmbedBuilder;

/**
 * @author K7
 */
public class EmbedUtils {

	/**
	 * 
	 */
	public EmbedUtils() {

	}

	public static EmbedBuilder getErrorEmbed(CharSequence description) {
		return getErrorEmbed(description, null);
	}

	public static EmbedBuilder getErrorEmbed(CharSequence description, Long guildId) {
		return getBuilderOf(Color.red, description, guildId);
	}

	public static EmbedBuilder getSuccessEmbed(CharSequence description) {
		return getSuccessEmbed(description, null);
	}

	public static EmbedBuilder getSuccessEmbed(CharSequence description, Long guildId) {
		return getBuilderOf(Color.green, description, guildId);
	}

	public static EmbedBuilder getBuilderOf(Color c) {
		return getDefault().setColor(c);
	}

	public static EmbedBuilder getBuilderOf(CharSequence description) {
		return getBuilderOf(description, null);
	}

	public static EmbedBuilder getBuilderOf(Color c, Long guildId) {
		return getDefault(guildId).setColor(c);
	}

	public static EmbedBuilder getBuilderOf(CharSequence description, Long guildId) {
		return getDefault(guildId).appendDescription(description);
	}

	public static EmbedBuilder getBuilderOf(Color c, CharSequence description) {
		return getBuilderOf(c).appendDescription(description);
	}

	public static EmbedBuilder getBuilderOf(Color c, CharSequence description, Long guildId) {
		return getBuilderOf(c, guildId).appendDescription(description);
	}

	public static EmbedBuilder getDefault() {
		return getDefault(null);
	}

	public static EmbedBuilder getDefault(Long guildId) {
		return new EmbedBuilder().setTimestamp(OffsetDateTime.now())
				.setFooter("@" + Klassenserver7bbot.getInstance().getSelfName(guildId));
	}

}
