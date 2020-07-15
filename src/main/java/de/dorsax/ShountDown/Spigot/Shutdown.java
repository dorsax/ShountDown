//Done: Shutdown einbauen
//Done: kick einbauen bevor der Server heruntergefahren wird (?)
//Done: konfigurierbare Kick-Nachricht
//TODO: Add Reboot


package de.dorsax.ShountDown.Spigot;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class Shutdown {
	
	boolean b_whitelist;
	int i_type;
	String s_comment;
	
	public Shutdown (boolean b_whitelist, String s_comment, int i_type) {
    	
        if (!s_comment.isEmpty()) {
        	this.s_comment= ": " + s_comment;
        } else {
        	this.s_comment="!";
        }
        this.b_whitelist = b_whitelist;
        this.i_type = i_type;
	}
	
	public void run () {
		List<Player> list = new ArrayList<>(Bukkit.getOnlinePlayers());
        String s_message ="";
		if (i_type==1) {
            s_message="reboot";
        }
		if (i_type ==0){
		    s_message="shutdown";
        }
        if (s_message.contentEquals("")) {
            Bukkit.getLogger().log(Level.SEVERE,"Shountdown: Error in Class Shutdown: No mode declared!");
        }
		s_message= "Scheduled "+s_message;

        for (Player toKick : list) {
            toKick.kickPlayer(s_message + this.s_comment);
        }
        if (b_whitelist) {
        	Bukkit.setWhitelist(!Bukkit.hasWhitelist());
        }

        if (i_type==1) {
            Bukkit.getServer().spigot().restart();
        }
        if (i_type ==0){
            Bukkit.getServer().shutdown();
        }
	}
}
