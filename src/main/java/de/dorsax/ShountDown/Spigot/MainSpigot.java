package de.dorsax.ShountDown.Spigot;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import java.util.logging.Level;

public class MainSpigot extends JavaPlugin{
	// Fired when plugin is first enabled
    @Override
    public void onEnable() {
        Bukkit.getLogger().log(Level.INFO,"[ShountDown] Plugin is active and waiting to run.");

        this.getCommand("shutdown").setExecutor(new CommandShutdown(this));
        this.getCommand("reboot").setExecutor(new CommandReboot(this));
    }
    // Fired when plugin is disabled
    @Override
    public void onDisable() {
    }
}