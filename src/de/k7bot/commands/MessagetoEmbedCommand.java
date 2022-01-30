/*    */ package de.k7bot.commands;
/*    */ 
/*    */ import de.k7bot.commands.types.ServerCommand;
/*    */ import java.time.OffsetDateTime;
/*    */ import net.dv8tion.jda.api.EmbedBuilder;
/*    */ import net.dv8tion.jda.api.entities.Member;
/*    */ import net.dv8tion.jda.api.entities.Message;
/*    */ import net.dv8tion.jda.api.entities.TextChannel;
/*    */ 
/*    */ 
/*    */ public class MessagetoEmbedCommand
/*    */   implements ServerCommand
/*    */ {
/*    */   public void performCommand(Member m, TextChannel channel, Message message) {
/* 15 */     message.delete().queue();
/* 16 */     String mess = message.getContentRaw().substring(9);
/*    */     
/* 18 */     EmbedBuilder builder = new EmbedBuilder();
/* 19 */     builder.setFooter("Requested by @" + m.getEffectiveName());
/* 20 */     builder.setTimestamp(OffsetDateTime.now());
/* 21 */     builder.setColor(4128512);
/* 22 */     builder.setTitle("@" + m.getEffectiveName() + "'s embed");
/* 23 */     builder.setDescription(mess);
/*    */     
/* 25 */     channel.sendMessageEmbeds(builder.build(), new net.dv8tion.jda.api.entities.MessageEmbed[0]).queue();
/*    */   }
/*    */ }


/* Location:              D:\Felix\Desktop\Bot\Bot.jar!\de\k7bot\commands\MessagetoEmbedCommand.class
 * Java compiler version: 15 (59.0)
 * JD-Core Version:       1.1.3
 */