/*    */ package de.k7bot.commands;
/*    */ 
/*    */ import de.k7bot.commands.types.ServerCommand;
/*    */ import net.dv8tion.jda.api.entities.Member;
/*    */ import net.dv8tion.jda.api.entities.Message;
/*    */ import net.dv8tion.jda.api.entities.TextChannel;
/*    */ 
/*    */ public class EveryoneCommand
/*    */   implements ServerCommand
/*    */ {
/*    */   public void performCommand(Member m, TextChannel channel, Message message) {
/* 12 */     message.delete().queue();
/* 13 */     String[] args = message.getContentDisplay().split(" ");
/* 14 */     StringBuilder builder = new StringBuilder();
/*    */     
/* 16 */     for (int i = 1; i < args.length; i++) {
/* 17 */       builder.append(" " + args[i]);
/*    */     }
/*    */     
/* 20 */     channel.sendMessage(String.valueOf(channel.getGuild().getPublicRole().getAsMention()) + " " + builder.toString().trim())
/* 21 */       .queue();
/*    */   }
/*    */ }


/* Location:              D:\Felix\Desktop\Bot\Bot.jar!\de\k7bot\commands\EveryoneCommand.class
 * Java compiler version: 15 (59.0)
 * JD-Core Version:       1.1.3
 */