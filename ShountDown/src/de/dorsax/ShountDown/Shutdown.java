//Done: Shutdown einbauen
//Done: kick einbauen bevor der Server heruntergefahren wird (?)
//Done: konfigurierbare Kick-Nachricht
//TODO: Add Reboot


package de.dorsax.ShountDown;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class Shutdown {
	
	boolean b_whitelist;
	String s_comment;
	
	public Shutdown (boolean b_whitelist, String s_comment) {
    	
        if (!s_comment.isEmpty()) {
        	this.s_comment= ": " + s_comment;
        } else {
        	this.s_comment="!";
        }
        this.b_whitelist = b_whitelist;
	}
	
	public void run () {
		List<Player> list = new ArrayList<>(Bukkit.getOnlinePlayers());
        Iterator<Player> playerIterator = list.iterator();

        while (playerIterator.hasNext()) {
            Player toKick = playerIterator.next();
            toKick.kickPlayer("Scheduled shutdown"+this.s_comment);
        }
        if (b_whitelist) {
        	Bukkit.setWhitelist(!Bukkit.hasWhitelist());
        }
        
        Bukkit.getServer().shutdown();
	}
}
