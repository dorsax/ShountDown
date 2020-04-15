//shutdown -abort -comment "why shutdown is initiated" -time <seconds or time in HH:MM, default 60s> --whitelist --silent  
//TO-DO: Permission-Check https://bukkit.org/threads/how-to-check-permissions.90065/
//Done: validate arguments
//TO-DO: Add short-commands -awst|c 
//Done: Only one action at a time
//Done: Actions can be cancelled
//TO-DO: Add Reboot

package de.dorsax.ShountDown;

import java.time.LocalDateTime;
import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class Commands implements CommandExecutor{
	
	private final JavaPlugin plugin;
	private Scheduler scheduler;
	
	public Commands(JavaPlugin plugin) {
		this.plugin = plugin;
	}
	
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
    	
    	if (label.contentEquals("shutdown")) {
    		return this.cmdShutdown(sender, command, args);
    	} else {
    		return false;
    	}
    
    }
    
    private boolean cmdShutdown (CommandSender sender, Command command, String[] args) {
    	
    	String s_message="";
		boolean b_comment=false;
		String s_comment="";
		boolean b_silent=false;
		boolean b_time=false;
		LocalDateTime ldt_time=LocalDateTime.now().plusMinutes(1);
		String s_time="";
		boolean b_whitelist=false;

    	if (args.length <= 0) {
    		//Do default action
    	} else {
    		//extract things from the thing
    		if (!args[0].startsWith("-")) {
    			return false;
    		}
    		for (int i=0; i<args.length;i++ ) {
    			
    			switch (args[i]) {
    			case "":
    				break;
    			case "-abort":
    				if (this.scheduler!=null) {
	    				this.scheduler.cancel();
	    				this.scheduler = null;
	    				s_message = "§4[ShountDown] §rSchedule aborted";
	    		    	if (sender instanceof Player) {
	    		    		sender.sendMessage(s_message+".");
	    		    	}
	    		    	Bukkit.getConsoleSender().sendMessage(s_message + " by Player "+sender.getName()+".");
	    				return true;
    				} else {
    					sender.sendMessage("§4[ShountDown] §rThere is no schedule to abort...");
        				return true;
    				}
    			case "-comment":
    				if (b_comment || (args.length-1==i)) { //when the flag is already set or no comment followed afterwards
    					return false;
    				}
					if (!args[i+1].startsWith("\"")) { //when the next argument does not start with "
						return false;
					}
					b_comment=true;
					int i_foundAt_0 = 0; //local var, used to determine the last part of the comment in the args[]
					for (int j = i+1; j<args.length;j++) { //find last part of comment, ends with "
						if (args[j].endsWith("\"")) {
							i_foundAt_0 = j;
							break;
						}
					}
					if (i_foundAt_0 != 0) { //if the last part is found, concat the strings between it 
						for (int j = i+1; j<=i_foundAt_0;j++) {
							if (s_comment.isEmpty()) {
								s_comment=args[j];
							} else {
								s_comment=s_comment + " " + args[j];
							}
						}
						s_comment=s_comment.substring(1, s_comment.length()-1); //clear comment from ""
					} else {
						return false;
					}
					i=i_foundAt_0; //index i jumps to end of comment, because it won't cover it anyway
					break;
    				
    			case "-time":
    				if (b_time || (args.length-1==i)) {  //when the flag is already set or no time followed afterwards
    					return false;
    				}
    				//flag is set, time-String is set as the next one, index skippes next entry 
					b_time=true;
					s_time=args[i+1];
					i++;
					
					if (s_time.contains(":")) { //if time is stated as at HH:MM, no need for ""
						String[] as_time = s_time.split(":");
						if (as_time.length!=2) {
							return false;
						}
						try {
							LocalDateTime atm = LocalDateTime.now();
							int hour   = Integer.parseInt(as_time[0]);
						    int minute = Integer.parseInt(as_time[1]);
						    ldt_time = atm.withHour(hour).withMinute(minute).withSecond(0).withNano(0); //time is static, so no need for accurate sec/nano
						    if (ldt_time.isBefore(atm)) { //if time is on the next day
						    	ldt_time = ldt_time.plusDays(1);
						    }
						} catch (java.lang.NumberFormatException | java.time.DateTimeException e) {
							return false;
						}
					} else { //if time is stated as "in X minutes"
						try {
							if (Integer.parseInt(s_time) > 1440 || Integer.parseInt(s_time) < 0) {
								return false;
							}
							ldt_time= LocalDateTime.now().plusMinutes(Integer.parseInt(s_time));
						} catch (java.lang.NumberFormatException | java.time.DateTimeException e) {
							return false;
						}
					}
					break;
    				
    			case "--whitelist": //flags containing '--' can only stand aat the end of a command
    				if ((!b_whitelist & (i==args.length-1)) || (!b_whitelist & (i==args.length-2) & (args[i+1].contains("--silent"))  )) {
    					b_whitelist=true;
    					break;
    				} else {
    					return false;
    				}
    			case "--silent": //flags containing '--' can only stand aat the end of a command
    				if ((!b_silent & (i==args.length-1)) || (!b_silent & (i==args.length-2) & (args[i+1].contains("--whitelist"))  )) {
    					b_silent=true;
    					break;
    				} else {
    					return false;
    				}
    			default:
    				return false;
    			}
    		}
    		
    	}
    	
    	//send fail/success-message to player and console
    	
    	if (this.scheduler != null) {
    		s_message = "[ShountDown] §4A schedule is already running! Please abort the other before running this command again.";
    	} else {
        	//create new scheduler and let it run
        	scheduler = new Scheduler (this.plugin,ldt_time,new Shutdown(b_whitelist,s_comment));
        	scheduler.setSilent(b_silent);
        	scheduler.runTaskTimer(this.plugin, 10, 10);
    		s_message = "§4[ShountDown] §rScheduled shutdown at " + String.format ("%02d", ldt_time.getHour()) + ":" + String.format ("%02d", ldt_time.getMinute());
    	}

    	if (sender instanceof Player) {
    		sender.sendMessage(s_message);
    	}
    	Bukkit.getConsoleSender().sendMessage(s_message);
    	return true;
    }

}