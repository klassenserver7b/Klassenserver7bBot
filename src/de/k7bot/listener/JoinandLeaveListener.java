/*    */ package de.k7bot.listener;
/*    */ 
/*    */ import java.time.OffsetDateTime;
/*    */ import net.dv8tion.jda.api.EmbedBuilder;
/*    */ import net.dv8tion.jda.api.entities.Member;
/*    */ import net.dv8tion.jda.api.entities.TextChannel;
/*    */ import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
/*    */ import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
/*    */ import net.dv8tion.jda.api.hooks.ListenerAdapter;
/*    */ 
/*    */ 
/*    */ public class JoinandLeaveListener
/*    */   extends ListenerAdapter
/*    */ {
/*    */   public void onGuildMemberJoin(GuildMemberJoinEvent event) {
/* 16 */     TextChannel system = event.getGuild().getSystemChannel();
/* 17 */     TextChannel def = event.getGuild().getDefaultChannel();
/* 18 */     String guildname = event.getGuild().getName();
/* 19 */     Member memb = event.getGuild().getMember(event.getUser());
/* 20 */     EmbedBuilder embbuild = new EmbedBuilder();
/* 21 */     embbuild.setTimestamp(OffsetDateTime.now());
/* 22 */     embbuild.setThumbnail(memb.getUser().getEffectiveAvatarUrl());
/* 23 */     embbuild.setTitle("@" + memb.getEffectiveName() + " joined :thumbsup:");
/* 24 */     embbuild.setFooter("Member joined");
/* 25 */     embbuild.setColor(58944);
/* 26 */     embbuild.setDescription(String.valueOf(memb.getAsMention()) + " joined");
/*    */     
/* 28 */     system.sendMessageEmbeds(embbuild.build(), new net.dv8tion.jda.api.entities.MessageEmbed[0]).queue();
/* 29 */     def.sendMessage("Willkommen auf dem " + guildname + " Server " + memb.getAsMention()).queue();
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public void onGuildMemberRemove(GuildMemberRemoveEvent event) {
/* 35 */     TextChannel system = event.getGuild().getSystemChannel();
/* 36 */     TextChannel def = event.getGuild().getDefaultChannel();
/* 37 */     Member memb = event.getGuild().getMember(event.getUser());
/* 38 */     EmbedBuilder embbuild = new EmbedBuilder();
/* 39 */     embbuild.setTimestamp(OffsetDateTime.now());
/* 40 */     embbuild.setThumbnail(memb.getUser().getEffectiveAvatarUrl());
/* 41 */     embbuild.setTitle("@" + memb.getEffectiveName() + " leaved :sob:");
/* 42 */     embbuild.setFooter("Member leaved");
/* 43 */     embbuild.setColor(13565967);
/* 44 */     embbuild.setDescription(String.valueOf(memb.getAsMention()) + " leaved");
/*    */     
/* 46 */     system.sendMessageEmbeds(embbuild.build(), new net.dv8tion.jda.api.entities.MessageEmbed[0]).queue();
/* 47 */     def.sendMessage("Schade das du gehst " + memb.getAsMention()).queue();
/*    */   }
/*    */ }


/* Location:              D:\Felix\Desktop\Bot\Bot.jar!\de\k7bot\listener\JoinandLeaveListener.class
 * Java compiler version: 15 (59.0)
 * JD-Core Version:       1.1.3
 */