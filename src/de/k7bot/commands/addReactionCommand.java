/*    */ package de.k7bot.commands;
/*    */ 
/*    */ import de.k7bot.commands.types.ServerCommand;
/*    */ import java.util.ArrayList;
/*    */ import java.util.List;
/*    */ import net.dv8tion.jda.api.entities.Emote;
/*    */ import net.dv8tion.jda.api.entities.Member;
/*    */ import net.dv8tion.jda.api.entities.Message;
/*    */ import net.dv8tion.jda.api.entities.TextChannel;
/*    */ 
/*    */ 
/*    */ public class addReactionCommand
/*    */   implements ServerCommand
/*    */ {
/*    */   public void performCommand(Member m, TextChannel channel, Message message) {
/* 16 */     message.delete().queue();
/*    */     
/* 18 */     String[] args = message.getContentDisplay().split(" ");
/* 19 */     List<TextChannel> channels = message.getMentionedChannels();
/* 20 */     List<Emote> emotes = message.getEmotes();
/*    */     
/* 22 */     if (!channels.isEmpty()) {
/* 23 */       TextChannel tc = message.getMentionedChannels().get(0);
/* 24 */       String MessageIdString = args[2];
/*    */ 
/*    */       
/*    */       try {
/* 28 */         long MessageId = Long.parseLong(MessageIdString);
/*    */         
/* 30 */         List<String> customemotes = new ArrayList<>();
/*    */         
/* 32 */         for (Emote emote : emotes) {
/* 33 */           tc.addReactionById(MessageId, emote).queue();
/* 34 */           customemotes.add(":" + emote.getName() + ":");
/*    */         } 
/*    */         
/* 37 */         for (int i = 3; i < args.length; i++) {
/* 38 */           String utfemote = args[i];
/* 39 */           if (!customemotes.contains(utfemote)) {
/* 40 */             tc.addReactionById(MessageId, args[i]).queue();
/*    */           }
/*    */         }
/*    */       
/* 44 */       } catch (NumberFormatException e) {
/* 45 */         e.printStackTrace();
/*    */       } 
/*    */     } 
/*    */   }
/*    */ }


/* Location:              D:\Felix\Desktop\Bot\Bot.jar!\de\k7bot\commands\addReactionCommand.class
 * Java compiler version: 15 (59.0)
 * JD-Core Version:       1.1.3
 */