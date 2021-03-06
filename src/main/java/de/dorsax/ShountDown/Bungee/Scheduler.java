package de.dorsax.ShountDown.Bungee;



import de.dorsax.ShountDown.Spigot.Shutdown;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.scheduler.ScheduledTask;
import org.bukkit.plugin.java.JavaPlugin;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class Scheduler implements Runnable {

    private ScheduledTask scheduledTask;
    private long l_hours, l_minutes,l_seconds;
    private boolean b_silent;
    private LocalDateTime ldt_goaltime,ldt_last;
    private String s_message;

    public Scheduler(LocalDateTime goaltime, String s_message) {
        // Done: calculate timespan in minutes and hours to use later

        this.s_message = s_message;
        this.ldt_goaltime=goaltime;
        this.ldt_last=LocalDateTime.now();
        this.l_hours=ChronoUnit.HOURS.between(ldt_last, ldt_goaltime);
        this.l_minutes=ChronoUnit.MINUTES.between(ldt_last, ldt_goaltime);
        this.l_seconds=0;
    }

    public void setSilent(boolean b_silent) {
        this.b_silent=b_silent;
    }

    public void setScheduledTask(ScheduledTask scheduledTask) {
        this.scheduledTask=scheduledTask;
    }
    @Override
    public void run() {
        // method checks, if the saved times differ from the newly fetched and the time has exceeded its lifespan.
        // if thats the case it broadcasts a message to the server
        // if the goaltime has been reached, the registered action is triggered and the scheduler canceled

        LocalDateTime ldt_now = LocalDateTime.now();
        long l_hours = ChronoUnit.HOURS.between(ldt_now, ldt_goaltime);
        long l_minutes = ChronoUnit.MINUTES.between(ldt_now, ldt_goaltime)+1;
        long l_seconds = ChronoUnit.SECONDS.between(ldt_now, ldt_goaltime)+1;
        String s_time= "";


        if (this.l_hours>0) { //while hours remaining
            if (this.l_hours != l_hours) {
                s_time=" in "+this.l_hours+" hour";
                if (this.l_hours>=2) {s_time+="s";}
                s_time=".";
                this.l_hours=l_hours;
            } else {
                return;
            }

        } else if (l_minutes>15) { //45,30,15 minutes
            if (l_minutes == 60) return;
            if ((l_minutes)%15==0 && this.l_minutes!=l_minutes) {
                this.l_minutes=l_minutes;
                s_time=" in "+l_minutes+" minutes.";
            } else {
                return;
            }

        } else if (l_minutes>1) { //15,10,5 minutes
            if ((l_minutes)%5==0 && this.l_minutes!=l_minutes) {
                this.l_minutes=l_minutes;
                s_time=" in "+l_minutes+" minutes.";
            } else {
                return;
            }

        } else if (l_minutes==1 && this.l_minutes!=l_minutes) { // 1 Minute
            this.l_minutes=l_minutes;
            s_time=" in 1 minute.";

        } else if (l_seconds>15) { //45,30,15 seconds
            if (l_seconds == 60) return;
            if ((l_seconds)%15==0 && this.l_seconds!=l_seconds) {
                this.l_seconds=l_seconds;
                s_time=" in "+l_seconds+" seconds.";
            } else {
                return;
            }

        } else if (l_seconds > 5) { // 15,10 seconds
            if ((l_seconds)%5==0 && this.l_seconds!=l_seconds) {
                this.l_seconds=l_seconds;
                s_time=" in "+l_seconds+" seconds.";
            } else {
                return;
            }

        } else if (l_seconds > 1 ) { //5,4,3,2,1 seconds
            if (this.l_seconds!=l_seconds) {
                this.l_seconds=l_seconds;
                s_time=" in "+ l_seconds +" seconds.";
            } else {
                return;
            }
        } else if (l_seconds == 1 ) { //5,4,3,2,1 seconds
            if (this.l_seconds!=l_seconds) {
                this.l_seconds=l_seconds;
                s_time=" in 1 second.";
            } else {
                return;
            }
        } else if (l_seconds <= 0) { //0 seconds
            s_time=".";
        }

        TextComponent textComponent = new TextComponent(ChatColor.RED + "[Proxy] "+ ChatColor.WHITE + this.s_message + s_time);

        if (!b_silent) {
            ProxyServer.getInstance().broadcast(textComponent);
            //this.plugin.getServer().broadcastMessage("§4[Server]§r " + this.s_message + s_time); //broadcast only if not silent
        } else {
            ProxyServer.getInstance().getConsole().sendMessage(textComponent);
            //getConsoleSender().sendMessage("§4[ShountDown] §r" + this.s_message + s_time); //console only if silent
        }
        if (l_seconds <= 0) { //if action should start, trigger it here
            //this.sd.run();
            ProxyServer.getInstance().getScheduler().cancel(this.scheduledTask);
            ProxyServer.getInstance().stop("Reason");
        }
    }
}


