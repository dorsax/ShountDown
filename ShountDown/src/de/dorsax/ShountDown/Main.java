package de.dorsax.ShountDown;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin{
	// Fired when plugin is first enabled
    @Override
    public void onEnable() {
    	Bukkit.getConsoleSender().sendMessage("[ShountDown] Plugin is active and waiting to run.");
    	
    	this.getCommand("shutdown").setExecutor(new Commands(this));
    }
    // Fired when plugin is disabled
    @Override
    public void onDisable() {
    }
}