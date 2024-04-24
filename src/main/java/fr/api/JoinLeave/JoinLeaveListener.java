package fr.api.JoinLeave;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class JoinLeaveListener implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();

        if (player.hasPlayedBefore()) {
            player.sendMessage(ChatColor.GREEN + "Welcome " + ChatColor.YELLOW + "" + ChatColor.BOLD + player.getDisplayName());
        } else {
            e.setJoinMessage(ChatColor.BLUE + "" + ChatColor.BOLD + player.getDisplayName() + ", " + ChatColor.BLUE + "Welcome back !");
        }
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent e) {

        Player player = e.getPlayer();

        e.setQuitMessage(ChatColor.YELLOW + "" + ChatColor.BOLD + player.getDisplayName() + "has left" );
    }
}
