/*    */ package de.k7bot.manage;
/*    */ 
/*    */ import java.util.concurrent.TimeUnit;
/*    */ import net.dv8tion.jda.api.entities.Member;
/*    */ import net.dv8tion.jda.api.entities.Message;
/*    */ import net.dv8tion.jda.api.entities.TextChannel;
/*    */ 
/*    */ public class PermissionError
/*    */ {
/*    */   public static void onPermissionError(Member m, TextChannel channel) {
/* 11 */     ((Message)channel.sendMessage("You don't have the permission to do this!" + m.getAsMention()).complete()).delete()
/* 12 */       .queueAfter(10L, TimeUnit.SECONDS);
/*    */   }
/*    */ }


/* Location:              D:\Felix\Desktop\Bot\Bot.jar!\de\k7bot\manage\PermissionError.class
 * Java compiler version: 15 (59.0)
 * JD-Core Version:       1.1.3
 */