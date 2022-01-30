/*    */ package de.k7bot.music;
/*    */ 
/*    */ import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
/*    */ import java.util.ArrayList;
/*    */ import java.util.Collections;
/*    */ import java.util.List;
/*    */ import javax.annotation.Nonnull;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class Queue
/*    */ {
/*    */   public List<AudioTrack> queuelist;
/*    */   private MusicController controller;
/*    */   
/*    */   public Queue(MusicController controller) {
/* 18 */     setController(controller);
/* 19 */     setQueuelist(new ArrayList<>());
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean emptyQueueList() {
/* 24 */     if (this.queuelist.size() == 0) {
/* 25 */       return true;
/*    */     }
/* 27 */     return false;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   @Nonnull
/*    */   public boolean next() {
/* 38 */     if (this.queuelist.size() >= 1) {
/* 39 */       AudioTrack track = this.queuelist.remove(0);
/* 40 */       if (track != null) {
/* 41 */         this.controller.getPlayer().playTrack(track);
/* 42 */         return true;
/*    */       } 
/*    */     } 
/*    */     
/* 46 */     return false;
/*    */   }
/*    */ 
/*    */   
/*    */   public void addTracktoQueue(AudioTrack track) {
/* 51 */     this.queuelist.add(track);
/*    */     
/* 53 */     if (this.controller.getPlayer().getPlayingTrack() == null) {
/* 54 */       next();
/*    */     }
/*    */   }
/*    */ 
/*    */   
/*    */   public void clearQueue() {
/* 60 */     this.queuelist.clear();
/*    */   }
/*    */ 
/*    */   
/*    */   public MusicController getController() {
/* 65 */     return this.controller;
/*    */   }
/*    */   
/*    */   public void setController(MusicController controller) {
/* 69 */     this.controller = controller;
/*    */   }
/*    */   
/*    */   public List<AudioTrack> getQueuelist() {
/* 73 */     return this.queuelist;
/*    */   }
/*    */   
/*    */   public void setQueuelist(List<AudioTrack> queuelist) {
/* 77 */     this.queuelist = queuelist;
/*    */   }
/*    */   
/*    */   public void shuffle() {
/* 81 */     Collections.shuffle(this.queuelist);
/*    */   }
/*    */ }


/* Location:              D:\Felix\Desktop\Bot\Bot.jar!\de\k7bot\music\Queue.class
 * Java compiler version: 15 (59.0)
 * JD-Core Version:       1.1.3
 */