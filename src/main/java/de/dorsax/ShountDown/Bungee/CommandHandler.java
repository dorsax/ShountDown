//TODO: Add a Whitelist module, turn the splitting part on again!

package de.dorsax.ShountDown.Bungee;


import net.md_5.bungee.api.CommandSender;
import java.time.LocalDateTime;
import java.util.logging.Level;

public class CommandHandler {

    String s_message=""; //is it needed though?
    boolean b_abort = false; //if abort argument is set
    String s_comment = ""; //comment string
    boolean b_silent=false; //if silent argument is set
    LocalDateTime ldt_time=LocalDateTime.now().plusMinutes(1);  //end time set, defaults to 1 minute
    boolean b_whitelist=false; //if whitelist argument is set
    boolean b_okay = true; //if argument chain is valid

    public CommandHandler() {
        this.s_message="";
        this.b_abort = false;
        this.s_comment = "";
        this.b_silent=false;
        this.ldt_time=LocalDateTime.now().plusMinutes(1);
        this.b_whitelist=false;
        this.b_okay = true;
    }

    public void bungeeSplitter(String[] args) {

        boolean b_comment = false; //local var
        boolean b_time=false; //local var
        String s_time=""; //just used internally

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
                            } catch (NumberFormatException | java.time.DateTimeException e) {
                                b_okay = false;
                            }
                        } else { //if time is stated as "in X minutes"
                            try {
                                if (Integer.parseInt(s_time) > 1440 || Integer.parseInt(s_time) < 0) {
                                    b_okay = false;
                                }
                                ldt_time= LocalDateTime.now().plusMinutes(Integer.parseInt(s_time));
                            } catch (NumberFormatException | java.time.DateTimeException e) {
                                b_okay = false;
                            }
                        }
                        break;
                    /* commented out because at this point (0.10.0) we do not have any module implementing a whitelist!
                    case "-w":
                    case "--whitelist":
                        if (!b_whitelist) {
                            b_whitelist=true;
                            break;
                        } else {
                            b_okay = false;
                        }
                    */
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
    }

    public String getMessage() {
        return s_message;
    }

    public boolean isAbort() {
        return b_abort;
    }

    public String getComment() {
        return s_comment;
    }

    public boolean isSilent() {
        return b_silent;
    }

    public LocalDateTime getTime() {
        return ldt_time;
    }

    public boolean isWhitelist() {
        return b_whitelist;
    }

    public boolean isOkay() {
        return b_okay;
    }
}
