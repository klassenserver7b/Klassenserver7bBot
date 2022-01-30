/*    */ package de.k7bot.commands;
/*    */ 
/*    */ import de.k7bot.commands.types.ServerCommand;
/*    */ import de.k7bot.manage.PermissionError;
/*    */ import java.time.OffsetDateTime;
/*    */ import java.util.ArrayList;
/*    */ import java.util.List;
/*    */ import java.util.concurrent.TimeUnit;
/*    */ import net.dv8tion.jda.api.EmbedBuilder;
/*    */ import net.dv8tion.jda.api.Permission;
/*    */ import net.dv8tion.jda.api.entities.GuildChannel;
/*    */ import net.dv8tion.jda.api.entities.Member;
/*    */ import net.dv8tion.jda.api.entities.Message;
/*    */ import net.dv8tion.jda.api.entities.MessageChannel;
/*    */ import net.dv8tion.jda.api.entities.TextChannel;
/*    */ 
/*    */ 
/*    */ public class ClearCommand
/*    */   implements ServerCommand
/*    */ {
/*    */   public void performCommand(Member m, TextChannel channel, Message message) {
/* 22 */     if (m.hasPermission((GuildChannel)channel, new Permission[] { Permission.MESSAGE_MANAGE })) {
/* 23 */       String[] args = message.getContentStripped().split(" ");
/*    */       
/* 25 */       if (args.length == 2)
/*    */       {
/* 27 */         onclear(Integer.parseInt(args[1]), channel, m);
/*    */       }
/*    */     }
/*    */     else {
/*    */       
/* 32 */       PermissionError.onPermissionError(m, channel);
/*    */     } 
/*    */   }
/*    */   
/*    */   public static void onclear(int amount, TextChannel chan, Member m) {
/*    */     try {
/* 38 */       TextChannel system = chan.getGuild().getSystemChannel();
/* 39 */       chan.purgeMessages(get((MessageChannel)chan, amount));
/*    */       
/* 41 */       if (chan != system) {
/* 42 */         ((Message)chan.sendMessage(String.valueOf(amount) + " messages deleted.").complete()).delete().queueAfter(3L, TimeUnit.SECONDS);
/*    */       }
/* 44 */       EmbedBuilder builder = new EmbedBuilder();
/* 45 */       builder.setColor(16345358);
/* 46 */       builder.setFooter("requested by @" + m.getEffectiveName());
/* 47 */       builder.setTimestamp(OffsetDateTime.now());
/* 48 */       builder.setDescription(String.valueOf(amount) + " messages deleted!\n\n" + "**Channel: **\n" + "#" + chan.getName());
/* 49 */       system.sendMessageEmbeds(builder.build(), new net.dv8tion.jda.api.entities.MessageEmbed[0]).queue();
/*    */       
/*    */       return;
/* 52 */     } catch (NumberFormatException e) {
/* 53 */       e.printStackTrace();
/*    */       return;
/*    */     } 
/*    */   }
/*    */   
/*    */   public static List<Message> get(MessageChannel channel, int amount) {
/* 59 */     List<Message> messages = new ArrayList<>();
/* 60 */     int i = 0;
/*    */     
/* 62 */     for (Message message : channel.getIterableHistory().cache(false)) {
/* 63 */       if (!message.isPinned()) {
/* 64 */         messages.add(message);
/*    */       }
/*    */       
/* 67 */       if (i++ >= amount) {
/*    */         break;
/*    */       }
/*    */     } 
/* 71 */     return messages;
/*    */   }
/*    */ }


/* Location:              D:\Felix\Desktop\Bot\Bot.jar!\de\k7bot\commands\ClearCommand.class
 * Java compiler version: 15 (59.0)
 * JD-Core Version:       1.1.3
 */