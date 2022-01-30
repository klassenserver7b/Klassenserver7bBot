/*    */ package de.k7bot.hypixel.commands;
/*    */ 
/*    */ import de.k7bot.Klassenserver7bbot;
/*    */ import de.k7bot.commands.types.HypixelCommand;
/*    */ import java.util.concurrent.ExecutionException;
/*    */ import net.dv8tion.jda.api.entities.Member;
/*    */ import net.dv8tion.jda.api.entities.Message;
/*    */ import net.dv8tion.jda.api.entities.TextChannel;
/*    */ import net.hypixel.api.HypixelAPI;
/*    */ import net.hypixel.api.reply.CountsReply;
/*    */ 
/*    */ 
/*    */ public class PlayerCountCommand
/*    */   implements HypixelCommand
/*    */ {
/*    */   public void performHypixelCommand(Member m, TextChannel channel, Message message) {
/* 17 */     message.delete().queue();
/*    */ 
/*    */     
/* 20 */     HypixelAPI api = Klassenserver7bbot.INSTANCE.API;
/*    */     
/*    */     try {
/* 23 */       CountsReply gescount = api.getCounts().get();
/*    */       
/* 25 */       channel.sendMessage("Auf Hypixel spielen aktuell " + gescount.getPlayerCount() + " Spieler.").queue();
/*    */     }
/* 27 */     catch (ExecutionException e) {
/* 28 */       System.err.println("Oh no, our API request failed!");
/* 29 */       e.getCause().printStackTrace();
/*    */     }
/* 31 */     catch (InterruptedException e) {
/*    */       
/* 33 */       System.err.println("Oh no, the player fetch thread was interrupted!");
/* 34 */       e.printStackTrace();
/*    */     } 
/*    */   }
/*    */ }


/* Location:              D:\Felix\Desktop\Bot\Bot.jar!\de\k7bot\hypixel\commands\PlayerCountCommand.class
 * Java compiler version: 15 (59.0)
 * JD-Core Version:       1.1.3
 */