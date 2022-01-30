/*    */
package de.k7bot.hypixel.commands;

/*    */
/*    */ import de.k7bot.Klassenserver7bbot;
/*    */ import de.k7bot.commands.types.HypixelCommand;
/*    */ import de.k7bot.manage.SyntaxError;

import java.io.IOException;
/*    */ import java.util.UUID;
/*    */ import java.util.concurrent.ExecutionException;
/*    */ import java.util.concurrent.TimeUnit;
/*    */ import me.kbrewster.exceptions.APIException;
import me.kbrewster.exceptions.InvalidPlayerException;
/*    */ import me.kbrewster.mojangapi.MojangAPI;
/*    */ import net.dv8tion.jda.api.entities.Member;
/*    */ import net.dv8tion.jda.api.entities.Message;
/*    */ import net.dv8tion.jda.api.entities.TextChannel;
/*    */ import net.hypixel.api.HypixelAPI;
/*    */ import net.hypixel.api.reply.PlayerReply;

/*    */
/*    */
/*    */
/*    */ public class KarmaCommand/*    */ implements HypixelCommand
/*    */ {
	/*    */ public void performHypixelCommand(Member m, TextChannel channel, Message message) {
		/* 23 */ message.delete().queue();
		/*    */
		/* 25 */ HypixelAPI api = Klassenserver7bbot.INSTANCE.API;
		/*    */
		/* 27 */ UUID id = null;
		/*    */
		/* 29 */ String[] args = message.getContentDisplay().split(" ");
		/*    */
		/* 31 */ if (args.length >= 3) {
			/*    */ String name;
			/* 33 */ if (args.length > 3) {
				/*    */
				/* 35 */ StringBuilder builder = new StringBuilder();
				/*    */
				/* 37 */ for (int i = 2; i <= args.length; i++) {
					/* 38 */ builder.append(" " + args[i]);
					/*    */ }
				/*    */
				/* 41 */ name = builder.toString().trim();
				/*    */ }
			/*    */ else {
				/*    */
				/* 45 */ name = args[2];
				/*    */ }
			/*    */
			/*    */
			/*    */
			/*    */ try {
				/* 51 */ id = MojangAPI.getUUID(name);
				/*    */ }
			/* 53 */ catch (APIException | InvalidPlayerException | IOException e1) {
				/*    */
				/* 55 */ e1.printStackTrace();
				/*    */ }
			/*    */
			/*    */
			/* 59 */ if (id != null) {
				/* 60 */ channel.sendTyping().queue();
				/*    */ try {
					/* 62 */ channel
							.sendMessage(String.valueOf(name) + " has "
									+ ((PlayerReply) api.getPlayerByUuid(id).get()).getPlayer().getKarma() + " Karma.")
							.queue();
					/*    */ }
				/* 64 */ catch (ExecutionException e) {
					/* 65 */ System.err.println("Oh no, our API request failed!");
					/* 66 */ e.getCause().printStackTrace();
					/*    */ }
				/* 68 */ catch (InterruptedException e) {
					/*    */
					/* 70 */ System.err.println("Oh no, the player fetch thread was interrupted!");
					/* 71 */ e.printStackTrace();
					/*    */ }
				/*    */ } else {
				/* 74 */ ((Message) channel
						.sendMessage(String.valueOf(name) + " is not a valid username " + m.getAsMention()).complete())
								.delete()/* 75 */ .queueAfter(10L, TimeUnit.SECONDS);
				/*    */ }
			/*    */
			/*    */ }
		/*    */ else {
			/*    */
			/* 81 */ SyntaxError.oncmdSyntaxError(channel, "hypixel friends [playername]", m);
			/*    */ }
		/*    */ }
	/*    */ }

/*
 * Location:
 * D:\Felix\Desktop\Bot\Bot.jar!\de\k7bot\hypixel\commands\KarmaCommand.class
 * Java compiler version: 15 (59.0) JD-Core Version: 1.1.3
 */