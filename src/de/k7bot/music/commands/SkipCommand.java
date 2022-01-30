/*    */ package de.k7bot.music.commands;
/*    */ 
/*    */ import de.k7bot.Klassenserver7bbot;
/*    */ import de.k7bot.commands.types.ServerCommand;
/*    */ import de.k7bot.manage.SyntaxError;
/*    */ import de.k7bot.music.MusicController;
/*    */ import de.k7bot.music.Queue;
/*    */ import java.time.OffsetDateTime;
/*    */ import java.util.concurrent.TimeUnit;
/*    */ import net.dv8tion.jda.api.EmbedBuilder;
/*    */ import net.dv8tion.jda.api.entities.Member;
/*    */ import net.dv8tion.jda.api.entities.Message;
/*    */ import net.dv8tion.jda.api.entities.TextChannel;
/*    */ 
/*    */ 
/*    */ public class SkipCommand
/*    */   implements ServerCommand
/*    */ {
/*    */   public static boolean onskip = false;
/*    */   
/*    */   public void performCommand(Member m, TextChannel channel, Message message) {
/* 22 */     String[] args = message.getContentDisplay().split(" ");
/* 23 */     long guildid = channel.getGuild().getIdLong();
/* 24 */     MusicController controller = Klassenserver7bbot.INSTANCE.playerManager.getController(guildid);
/* 25 */     Queue queue = controller.getQueue();
/* 26 */     onskip = true;
/* 27 */     if (args.length == 1) {
/* 28 */       onskip = false;
/* 29 */       if (queue.next()) {
/*    */         return;
/*    */       }
/*    */     } else {
/*    */       
/*    */       try {
/* 35 */         for (int i = 0; i <= Integer.parseInt(args[1]) - 1; i++) {
/* 36 */           queue.next();
/*    */         }
/*    */ 
/*    */ 
/*    */         
/* 41 */         onskip = false;
/* 42 */         queue.next();
/*    */ 
/*    */         
/* 45 */         EmbedBuilder builder = new EmbedBuilder();
/* 46 */         builder.setTimestamp(OffsetDateTime.now());
/* 47 */         builder.setFooter("Requested by @" + m.getEffectiveName());
/* 48 */         builder.setTitle(String.valueOf(Integer.parseInt(args[1])) + " tracks skipped");
/* 49 */         ((Message)channel.sendMessageEmbeds(builder.build()).complete()).delete().queueAfter(10L, TimeUnit.SECONDS);
/*    */       }
/* 51 */       catch (NumberFormatException e) {
/* 52 */        SyntaxError.oncmdSyntaxError(channel, "skip [int]", m);
/*    */       } 
/*    */     } 
/* 55 */     onskip = false;
/*    */   }
/*    */ }


/* Location:              D:\Felix\Desktop\Bot\Bot.jar!\de\k7bot\music\commands\SkipCommand.class
 * Java compiler version: 15 (59.0)
 * JD-Core Version:       1.1.3
 */