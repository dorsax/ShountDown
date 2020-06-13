package de.dorsax.ShountDown;

import com.sun.istack.internal.NotNull;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.time.LocalDateTime;
import java.util.List;
import java.util.logging.Level;

public class CommandReboot implements CommandExecutor {
    private final JavaPlugin plugin;
    private Scheduler scheduler;
    private static int i_schedulerId;

    public CommandReboot(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public boolean onCommand(@NotNull CommandSender sender, Command command, String label, @NotNull String[] args) {

        CommandHandler ch = new CommandHandler(this.plugin);
        return ch.spigotSplitter(sender,command,label,args);

    }
}
