/*    */ package de.k7bot.hypixel.commands;
/*    */ 
/*    */ import de.k7bot.Klassenserver7bbot;
/*    */ import de.k7bot.commands.types.HypixelCommand;
/*    */ import java.util.concurrent.ExecutionException;
/*    */ import net.dv8tion.jda.api.entities.Member;
/*    */ import net.dv8tion.jda.api.entities.Message;
/*    */ import net.dv8tion.jda.api.entities.TextChannel;
/*    */ import net.hypixel.api.HypixelAPI;
/*    */ import net.hypixel.api.reply.PunishmentStatsReply;
/*    */ 
/*    */ 
/*    */ public class WatchdogCommand
/*    */   implements HypixelCommand
/*    */ {
/*    */   public void performHypixelCommand(Member m, TextChannel channel, Message message) {
/* 17 */     message.delete().queue();
/* 18 */     HypixelAPI api = Klassenserver7bbot.INSTANCE.API;
/*    */ 
/*    */ 
/*    */     
/*    */     try {
/* 23 */       PunishmentStatsReply reply = api.getPunishmentStats().get();
/* 24 */       channel.sendMessage(
/* 25 */           "Staff-Rolling-Daily: " + reply.getStaffRollingDaily() + "\nStaff-Total: " + reply.getStaffTotal() + 
/* 26 */           "\nWatchdog-Last-Minute: " + reply.getWatchdogLastMinute() + "\nWatchdog-Rolling-Daily: " + 
/* 27 */           reply.getWatchdogRollingDaily() + "\nWatchdog Total: " + reply.getWatchdogTotal())
/* 28 */         .queue();
/*    */     }
/* 30 */     catch (ExecutionException e) {
/* 31 */       System.err.println("Oh no, our API request failed!");
/* 32 */       e.getCause().printStackTrace();
/*    */     }
/* 34 */     catch (InterruptedException e) {
/*    */       
/* 36 */       System.err.println("Oh no, the player fetch thread was interrupted!");
/* 37 */       e.printStackTrace();
/*    */     } 
/*    */   }
/*    */ }


/* Location:              D:\Felix\Desktop\Bot\Bot.jar!\de\k7bot\hypixel\commands\WatchdogCommand.class
 * Java compiler version: 15 (59.0)
 * JD-Core Version:       1.1.3
 */