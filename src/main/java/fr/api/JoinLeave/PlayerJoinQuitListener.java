package fr.api.JoinLeave;

import fr.api.basededonnees.SQLiteManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerJoinQuitListener implements Listener {
    private final SQLiteManager sqliteManager;

    public PlayerJoinQuitListener(SQLiteManager sqliteManager) {
        this.sqliteManager = sqliteManager;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        PlayerManager.updatePlayerTable(sqliteManager, event.getPlayer(), "connected");
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        PlayerManager.updatePlayerTable(sqliteManager, event.getPlayer(), "disconnect");
    }
}
