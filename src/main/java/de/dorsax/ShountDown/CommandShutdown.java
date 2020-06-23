package de.dorsax.ShountDown;


import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

public class CommandShutdown implements CommandExecutor {
    private final JavaPlugin plugin;

    public CommandShutdown(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        CommandHandler ch = new CommandHandler(this.plugin);
        return ch.spigotSplitter(sender,command,label,args);
    }
}
