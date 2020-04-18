package de.dorsax.ShountDown;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import java.util.logging.Level;

public class Main extends JavaPlugin{
	// Fired when plugin is first enabled
    @Override
    public void onEnable() {
        Bukkit.getLogger().log(Level.INFO,"[ShountDown] Plugin is active and waiting to run.");
    	this.getCommand("shutdown").setExecutor(new Commands(this));
    }
    // Fired when plugin is disabled
    @Override
    public void onDisable() {
    }
}