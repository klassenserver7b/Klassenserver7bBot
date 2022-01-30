/*    */ package de.k7bot.commands;
import de.k7bot.Klassenserver7bbot;
/*    */ 
/*    */ import de.k7bot.commands.types.ServerCommand;
/*    */ import de.k7bot.manage.PermissionError;
/*    */ import de.k7bot.manage.SyntaxError;
/*    */ import java.time.OffsetDateTime;
/*    */ import java.util.List;
/*    */ import java.util.concurrent.TimeUnit;
/*    */ import net.dv8tion.jda.api.EmbedBuilder;
/*    */ import net.dv8tion.jda.api.Permission;
/*    */ import net.dv8tion.jda.api.entities.Guild;
/*    */ import net.dv8tion.jda.api.entities.Member;
/*    */ import net.dv8tion.jda.api.entities.Message;
/*    */ import net.dv8tion.jda.api.entities.TextChannel;
/*    */ import net.dv8tion.jda.api.exceptions.HierarchyException;
/*    */ 
/*    */ 
/*    */ public class MuteCommand
/*    */   implements ServerCommand
/*    */ {
/*    */   public void performCommand(Member m, TextChannel channel, Message message) {
/* 23 */     List<Member> ment = message.getMentionedMembers();
/* 24 */     message.delete().queue();
/*    */     try {
/* 26 */       String grund = message.getContentDisplay().substring(((Member)ment.get(0)).getEffectiveName().length() + 8);
/*    */       
/* 28 */       channel.sendTyping().queue();
/*    */       
/* 30 */       if (m.hasPermission(new Permission[] { Permission.KICK_MEMBERS })) {
/* 31 */         if (ment.size() > 0) {
/* 32 */           for (Member u : ment) {
/* 33 */             onMute(m, u, channel, message, grund);
/*    */           }
/*    */         }
/*    */       } else {
/* 37 */         PermissionError.onPermissionError(m, channel);
/*    */       } 
/* 39 */     } catch (StringIndexOutOfBoundsException e) {
/* 40 */       SyntaxError.oncmdSyntaxError(channel, "mute [@user] [reason]", m);
/*    */     } 
/*    */   }
/*    */ 
/*    */   
/*    */   public void onMute(Member requester, Member u, TextChannel channel, Message message, String grund) {
/* 46 */     EmbedBuilder builder = new EmbedBuilder();
/* 47 */     builder.setFooter("Requested by @" + requester.getEffectiveName());
/* 48 */     builder.setTimestamp(OffsetDateTime.now());
/* 49 */     builder.setThumbnail(u.getUser().getEffectiveAvatarUrl());
/* 50 */     builder.setColor(16711680);
/* 51 */     builder.setTitle("@" + u.getEffectiveName() + " was muted");
/*    */     
/* 53 */     StringBuilder strBuilder = new StringBuilder();
/* 54 */     strBuilder.append("**User: **" + u.getAsMention() + "\n");
/* 55 */     strBuilder.append("**Case: **" + grund + "\n");
/* 56 */     strBuilder.append("**Requester: **" + requester.getAsMention() + "\n");
/*    */     
/* 58 */     builder.setDescription(strBuilder);
/*    */     
/* 60 */     Guild guild = channel.getGuild();
/* 61 */     TextChannel system = guild.getSystemChannel();
/*    */     
/*    */     try {
/* 64 */       Guild g = channel.getGuild();
/* 65 */       g.addRoleToMember(u.getIdLong(), g.getRoleById(702828274837094400L));
/*    */       
/* 67 */       if (system.getIdLong() != channel.getIdLong()) {
/* 68 */         ((Message)channel.sendMessageEmbeds(builder.build(), new net.dv8tion.jda.api.entities.MessageEmbed[0]).complete()).delete().queueAfter(20L, TimeUnit.SECONDS);
/*    */       }
/*    */       
/* 71 */       String action = "mute";
/* 72 */       Klassenserver7bbot.INSTANCE.getDB().onUpdate(
/* 73 */           "INSERT INTO modlogs(guildId, memberId, requesterId, memberName, requesterName, action, reason, date) VALUES(" + 
/* 74 */           channel.getGuild().getIdLong() + ", " + u.getIdLong() + ", " + requester.getIdLong() + 
/* 75 */           ", '" + u.getEffectiveName() + "', '" + requester.getEffectiveName() + "', '" + action + 
/* 76 */           "', '" + grund + "', '" + OffsetDateTime.now() + "')");
/* 77 */     } catch (HierarchyException e) {
/* 78 */       PermissionError.onPermissionError(requester, channel);
/*    */     } 
/*    */   }
/*    */ }