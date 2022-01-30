/*    */ package de.k7bot.listener;
/*    */ 
/*    */ import java.time.OffsetDateTime;
/*    */ import net.dv8tion.jda.api.EmbedBuilder;
/*    */ import net.dv8tion.jda.api.entities.Guild;
/*    */ import net.dv8tion.jda.api.entities.GuildChannel;
/*    */ import net.dv8tion.jda.api.entities.Invite;
/*    */ import net.dv8tion.jda.api.entities.Role;
/*    */ import net.dv8tion.jda.api.entities.TextChannel;
/*    */ import net.dv8tion.jda.api.events.guild.invite.GuildInviteCreateEvent;
/*    */ import net.dv8tion.jda.api.events.role.RoleCreateEvent;
/*    */ import net.dv8tion.jda.api.events.role.RoleDeleteEvent;
/*    */ import net.dv8tion.jda.api.hooks.ListenerAdapter;
/*    */ 
/*    */ public class Role_InviteListener
/*    */   extends ListenerAdapter {
/* 18 */   String gen = "";
/*    */ 
/*    */   
/*    */   public void onRoleCreate(RoleCreateEvent event) {
/* 22 */     Guild guild = event.getGuild();
/* 23 */     TextChannel system = guild.getSystemChannel();
/* 24 */     Role rolle = event.getRole();
/*    */     
/* 26 */     rolle.getPermissions().forEach(perm -> this.gen = String.valueOf(this.gen) + perm.getName() + ", ");
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */     
/* 32 */     EmbedBuilder builder = new EmbedBuilder();
/* 33 */     builder.setTimestamp(OffsetDateTime.now());
/* 34 */     builder.setFooter(guild.getName());
/* 35 */     builder.setColor(58944);
/* 36 */     builder.setTitle("Role created: " + rolle.getName());
/* 37 */     builder.setDescription(
/* 38 */         "**Role: **\n @" + rolle.getName() + "\n\n **Permissions: **\n" + this.gen);
/* 39 */     system.sendMessageEmbeds(builder.build(), new net.dv8tion.jda.api.entities.MessageEmbed[0]).queue();
/*    */     
/* 41 */     this.gen = "";
/*    */   }
/*    */ 
/*    */   
/*    */   public void onRoleDelete(RoleDeleteEvent event) {
/* 46 */     Guild guild = event.getGuild();
/* 47 */     TextChannel system = guild.getSystemChannel();
/* 48 */     Role rolle = event.getRole();
/* 49 */     EmbedBuilder builder = new EmbedBuilder();
/* 50 */     builder.setTimestamp(OffsetDateTime.now());
/* 51 */     builder.setFooter(guild.getName());
/* 52 */     builder.setColor(13565967);
/* 53 */     builder.setTitle("Role deleted: " + rolle.getName());
/* 54 */     builder.setDescription("**Role: **\n @" + rolle.getName());
/* 55 */     system.sendMessageEmbeds(builder.build(), new net.dv8tion.jda.api.entities.MessageEmbed[0]).queue();
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public void onGuildInviteCreate(GuildInviteCreateEvent event) {
/* 61 */     Guild guild = event.getGuild();
/* 62 */     GuildChannel channel = event.getChannel();
/* 63 */     TextChannel system = guild.getSystemChannel();
/* 64 */     Invite inv = event.getInvite();
/*    */     
/* 66 */     EmbedBuilder builder = new EmbedBuilder();
/* 67 */     builder.setTimestamp(OffsetDateTime.now());
/* 68 */     builder.setFooter(guild.getName());
/* 69 */     builder.setColor(58944);
/* 70 */     builder.setTitle("Invite created for " + channel.getName());
/* 71 */     builder.setDescription("**Invite: **\n " + inv.getUrl() + "\n\n **Channel: **\n" + channel.getName() + 
/* 72 */         "\n\n **Inviter: **\n@" + inv.getInviter().getName() + "\n\n **Is temporary: **\n" + inv.isTemporary() + 
/* 73 */         "\n\n **Is expandet: **\n" + inv.isExpanded());
/* 74 */     system.sendMessageEmbeds(builder.build(), new net.dv8tion.jda.api.entities.MessageEmbed[0]).queue();
/*    */   }
/*    */ }


/* Location:              D:\Felix\Desktop\Bot\Bot.jar!\de\k7bot\listener\Role_InviteListener.class
 * Java compiler version: 15 (59.0)
 * JD-Core Version:       1.1.3
 */