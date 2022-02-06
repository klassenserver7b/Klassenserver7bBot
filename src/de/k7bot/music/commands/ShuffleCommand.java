/*    */ package de.k7bot.music.commands;
/*    */ 
/*    */ import de.k7bot.Klassenserver7bbot;
/*    */ import de.k7bot.commands.types.ServerCommand;
/*    */ import de.k7bot.music.MusicController;
/*    */ import java.util.concurrent.TimeUnit;
/*    */ import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.AudioChannel;
/*    */ import net.dv8tion.jda.api.entities.GuildVoiceState;
/*    */ import net.dv8tion.jda.api.entities.Member;
/*    */ import net.dv8tion.jda.api.entities.Message;
/*    */ import net.dv8tion.jda.api.entities.TextChannel;
/*    */ 
/*    */ 
/*    */ public class ShuffleCommand
/*    */   implements ServerCommand
/*    */ {
/*    */   public void performCommand(Member m, TextChannel channel, Message message) {
/* 19 */     message.delete().queue();
/*    */     GuildVoiceState state;
/* 21 */     if ((state = m.getVoiceState()) != null) {
/*    */       AudioChannel vc;
/* 23 */       if ((vc = state.getChannel()) != null) {
/* 24 */         MusicController controller = Klassenserver7bbot.INSTANCE.playerManager
/* 25 */           .getController(vc.getGuild().getIdLong());
/* 26 */         controller.getQueue().shuffle();
/* 27 */         EmbedBuilder builder = new EmbedBuilder();
/* 28 */         builder.setDescription("playlist shuffled");
/* 29 */         builder.setColor(10827773);
/* 30 */         ((Message)channel.sendMessageEmbeds(builder.build(), new net.dv8tion.jda.api.entities.MessageEmbed[0]).complete()).addReaction("U+1F500").queue();
/*    */       } else {
/*    */         
/* 33 */         ((Message)channel.sendMessage("You are not in a voicechannel" + m.getAsMention()).complete()).delete()
/* 34 */           .queueAfter(10L, TimeUnit.SECONDS);
/*    */       } 
/*    */     } else {
/* 37 */       ((Message)channel.sendMessage("You are not in a voicechannel" + m.getAsMention()).complete()).delete().queueAfter(10L, 
/* 38 */           TimeUnit.SECONDS);
/*    */     } 
/*    */   }
/*    */ }


/* Location:              D:\Felix\Desktop\Bot\Bot.jar!\de\k7bot\music\commands\ShuffleCommand.class
 * Java compiler version: 15 (59.0)
 * JD-Core Version:       1.1.3
 */