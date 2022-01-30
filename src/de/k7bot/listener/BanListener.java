/*    */ package de.k7bot.listener;
/*    */ 
/*    */ import java.time.OffsetDateTime;
/*    */ import net.dv8tion.jda.api.EmbedBuilder;
/*    */ import net.dv8tion.jda.api.entities.Guild;
/*    */ import net.dv8tion.jda.api.entities.TextChannel;
/*    */ import net.dv8tion.jda.api.entities.User;
/*    */ import net.dv8tion.jda.api.events.guild.GuildBanEvent;
/*    */ import net.dv8tion.jda.api.events.guild.GuildUnbanEvent;
/*    */ import net.dv8tion.jda.api.hooks.ListenerAdapter;
/*    */ 
/*    */ 
/*    */ public class BanListener
/*    */   extends ListenerAdapter
/*    */ {
/*    */   public void onGuildBan(GuildBanEvent event) {
/* 17 */     Guild guild = event.getGuild();
/* 18 */     TextChannel system = guild.getSystemChannel();
/* 19 */     User user = event.getUser();
/* 20 */     EmbedBuilder builder = new EmbedBuilder();
/* 21 */     builder.setTimestamp(OffsetDateTime.now());
/* 22 */     builder.setThumbnail(user.getEffectiveAvatarUrl());
/* 23 */     builder.setFooter(guild.getName());
/* 24 */     builder.setColor(13565967);
/* 25 */     builder.setTitle("User banned: " + user.getName());
/* 26 */     builder.setDescription("**User: **\n @" + user.getName());
/* 27 */     system.sendMessageEmbeds(builder.build(), new net.dv8tion.jda.api.entities.MessageEmbed[0]).queue();
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public void onGuildUnban(GuildUnbanEvent event) {
/* 34 */     Guild guild = event.getGuild();
/* 35 */     TextChannel system = guild.getSystemChannel();
/* 36 */     User user = event.getUser();
/* 37 */     EmbedBuilder builder = new EmbedBuilder();
/* 38 */     builder.setTimestamp(OffsetDateTime.now());
/* 39 */     builder.setThumbnail(user.getEffectiveAvatarUrl());
/* 40 */     builder.setFooter(guild.getName());
/* 41 */     builder.setColor(58944);
/* 42 */     builder.setTitle("User unbanned: " + user.getName());
/* 43 */     builder.setDescription("**User: **\n @" + user.getName());
/* 44 */     system.sendMessageEmbeds(builder.build(), new net.dv8tion.jda.api.entities.MessageEmbed[0]).queue();
/*    */   }
/*    */ }


/* Location:              D:\Felix\Desktop\Bot\Bot.jar!\de\k7bot\listener\BanListener.class
 * Java compiler version: 15 (59.0)
 * JD-Core Version:       1.1.3
 */