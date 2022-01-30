/*    */ package de.k7bot.hypixel.commands;
/*    */ 
/*    */ import de.k7bot.Klassenserver7bbot;
/*    */ import de.k7bot.commands.types.HypixelCommand;
/*    */ import de.k7bot.manage.SyntaxError;
/*    */ import java.util.UUID;
/*    */ import java.util.concurrent.ExecutionException;
/*    */ import java.util.concurrent.TimeUnit;
/*    */ import me.kbrewster.exceptions.APIException;
/*    */ import me.kbrewster.mojangapi.MojangAPI;
/*    */ import net.dv8tion.jda.api.entities.Member;
/*    */ import net.dv8tion.jda.api.entities.Message;
/*    */ import net.dv8tion.jda.api.entities.TextChannel;
/*    */ import net.hypixel.api.HypixelAPI;
/*    */ import net.hypixel.api.reply.PlayerReply;
/*    */ 
/*    */ 
/*    */ 
/*    */ public class HypixelRankCommand
/*    */   implements HypixelCommand
/*    */ {
/*    */   public void performHypixelCommand(Member m, TextChannel channel, Message message) {
/* 23 */     message.delete().queue();
/*    */     
/* 25 */     HypixelAPI api = Klassenserver7bbot.INSTANCE.API;
/*    */     
/* 27 */     UUID id = null;
/*    */     
/* 29 */     String[] args = message.getContentDisplay().split(" ");
/*    */     
/* 31 */     if (args.length >= 3) {
/*    */       String name;
/* 33 */       if (args.length > 3) {
/*    */         
/* 35 */         StringBuilder builder = new StringBuilder();
/* 36 */         for (int i = 2; i <= args.length; i++) {
/* 37 */           builder.append(" " + args[i]);
/*    */         }
/* 39 */         name = builder.toString().trim();
/*    */       }
/*    */       else {
/*    */         
/* 43 */         name = args[2];
/*    */       } 
/*    */ 
/*    */ 
/*    */       
/*    */       try {
/* 49 */         id = MojangAPI.getUUID(name);
/*    */       }
/* 51 */       catch (APIException|me.kbrewster.exceptions.InvalidPlayerException|java.io.IOException e1) {
/*    */         
/* 53 */         e1.printStackTrace();
/*    */       } 
/*    */ 
/*    */       
/* 57 */       if (id != null) {
/* 58 */         channel.sendTyping().queue();
/*    */         try {
/* 60 */           channel.sendMessage("The highest Rank of " + name + " is: " + (
/* 61 */               (PlayerReply)api.getPlayerByUuid(id).get()).getPlayer().getHighestRank()).queue();
/* 62 */         } catch (ExecutionException e) {
/* 63 */           System.err.println("Oh no, our API request failed!");
/* 64 */           e.getCause().printStackTrace();
/*    */         }
/* 66 */         catch (InterruptedException e) {
/*    */           
/* 68 */           System.err.println("Oh no, the player fetch thread was interrupted!");
/* 69 */           e.printStackTrace();
/*    */         } 
/*    */       } else {
/* 72 */         ((Message)channel.sendMessage(String.valueOf(name) + " is not a valid username " + m.getAsMention()).complete()).delete()
/* 73 */           .queueAfter(10L, TimeUnit.SECONDS);
/*    */       } 
/*    */     } else {
/* 76 */       SyntaxError.oncmdSyntaxError(channel, "hypixel rank [playername]", m);
/*    */     } 
/*    */   }
/*    */ }


/* Location:              D:\Felix\Desktop\Bot\Bot.jar!\de\k7bot\hypixel\commands\HypixelRankCommand.class
 * Java compiler version: 15 (59.0)
 * JD-Core Version:       1.1.3
 */