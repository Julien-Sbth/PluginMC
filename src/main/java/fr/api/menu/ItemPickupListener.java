package fr.api.menu;

import fr.api.InventoryManager.InventoryManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;
import java.util.UUID;

public class ItemPickupListener implements Listener {

    @EventHandler
    public void onItemPickup(PlayerPickupItemEvent event) {
        Player player = event.getPlayer();
        UUID playerId = player.getUniqueId();
        ItemStack pickedItem = event.getItem().getItemStack();
        InventoryManager.savePlayerItems(playerId, Collections.singletonList(pickedItem));
    }
}
