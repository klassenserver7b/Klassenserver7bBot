/*    */ package de.k7bot.hypixel.commands;
/*    */ 
/*    */ import de.k7bot.commands.types.HypixelCommand;
/*    */ import net.dv8tion.jda.api.entities.Member;
/*    */ import net.dv8tion.jda.api.entities.Message;
/*    */ import net.dv8tion.jda.api.entities.TextChannel;
/*    */ 
/*    */ public class TestCommand
/*    */   implements HypixelCommand
/*    */ {
/*    */   public void performHypixelCommand(Member m, TextChannel channel, Message message) {
/* 12 */     message.delete().queue();
/*    */   }
/*    */ }


/* Location:              D:\Felix\Desktop\Bot\Bot.jar!\de\k7bot\hypixel\commands\TestCommand.class
 * Java compiler version: 15 (59.0)
 * JD-Core Version:       1.1.3
 */