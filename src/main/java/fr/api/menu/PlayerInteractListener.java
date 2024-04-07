package fr.api.menu;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

import static fr.api.menu.Menu.openCustomMenu;

public class PlayerInteractListener implements Listener {

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        if (event.getItem() != null && event.getItem().getType() == Material.EMERALD) {
            openCustomMenu(player);
        }
    }
}
