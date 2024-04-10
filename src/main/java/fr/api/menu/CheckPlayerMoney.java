package fr.api.menu;

import org.bukkit.entity.Player;

import org.bukkit.plugin.java.JavaPlugin;
import fr.api.monster.MonsterDeathListener;

import java.util.UUID;

public class CheckPlayerMoney {

    private final JavaPlugin plugin;

    public CheckPlayerMoney(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public boolean hasEnoughMoney(Player player, int amount) {
        double playerCoins = getPlayerCoins(player.getUniqueId());
        return playerCoins >= amount;
    }
    private double getPlayerCoins(UUID playerId) {
        return MonsterDeathListener.getPlayerCoins(playerId);
    }
}
