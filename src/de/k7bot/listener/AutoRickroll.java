/*    */ package de.k7bot.listener;
/*    */ 
/*    */ import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
/*    */ import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
/*    */ import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
/*    */ import de.k7bot.Klassenserver7bbot;
/*    */ import de.k7bot.music.AudioLoadResult;
/*    */ import de.k7bot.music.MusicController;
/*    */ import de.k7bot.music.Queue;
import net.dv8tion.jda.api.entities.AudioChannel;
/*    */ import net.dv8tion.jda.api.events.guild.voice.GuildVoiceJoinEvent;
/*    */ import net.dv8tion.jda.api.hooks.ListenerAdapter;
/*    */ import net.dv8tion.jda.api.managers.AudioManager;
/*    */ 
/*    */ public class AutoRickroll
/*    */   extends ListenerAdapter
/*    */ {
/*    */   public void onGuildVoiceJoin(GuildVoiceJoinEvent event) {
/* 19 */     if (event.getGuild().getIdLong() == 701341683325075477L && 
/* 20 */       event.getMember().getIdLong() != 846296603139506187L && 
/* 21 */       Math.random() >= 0.95D) {
/* 22 */       AudioChannel vc = event.getChannelJoined();
/* 23 */       MusicController controller = Klassenserver7bbot.INSTANCE.playerManager
/* 24 */         .getController(vc.getGuild().getIdLong());
/* 25 */       AudioManager manager = vc.getGuild().getAudioManager();
/* 26 */       AudioPlayerManager apm = Klassenserver7bbot.INSTANCE.audioPlayerManager;
/* 27 */       AudioPlayer player = controller.getPlayer();
/* 28 */       Queue queue = controller.getQueue();
/*    */       
/* 30 */       String url = "https://www.youtube.com/watch?v=BBJa32lCaaY";
/*    */       
/* 32 */       if (player.getPlayingTrack() == null) {
/* 33 */         if (!queue.emptyQueueList()) {
/* 34 */           queue.clearQueue();
/*    */         }
/* 36 */         manager.openAudioConnection(vc);
/* 37 */         apm.loadItem(url, (AudioLoadResultHandler)new AudioLoadResult(controller, url));
/* 38 */         player.setPaused(false);
/*    */       } else {
/*    */         
/* 41 */         if (!queue.emptyQueueList()) {
/* 42 */           queue.clearQueue();
/*    */         }
/* 44 */         player.stopTrack();
/* 45 */         apm.loadItem(url, (AudioLoadResultHandler)new AudioLoadResult(controller, url));
/* 46 */         player.setPaused(false);
/*    */       } 
/*    */     } 
/*    */   }
/*    */ }


/* Location:              D:\Felix\Desktop\Bot\Bot.jar!\de\k7bot\listener\AutoRickroll.class
 * Java compiler version: 15 (59.0)
 * JD-Core Version:       1.1.3
 */