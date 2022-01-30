/*    */ package de.k7bot.hypixel.commands;
/*    */ 
/*    */ import de.k7bot.Klassenserver7bbot;
/*    */ import de.k7bot.commands.types.ServerCommand;
/*    */ import java.util.concurrent.TimeUnit;
/*    */ import net.dv8tion.jda.api.entities.Member;
/*    */ import net.dv8tion.jda.api.entities.Message;
/*    */ import net.dv8tion.jda.api.entities.TextChannel;
/*    */ 
/*    */ 
/*    */ 
/*    */ public class SCtoHC
/*    */   implements ServerCommand
/*    */ {
/*    */   public void performCommand(Member m, TextChannel channel, Message message) {
/* 16 */     String mess = message.getContentStripped();
/*    */ 
/*    */     
/* 19 */     if (mess.length() >= 8) {
/*    */ 
/*    */       
/* 22 */       String[] args = mess.substring(8).trim().split(" ");
/*    */ 
/*    */       
/* 25 */       if (args.length > 0)
/*    */       {
/*    */         
/* 28 */         if (!Klassenserver7bbot.INSTANCE.gethypMan().performHypixel(args[0], m, channel, message))
/* 29 */           ((Message)channel.sendMessage("`unbekannter Hypixel - Command` - Hilfe: '-Hypixel help'").complete()).delete().queueAfter(10L, 
/* 30 */               TimeUnit.SECONDS); 
/*    */       }
/*    */     } 
/*    */   }
/*    */ }


/* Location:              D:\Felix\Desktop\Bot\Bot.jar!\de\k7bot\hypixel\commands\SCtoHC.class
 * Java compiler version: 15 (59.0)
 * JD-Core Version:       1.1.3
 */