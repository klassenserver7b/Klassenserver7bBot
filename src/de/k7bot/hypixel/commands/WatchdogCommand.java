 package de.k7bot.hypixel.commands;
 
 import de.k7bot.Klassenserver7bbot;
 import de.k7bot.commands.types.HypixelCommand;
 import java.util.concurrent.ExecutionException;
 import net.dv8tion.jda.api.entities.Member;
 import net.dv8tion.jda.api.entities.Message;
 import net.dv8tion.jda.api.entities.TextChannel;
 import net.hypixel.api.HypixelAPI;
 import net.hypixel.api.reply.PunishmentStatsReply;
 
 
 public class WatchdogCommand
   implements HypixelCommand
 {
   public void performHypixelCommand(Member m, TextChannel channel, Message message) {
     HypixelAPI api = Klassenserver7bbot.INSTANCE.getHypixelAPI();
 
 
     
     try {
       PunishmentStatsReply reply = api.getPunishmentStats().get();
       channel.sendMessage(
           "Staff-Rolling-Daily: " + reply.getStaffRollingDaily() + "\nStaff-Total: " + reply.getStaffTotal() + 
           "\nWatchdog-Last-Minute: " + reply.getWatchdogLastMinute() + "\nWatchdog-Rolling-Daily: " + 
           reply.getWatchdogRollingDaily() + "\nWatchdog Total: " + reply.getWatchdogTotal())
         .queue();
     }
     catch (ExecutionException e) {
       System.err.println("Oh no, our API request failed!");
       e.getCause().printStackTrace();
     }
     catch (InterruptedException e) {
       
       System.err.println("Oh no, the player fetch thread was interrupted!");
       e.printStackTrace();
     } 
   }
 }