package fr.api.pluginmc;

import fr.api.Listener.Listeners;
import fr.api.commands.OpenCommand;
import org.bukkit.plugin.java.JavaPlugin;

public final class VaultPlugin extends JavaPlugin {

    private static VaultPlugin plugin;

    @Override
    public void onEnable() {
        // Plugin startup logic

        plugin = this;

        getCommand("vault").setExecutor(new OpenCommand());

        getServer().getPluginManager().registerEvents(new Listeners(), this);

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public static VaultPlugin getPlugin() {
        return plugin;
    }
}

