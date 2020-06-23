//TODO: Add short-commands -awst|c

package de.dorsax.ShountDown.Spigot;


import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.time.LocalDateTime;
import java.util.logging.Level;

public class CommandHandler {

    private JavaPlugin plugin;
    private static int i_schedulerId;


    public CommandHandler(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public boolean spigotSplitter( CommandSender sender, Command command, String label,  String[] args) {

        String s_message="";
        boolean b_abort = false; //if abort argument is set
        boolean b_comment = false; //local var
        String s_comment = ""; //comment string
        boolean b_silent=false; //if silent argument is set
        boolean b_time=false; //local var
        LocalDateTime ldt_time=LocalDateTime.now().plusMinutes(1);  //end time set, defaults to 1 minute
        String s_time=""; //just used internally (why declared though??)
        boolean b_whitelist=false; //if whitelist argument is set
        boolean b_okay = true; //if argument chain is valid

        if (args.length == 0) {
            //default action
        } else {
            //extract things from the thing
            if (!args[0].startsWith("-")) {
                b_okay = false;
            }
            outer:
            for (int i=0; i<args.length;i++ ) {
                switch (args[i]) {
                    case "":
                        break;
                    case "-a":
                    case "-abort":
                        if (args.length-1 == i) {
                            b_abort = true;
                            b_okay = true;
                        } else {
                            b_okay = false;
                        }
                        break outer;
                    case "-c":
                    case "-comment":
                        if (b_comment || (args.length-1==i)) { //when the flag is already set or no comment followed afterwards
                            b_okay = false;
                        }
                        if (!args[i+1].startsWith("\"")) { //when the next argument does not start with "
                            b_okay = false;
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
                                    s_comment=s_comment.concat(args[j]);
                                }
                            }
                            s_comment=s_comment.substring(1, s_comment.length()-1); //clear comment from ""
                        } else {
                            b_okay = false;
                        }
                        i=i_foundAt_0; //index i jumps to end of comment, because it won't cover it anyway
                        break;
                    case "-t":
                    case "-time":
                        if (b_time || (args.length-1==i)) {  //when the flag is already set or no time followed afterwards
                            b_okay = false;
                        }
                        //flag is set, time-String is set as the next one, index skippes next entry
                        b_time=true;
                        s_time=args[i+1];
                        i++;

                        if (s_time.contains(":")) { //if time is stated as at HH:MM, no need for ""
                            String[] as_time = s_time.split(":");
                            if (as_time.length!=2) {
                                b_okay = false;
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
                                b_okay = false;
                            }
                        } else { //if time is stated as "in X minutes"
                            try {
                                if (Integer.parseInt(s_time) > 1440 || Integer.parseInt(s_time) < 0) {
                                    b_okay = false;
                                }
                                ldt_time= LocalDateTime.now().plusMinutes(Integer.parseInt(s_time));
                            } catch (java.lang.NumberFormatException | java.time.DateTimeException e) {
                                b_okay = false;
                            }
                        }
                        break;
                    case "-w":
                    case "--whitelist":
                        if (!b_whitelist) {
                            b_whitelist=true;
                            break;
                        } else {
                            b_okay = false;
                        }
                    case "-s":
                    case "--silent":
                        if (!b_silent) {
                            b_silent=true;
                            break;
                        } else {
                            b_okay = false;
                        }
                    default:
                        b_okay = false;
                }

                if (!b_okay) {
                    break;
                }
            }
        }

        if (b_okay) {
            if (b_abort) { //if schedule has to be aborted
                if (Bukkit.getScheduler().isQueued(i_schedulerId)) {
                    Bukkit.getScheduler().cancelTask(i_schedulerId);
                    s_message = "§4[ShountDown] §rSchedule aborted";
                    //send a message to player and log
                    Bukkit.broadcastMessage(s_message+".");
                    Bukkit.getLogger().log(Level.INFO,s_message + " by Player "+sender.getName()+".");
                    return true;
                } else {
                    sender.sendMessage("§4[ShountDown] §rThere is no schedule to abort...");
                    return true;
                }
            } else {
                if (Bukkit.getScheduler().isQueued(i_schedulerId)) { //if scheduler is already running
                    s_message = "[ShountDown] A schedule is already running! Please abort the other before running this command again.";
                    sender.sendMessage(s_message);
                    return true;
                } else {
                    //create new scheduler and let it run
                    int i_type = -1;
                    String s_shutdownMessage ="";

                    if (label.contentEquals("shutdown")) {
                        i_type = 0;
                        s_shutdownMessage = "Server shuts down";
                    }
                    if (label.contentEquals("reboot")) {
                        i_type = 1;
                        s_shutdownMessage = "Server reboots";
                    }




                    Shutdown sd = new Shutdown(b_whitelist,s_comment,i_type);
                    Scheduler scheduler = new Scheduler (this.plugin,ldt_time, s_shutdownMessage, sd);
                    scheduler.setSilent(b_silent);
                    scheduler.runTaskTimer(this.plugin, 10, 10);
                    i_schedulerId = scheduler.getTaskId();
                    s_message = "§4[ShountDown] §rScheduled shutdown at " + String.format ("%02d", ldt_time.getHour()) + ":" + String.format ("%02d", ldt_time.getMinute());
                    if (!b_silent) Bukkit.broadcastMessage(s_message);
                    Bukkit.getLogger().log(Level.WARNING,s_message + " by Player " + sender.getName());

                    return true;
                }
            }
        } else {
            return false;
        }
    }
}
