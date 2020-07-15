//Done: Scheduler aufnehmen: https://bukkit.gamepedia.com/Scheduler_Programming
//Done: Broadcast einbauen für Countdown
//Done: Scheduler umbauen auf LocalDateTime
//Done: Scheduler umbauen für CountDown
//TODO: generalise the class to use a specific message instead of a fixed message
//TODO: Maybe append the comment to s_message


package de.dorsax.ShountDown.Spigot;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.logging.Level;

import jdk.nashorn.internal.runtime.regexp.joni.exception.InternalException;
import net.md_5.bungee.api.scheduler.ScheduledTask;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class Scheduler extends BukkitRunnable {

	static private int i_schedulerID;
	private static Scheduler runningScheduler;
    private final JavaPlugin plugin;
    private long l_hours, l_minutes,l_seconds;
    private boolean b_silent;
    private Shutdown sd;
    private LocalDateTime ldt_goaltime,ldt_last;
    private String s_message;

    static {
    	i_schedulerID = 0;
	}

	public Scheduler(JavaPlugin plugin, LocalDateTime goaltime, String s_message, Shutdown shutdown) {
    	// Done: calculate timespan in minutes and hours to use later

		this.s_message = s_message;
        this.plugin = plugin;
        this.sd = shutdown;
        this.ldt_goaltime=goaltime;
        this.ldt_last=LocalDateTime.now();
        this.l_hours=ChronoUnit.HOURS.between(ldt_last, ldt_goaltime);        
        this.l_minutes=ChronoUnit.MINUTES.between(ldt_last, ldt_goaltime);
        this.l_seconds=0;
    }
    
    public void setSilent(boolean b_silent) {
    	this.b_silent=b_silent;
    }

	public long getHours() { //returns how many hours are left
		return this.l_hours;
	}

	public long getMinutes() { //returns how many minutes are left, as the part of the whole time
		return this.l_minutes%60;
	}

	public LocalDateTime getGoaltime() { //returns the goaltime
		return this.ldt_goaltime;
	}

	public String getMessage () {
		return this.s_message;
	}

    public static boolean isRunning() {

    	if (runningScheduler==null) {
    		return false;
		} else {
    		return true;
		}
    	/*if (i_schedulerID == 0) {
    		return false;
		} else {
    		return true;
		}*/

	}

	public static  Scheduler getInstance() {
    	return runningScheduler;
	}

	public static void cancelAll() {

    	if (runningScheduler!=null) {
    		runningScheduler.cancel();
		}
		/*if (i_schedulerID != 0) {
			if (Bukkit.getScheduler().isQueued(i_schedulerID)) {
				Bukkit.getScheduler().cancelTask(i_schedulerID);
			} else {
				i_schedulerID=0;
			}
		}*/
	}

	public void cancel() {
		Bukkit.broadcastMessage("Cancel-Methode:"+i_schedulerID);
		super.cancel(); //throws IllegalStateException
		if (runningScheduler == this ) {
    	//if (i_schedulerID == this.getTaskId()) {
			String s_message = "§4[ShountDown] §rSchedule aborted.";
			//send a message to player or log
			if (!b_silent) {
				Bukkit.broadcastMessage(s_message);
			} else {
				Bukkit.getLogger().log(Level.INFO,s_message);
			}
			runningScheduler=null;
			//i_schedulerID=0;
		} else {
			throw new IllegalStateException();
		}
	}

    @Override
    public void run() {
    	if (runningScheduler == null) {
    		runningScheduler = this;
		}
    	if (this != runningScheduler) {
    		this.cancel();
		}
    	/*
    	if (i_schedulerID == 0) {
			i_schedulerID = this.getTaskId();
		} else if (i_schedulerID != this.getTaskId()) {
    		this.cancel();
    		throw new IllegalStateException(); //this should not happen!
		}*/
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
    	
    	if (!b_silent) {
			this.plugin.getServer().broadcastMessage("§4[Server]§r " + this.s_message + s_time); //broadcast only if not silent
		} else {
			Bukkit.getConsoleSender().sendMessage("§4[ShountDown] §r" + this.s_message + s_time); //console only if silent
		}
    	if (l_seconds <= 0) { //if action should start, trigger it here
        	this.sd.run();
        	this.cancel();
        }
    }

}