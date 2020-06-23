package de.dorsax.ShountDown.Spigot;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

public class CommandReboot implements CommandExecutor {
    private final JavaPlugin plugin;
    private Scheduler scheduler;
    private static int i_schedulerId;

    public CommandReboot(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public boolean onCommand( CommandSender sender, Command command, String label,  String[] args) {

        CommandHandler ch = new CommandHandler(this.plugin);
        return ch.spigotSplitter(sender,command,label,args);

    }
}
