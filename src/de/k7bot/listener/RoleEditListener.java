/*    */ package de.k7bot.listener;
/*    */ 
/*    */ import java.time.OffsetDateTime;
/*    */ import java.util.ArrayList;
/*    */ import java.util.List;
/*    */ import javax.annotation.Nonnull;
/*    */ import net.dv8tion.jda.api.EmbedBuilder;
/*    */ import net.dv8tion.jda.api.entities.Guild;
/*    */ import net.dv8tion.jda.api.entities.Role;
/*    */ import net.dv8tion.jda.api.entities.TextChannel;
/*    */ import net.dv8tion.jda.api.events.role.update.RoleUpdateNameEvent;
/*    */ import net.dv8tion.jda.api.events.role.update.RoleUpdatePermissionsEvent;
/*    */ import net.dv8tion.jda.api.hooks.ListenerAdapter;
/*    */ 
/*    */ public class RoleEditListener extends ListenerAdapter {
/* 19 */   String remove = "";
/*    */   
/* 20 */   String added = "";
/*    */   
/* 21 */   String gen = "";
/*    */   
/*    */   public void onRoleUpdateName(@Nonnull RoleUpdateNameEvent event) {
/* 26 */     Guild guild = event.getGuild();
/* 27 */     TextChannel system = guild.getSystemChannel();
/* 28 */     Role rolle = event.getRole();
/* 30 */     rolle.getPermissions().forEach(perm -> this.gen = String.valueOf(this.gen) + perm.getName() + ", ");
/* 36 */     EmbedBuilder builder = new EmbedBuilder();
/* 37 */     builder.setTimestamp(OffsetDateTime.now());
/* 38 */     builder.setFooter(guild.getName());
/* 39 */     builder.setColor(58944);
/* 40 */     builder.setTitle("Role(**Name**) edited: " + rolle.getName());
/* 41 */     builder.setDescription("Old Rolename: **\n @" + event.getOldName() + "\n\n**New Rolename: **\n @" + 
/* 42 */         event.getNewName() + "\n\n **Permissions: **\n" + this.gen);
/* 43 */     system.sendMessageEmbeds(builder.build(), new net.dv8tion.jda.api.entities.MessageEmbed[0]).queue();
/* 45 */     this.gen = "";
/*    */   }
/*    */   
/*    */   public void onRoleUpdatePermissions(@Nonnull RoleUpdatePermissionsEvent event) {
/* 51 */     List<String> rem = new ArrayList<>();
/* 52 */     List<String> add = new ArrayList<>();
/* 54 */     event.getOldPermissions().forEach(perm -> {
/*    */           if (!event.getNewPermissions().contains(perm))
/*    */             add.add(perm.getName()); 
/*    */         });
/* 59 */     event.getNewPermissions().forEach(perm -> {
/*    */           this.gen = String.valueOf(this.gen) + perm.getName() + ", ";
/*    */           if (!event.getOldPermissions().contains(perm))
/*    */             rem.add(perm.getName()); 
/*    */         });
/* 65 */     rem.forEach(str -> this.remove = String.valueOf(this.remove) + str + ", ");
/* 71 */     add.forEach(str -> this.added = String.valueOf(this.added) + str + ", ");
/* 77 */     Guild guild = event.getGuild();
/* 78 */     TextChannel system = guild.getSystemChannel();
/* 79 */     Role rolle = event.getRole();
/* 80 */     EmbedBuilder builder = new EmbedBuilder();
/* 81 */     builder.setTimestamp(OffsetDateTime.now());
/* 82 */     builder.setFooter(guild.getName());
/* 83 */     builder.setColor(58944);
/* 84 */     builder.setTitle("Role(**Permissions**) edited: " + rolle.getName());
/* 85 */     builder.setDescription("**Role: **\n @" + rolle.getName() + "\n\n **Removed Permissions: **\n" + this.remove + 
/* 86 */         "\n\n **Added Permissions: **\n" + this.added + "\n\n **Permissions: **\n" + this.gen);
/* 87 */     system.sendMessageEmbeds(builder.build(), new net.dv8tion.jda.api.entities.MessageEmbed[0]).queue();
/* 89 */     this.gen = "";
/*    */   }
/*    */ }


/* Location:              D:\Felix\Desktop\Bot\Bot.jar!\de\k7bot\listener\RoleEditListener.class
 * Java compiler version: 15 (59.0)
 * JD-Core Version:       1.1.3
 */