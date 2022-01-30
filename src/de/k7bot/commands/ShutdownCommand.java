/*    */ package de.k7bot.commands;
/*    */ 
/*    */ import de.k7bot.Klassenserver7bbot;
/*    */ import de.k7bot.commands.types.ServerCommand;
/*    */ import de.k7bot.manage.PermissionError;
/*    */ import net.dv8tion.jda.api.Permission;
/*    */ import net.dv8tion.jda.api.entities.Member;
/*    */ import net.dv8tion.jda.api.entities.Message;
/*    */ import net.dv8tion.jda.api.entities.TextChannel;
/*    */ 
/*    */ public class ShutdownCommand
/*    */   implements ServerCommand
/*    */ {
/*    */   public void performCommand(Member m, TextChannel channel, Message message) {
/* 15 */     message.delete().queue();
/* 16 */     if (m.hasPermission(new Permission[] { Permission.ADMINISTRATOR })) {
/* 17 */       Klassenserver7bbot.INSTANCE.exit = true;
/* 18 */       Klassenserver7bbot.INSTANCE.onShutdown();
/*    */     } else {
/* 20 */       PermissionError.onPermissionError(m, channel);
/*    */     } 
/*    */   }
/*    */ }


/* Location:              D:\Felix\Desktop\Bot\Bot.jar!\de\k7bot\commands\ShutdownCommand.class
 * Java compiler version: 15 (59.0)
 * JD-Core Version:       1.1.3
 */