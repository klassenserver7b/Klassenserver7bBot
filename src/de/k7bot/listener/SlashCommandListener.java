/*    */ package de.k7bot.listener;
/*    */ 
/*    */ import de.k7bot.Klassenserver7bbot;
/*    */ import de.k7bot.commands.helpCommand;
/*    */ import java.util.concurrent.TimeUnit;
/*    */ import net.dv8tion.jda.api.entities.Message;
/*    */ import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
/*    */ import net.dv8tion.jda.api.hooks.ListenerAdapter;
/*    */ 
/*    */ public class SlashCommandListener
/*    */   extends ListenerAdapter {
/* 12 */   helpCommand help = new helpCommand();
/*    */ 
/*    */ 
/*    */   
/*    */   public void onSlashCommand(SlashCommandEvent event) {
/* 17 */     if (event.getGuild() == null) {
/*    */       return;
/*    */     }
/*    */     
/* 21 */     if (!Klassenserver7bbot.INSTANCE.getslashMan().perform(event))
/*    */     {
/* 23 */       ((Message)event.getChannel().sendMessage("`unbekannter Slash-Command`").complete()).delete().queueAfter(10L, 
/* 24 */           TimeUnit.SECONDS);
/*    */     }
/*    */   }
/*    */ }


/* Location:              D:\Felix\Desktop\Bot\Bot.jar!\de\k7bot\listener\SlashCommandListener.class
 * Java compiler version: 15 (59.0)
 * JD-Core Version:       1.1.3
 */