/*    */ package de.k7bot.music;
/*    */ 
/*    */ import de.k7bot.Klassenserver7bbot;
/*    */ import java.util.concurrent.ConcurrentHashMap;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class PlayerManager
/*    */ {
/* 12 */   public ConcurrentHashMap<Long, MusicController> controller = new ConcurrentHashMap<>();
/*    */ 
/*    */   
/*    */   public MusicController getController(long guildid) {
/* 16 */     MusicController mc = null;
/*    */     
/* 18 */     if (this.controller.containsKey(Long.valueOf(guildid))) {
/* 19 */       mc = this.controller.get(Long.valueOf(guildid));
/*    */     } else {
/*    */       
/* 22 */       mc = new MusicController(Klassenserver7bbot.INSTANCE.shardMan.getGuildById(guildid));
/*    */       
/* 24 */       this.controller.put(Long.valueOf(guildid), mc);
/*    */     } 
/*    */ 
/*    */     
/* 28 */     return mc;
/*    */   }
/*    */ 
/*    */   
/*    */   public long getGuildbyPlayerHash(int hash) {
/* 33 */     for (MusicController controller : this.controller.values()) {
/* 34 */       if (controller.getPlayer().hashCode() == hash) {
/* 35 */         return controller.getGuild().getIdLong();
/*    */       }
/*    */     } 
/* 38 */     return -1L;
/*    */   }
}