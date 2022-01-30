/*    */ package de.k7bot.listener;
/*    */ 
/*    */ import net.dv8tion.jda.api.entities.ChannelType;
/*    */ import net.dv8tion.jda.api.entities.TextChannel;
/*    */ import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
/*    */ import net.dv8tion.jda.api.hooks.ListenerAdapter;
/*    */ 
/*    */ public class MemesReact
/*    */   extends ListenerAdapter
/*    */ {
/*    */   public void onMessageReceived(MessageReceivedEvent event) {
/* 12 */     if (event.isFromType(ChannelType.TEXT))
/*    */     {
/* 14 */       if (event.getGuild().getIdLong() == 779024287733776454L && 
/* 15 */         event.getChannel().getIdLong() == 780000480406405130L) {
/* 16 */         Long messid = event.getMessage().getIdLong();
/* 17 */         TextChannel chan = event.getTextChannel();
/*    */         
/* 19 */         chan.addReactionById(messid, chan.getGuild().getEmoteById(896482215473610812L)).queue();
/*    */         
/* 21 */         chan.addReactionById(messid, chan.getGuild().getEmoteById(896482181759778897L)).queue();
/*    */       } 
/*    */     }
/*    */   }
/*    */ }