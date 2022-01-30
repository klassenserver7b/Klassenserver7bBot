/*    */ package de.k7bot.music;
/*    */ 
/*    */ import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
/*    */ import com.sedmelluq.discord.lavaplayer.track.playback.AudioFrame;
/*    */ import java.nio.ByteBuffer;
/*    */ import net.dv8tion.jda.api.audio.AudioSendHandler;
/*    */ 
/*    */ 
/*    */ public class AudioPlayerSendHandler
/*    */   implements AudioSendHandler
/*    */ {
/*    */   private final AudioPlayer audioPlayer;
/*    */   private AudioFrame lastFrame;
/*    */   
/*    */   public AudioPlayerSendHandler(AudioPlayer audioPlayer) {
/* 16 */     this.audioPlayer = audioPlayer;
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public boolean canProvide() {
/* 22 */     this.lastFrame = this.audioPlayer.provide();
/* 23 */     return (this.lastFrame != null);
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public ByteBuffer provide20MsAudio() {
/* 29 */     return ByteBuffer.wrap(this.lastFrame.getData());
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public boolean isOpus() {
/* 35 */     return true;
/*    */   }
/*    */ }


/* Location:              D:\Felix\Desktop\Bot\Bot.jar!\de\k7bot\music\AudioPlayerSendHandler.class
 * Java compiler version: 15 (59.0)
 * JD-Core Version:       1.1.3
 */