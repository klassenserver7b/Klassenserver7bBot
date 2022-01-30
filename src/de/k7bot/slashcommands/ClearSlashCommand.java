/*    */
package de.k7bot.slashcommands;

/*    */
/*    */ import de.k7bot.commands.ClearCommand;
/*    */ import de.k7bot.commands.types.SlashCommand;
/*    */ import de.k7bot.manage.PermissionError;
/*    */ import net.dv8tion.jda.api.Permission;
/*    */ import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
/*    */ import net.dv8tion.jda.api.interactions.commands.OptionMapping;

/*    */
/*    */
/*    */ public class ClearSlashCommand/*    */ implements SlashCommand
/*    */ {
	/*    */ public void performSlashCommand(SlashCommandEvent event) {
		/* 15 */ if (event.getMember().hasPermission(new Permission[] { Permission.MESSAGE_MANAGE })) {
			/* 16 */
			/*    */
			/* 18 */ OptionMapping amountOption = event.getOption("amount");

			event.deferReply(true);
			/* 19 */ if (amountOption != null) {
				/* 20 */ int amount = (int) amountOption.getAsLong();
				/* 21 */ if (amount == 0) {
					/* 22 */ amount = 1;
					/*    */ }

				/* 24 */ else if (amount > 200) {
					/* 25 */ event.reply(
							/* 26 */ "Aufgrund von Zugriffslimitierungen, kann ich nicht mehr als 200 Nachrichten löschen!")
							/* 27 */ .queue();
					/*    */ } else {
					/*    */
					/* 30 */ ClearCommand.onclear(amount, event.getTextChannel(), event.getMember());
					/*    */ }
				/*    */
				/* 34 */ event.reply(String.valueOf(amount) + " messages deleted.");
				/*    */ } else {
				/*    */
				/* 37 */ event.reply("please submit an amount of messages to purge");
				/*    */ }
			/*    */ } else {
			/*    */
			/* 41 */ PermissionError.onPermissionError(event.getMember(), event.getTextChannel());
			/*    */ }
		/*    */ }
	/*    */ }

/*
 * Location:
 * D:\Felix\Desktop\Bot\Bot.jar!\de\k7bot\slashcommands\ClearSlashCommand.class
 * Java compiler version: 15 (59.0) JD-Core Version: 1.1.3
 */