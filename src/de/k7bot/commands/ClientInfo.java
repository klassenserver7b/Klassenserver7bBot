/*    */ package de.k7bot.commands;
/*    */ 
/*    */ import de.k7bot.commands.types.ServerCommand;
/*    */ import de.k7bot.manage.PermissionError;
/*    */ import java.time.OffsetDateTime;
/*    */ import java.util.List;
/*    */ import java.util.concurrent.TimeUnit;
/*    */ import net.dv8tion.jda.api.EmbedBuilder;
/*    */ import net.dv8tion.jda.api.Permission;
/*    */ import net.dv8tion.jda.api.entities.Member;
/*    */ import net.dv8tion.jda.api.entities.Message;
/*    */ import net.dv8tion.jda.api.entities.Role;
/*    */ import net.dv8tion.jda.api.entities.TextChannel;
/*    */ 
/*    */ 
/*    */ public class ClientInfo
/*    */   implements ServerCommand
/*    */ {
/*    */   public void performCommand(Member m, TextChannel channel, Message message) {
/* 20 */     message.delete().queue();
/*    */     
/* 22 */     channel.sendTyping().queue();
/* 23 */     List<Member> ment = message.getMentionedMembers();
/*    */     
/* 25 */     if (m.hasPermission(new Permission[] { Permission.KICK_MEMBERS, Permission.NICKNAME_MANAGE, Permission.NICKNAME_CHANGE })) {
/* 26 */       if (ment.size() > 0) {
/* 27 */         for (Member u : ment) {
/* 28 */           onInfo(m, u, channel);
/*    */         }
/*    */       }
/*    */     } else {
/* 32 */       PermissionError.onPermissionError(m, channel);
/*    */     } 
/*    */   }
/*    */ 
/*    */   
/*    */   public void onInfo(Member requester, Member u, TextChannel channel) {
/* 38 */     EmbedBuilder builder = new EmbedBuilder();
/* 39 */     builder.setFooter("Requested by @" + requester.getEffectiveName());
/* 40 */     builder.setTimestamp(OffsetDateTime.now());
/* 41 */     builder.setThumbnail(u.getUser().getEffectiveAvatarUrl());
/* 42 */     builder.setColor(16711680);
/* 43 */     builder.setTitle("Info zu " + u.getAsMention());
/*    */     
/* 45 */     StringBuilder strBuilder = new StringBuilder();
/* 46 */     strBuilder.append("**User: **" + u.getAsMention() + "\n");
/* 47 */     strBuilder.append("**ClientId: **" + u.getId() + "\n");
/* 48 */     strBuilder.append("**TimeJoined: **" + u.getTimeJoined() + "\n");
/* 49 */     strBuilder.append("**TimeCreated: **" + u.getTimeCreated() + "\n");
/* 50 */     strBuilder.append("**Nickname: **" + u.getNickname() + "\n");
/* 51 */     strBuilder.append("**Status: **" + u.getOnlineStatus() + "\n");
/* 52 */     strBuilder.append("**Is Owner: **" + u.isOwner() + "\n");
/* 53 */     strBuilder.append("**Permissions: **" + u.getPermissions() + "\n\n");
/*    */     
/* 55 */     strBuilder.append("**Roles: **\n");
/*    */     
/* 57 */     StringBuilder roleBuild = new StringBuilder();
/* 58 */     for (Role role : u.getRoles()) {
/* 59 */       roleBuild.append(String.valueOf(role.getAsMention()) + " ");
/*    */     }
/*    */     
/* 62 */     strBuilder.append(String.valueOf(roleBuild.toString().trim()) + "\n");
/*    */     
/* 64 */     builder.setDescription(strBuilder);
/*    */     
/* 66 */     ((Message)channel.sendMessageEmbeds(builder.build(), new net.dv8tion.jda.api.entities.MessageEmbed[0]).complete()).delete().queueAfter(20L, TimeUnit.SECONDS);
/*    */   }
/*    */ }


/* Location:              D:\Felix\Desktop\Bot\Bot.jar!\de\k7bot\commands\ClientInfo.class
 * Java compiler version: 15 (59.0)
 * JD-Core Version:       1.1.3
 */