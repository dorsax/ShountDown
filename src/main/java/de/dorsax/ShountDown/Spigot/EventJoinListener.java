package de.dorsax.ShountDown.Spigot;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class EventJoinListener implements Listener {
    @EventHandler
    public void onPlayerJoin (PlayerJoinEvent event) {
        if (Scheduler.isRunning()) {
            Scheduler scheduler = Scheduler.getInstance();
            String time = String.format ("%02d", scheduler.getGoaltime().getHour()) + ":" + String.format ("%02d", scheduler.getGoaltime().getMinute());
            event.getPlayer().sendMessage(scheduler.getMessage() +" in approximately "+scheduler.getHours()+"h "+scheduler.getMinutes()+"min, at "+time+"!");
        }

    }
}
