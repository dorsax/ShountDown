package de.dorsax.ShountDown.Bungee;

import net.md_5.bungee.api.plugin.Plugin;

public class MainBungee extends Plugin {
    @Override
    public void onEnable() {
        getProxy().getPluginManager().registerCommand(this, new CommandShutdown(this));
    }
}
