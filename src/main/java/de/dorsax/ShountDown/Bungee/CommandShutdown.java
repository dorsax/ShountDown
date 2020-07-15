package de.dorsax.ShountDown.Bungee;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ConnectedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.scheduler.ScheduledTask;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.chat.TextComponent;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;


public class CommandShutdown extends Command {

    private Plugin plugin;
    private ScheduledTask scheduledTask;

    public CommandShutdown(Plugin plugin) {
        super("bshutdown");
        this.plugin = plugin;
    }

    private TextComponent createMessage(String appendix) {
        TextComponent tc_prefix = new TextComponent("[" + ChatColor.RED + "Shountdown"+ChatColor.WHITE + "@" + ChatColor.RED + "Proxy" + ChatColor.WHITE + "] ");
        TextComponent tc_message = tc_prefix.duplicate();
        tc_message.addExtra(appendix);
        return tc_message;
    }

    @Override
    public void execute(CommandSender commandSender, String[] args) {
        //commandSender.sendMessage(new TextComponent(ChatColor.GREEN + "Hello "+ commandSender.getName() + "!"));
        //ProxyServer.getInstance().stop("Reason");

        String s_message = "";
        CommandHandler ch_handler = new CommandHandler();
        ch_handler.bungeeSplitter(args);

        if (ch_handler.isOkay()) {
            if (ch_handler.isAbort()) { //if schedule has to be aborted
                if (this.scheduledTask != null) {
                //if (Bukkit.getScheduler().isQueued(i_schedulerId)) {
                    ProxyServer.getInstance().getScheduler().cancel(this.scheduledTask);
                    this.scheduledTask = null;
                    //Bukkit.getScheduler().cancelTask(i_schedulerId);
                    s_message = "Schedule aborted";
                    //send a message to player and log
                    ProxyServer.getInstance().broadcast(createMessage(s_message+"."));
                    ProxyServer.getInstance().getLogger().log(Level.INFO,s_message + " by Player "+commandSender.getName()+".");
                    return;
                } else {
                    commandSender.sendMessage(createMessage("There is no schedule to abort..."));
                    return;
                }
            } else { //if a new schedule has to be created
                if (this.scheduledTask != null) { //if scheduler is already running
                //if (ProxyServer.getInstance().getScheduler().isQueued(i_schedulerId)) {
                    s_message = "A schedule is already running! Please abort it before running this command again.";
                    commandSender.sendMessage(createMessage(s_message));
                    return;
                } else {
                    //create new scheduler and let it run
                    int i_type = -1;
                    String s_shutdownMessage ="";

                    //if (label.contentEquals("shutdown")) {
                        i_type = 0;
                        s_shutdownMessage = "Server shuts down";
                    //}
                    //if (label.contentEquals("reboot")) {
                    //    i_type = 1;
                    //    s_shutdownMessage = "Server reboots";
                    //}


                    ShutdownManager.setType(i_type);
                    ShutdownManager.setComment(ch_handler.getComment());
                    ShutdownManager.setWhitelist(ch_handler.isWhitelist());

                    //de.dorsax.ShountDown.Spigot.Scheduler scheduler = new Scheduler(this.plugin,ldt_time, s_shutdownMessage, sd);
                    Scheduler schedule = new Scheduler(LocalDateTime.now().plusMinutes(1L).plusSeconds(1L),s_shutdownMessage);
                    schedule.setSilent(ch_handler.isSilent());
                    scheduledTask = ProxyServer.getInstance().getScheduler().schedule(this.plugin,schedule,1L, 1L, TimeUnit.SECONDS);
                    schedule.setScheduledTask(scheduledTask);


                    s_message = "Scheduled shutdown at " + String.format("%02d", ch_handler.getTime().getHour()) + ":" + String.format("%02d", ch_handler.getTime().getMinute()) + " by Player " + commandSender.getName() + ".";
                    if (!ch_handler.isSilent()) ProxyServer.getInstance().broadcast(createMessage(s_message));
                    ProxyServer.getInstance().getLogger().log(Level.WARNING, s_message);


                    return;
                }
            }
        } else {
            commandSender.sendMessage(createMessage("Wrong usage!"));
        }



    }


}
