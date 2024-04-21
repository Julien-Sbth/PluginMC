package fr.api.pluginmc;

import fr.api.Block.BlockDestroyListener;
import fr.api.menu.Menu;
import fr.api.BlockMovementTracker.BlockMovementTracker;
import fr.api.advancement.AchievementListener;
import fr.api.api.APIClient;
import fr.api.menu.PlayerCoinsManager;
import fr.api.InventoryManager.InventoryListener;
import fr.api.artefact.ArtefactItemsListener;
import fr.api.basededonnees.SQLiteManager;
import fr.api.commands.Commands;
import fr.api.menu.ShopManager;
import fr.api.moneycommands.MoneyCommand;
import fr.api.monster.MonsterDeathListener;
import org.bukkit.Material;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static fr.api.menu.Menu.getPrice;

public class Main extends JavaPlugin implements Listener {
    private SQLiteManager sqliteManager;
    private ShopManager shopManager;
    private MonsterDeathListener monsterDeathListener;
    private APIClient apiClient;
    private PlayerCoinsManager playerCoinsManager;
    private Menu menu;

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
        getCommand("money").setExecutor(new MoneyCommand(monsterDeathListener));
        getServer().getPluginManager().registerEvents(new ArtefactItemsListener(), this);

        getCommand("test").setExecutor(new Commands());
        getCommand("alert").setExecutor(new Commands());
        getCommand("stats").setExecutor(new Commands());
        getCommand("shop").setExecutor(new Commands());

        Logger logger = Logger.getLogger("MenuLogger");

        sqliteManager = new SQLiteManager("database.sqlite");
        if (sqliteManager.isConnected()) {
            getLogger().info("Connexion à la base de données établie avec succès !");
        } else {
            getLogger().warning("La connexion à la base de données a échoué !");
        }
        menu = new Menu(this, sqliteManager, logger);

        menu.registerEvents();
        Listener inventoryListener = new InventoryListener(sqliteManager, getLogger());

        getServer().getPluginManager().registerEvents(inventoryListener, this);
        apiClient = new APIClient(sqliteManager);
        getServer().getPluginManager().registerEvents(new BlockDestroyListener(sqliteManager), this);
        List<Material> displayedItems = Arrays.asList(
                Material.NETHERITE_SWORD, Material.NETHERITE_BOOTS, Material.DIAMOND_SWORD,
                Material.GOLDEN_BOOTS, Material.LEATHER_BOOTS, Material.WOODEN_SWORD,
                Material.IRON_SWORD, Material.GOLDEN_SWORD, Material.NETHERITE_LEGGINGS,
                Material.NETHERITE_CHESTPLATE, Material.LEATHER_LEGGINGS, Material.WOODEN_SWORD,
                Material.GOLDEN_SWORD, Material.NETHERITE_HELMET,
                Material.IRON_HELMET, Material.GOLDEN_HELMET, Material.DIAMOND_HELMET,
                Material.LEATHER_HELMET, Material.NETHERITE_HELMET, Material.LEATHER_LEGGINGS,
                Material.NETHERITE_PICKAXE, Material.DIAMOND_PICKAXE, Material.DIAMOND_AXE,
                Material.NETHERITE_AXE, Material.IRON_AXE, Material.GOLDEN_AXE,
                Material.WOODEN_AXE, Material.STONE_AXE, Material.BOW,
                Material.IRON_BOOTS, Material.DIAMOND_BOOTS, Material.DIAMOND_CHESTPLATE,
                Material.GOLDEN_CHESTPLATE, Material.LEATHER_CHESTPLATE, Material.IRON_CHESTPLATE,
                Material.DIAMOND_LEGGINGS, Material.IRON_LEGGINGS, Material.GOLDEN_LEGGINGS, Material.SHIELD,
                Material.IRON_PICKAXE, Material.WOODEN_PICKAXE, Material.STONE_PICKAXE, Material.RED_STAINED_GLASS,
                Material.DIAMOND_PICKAXE,
                Material.NETHERITE_SWORD,
                Material.FISHING_ROD,
                Material.POTION
        );

        for (Material material : displayedItems) {
            int price = getPrice(material);
            if (!isItemAlreadyInShop(material)) {
                shopManager.addItemToShop(material, 1, price);
            }
        }

        ShopManager shopManager = new ShopManager(sqliteManager);
        apiClient.sendPlayerDataToSite();

        getServer().getPluginManager().registerEvents(new MonsterDeathListener(sqliteManager, getLogger(), List.of("")), this);
        getServer().getPluginManager().registerEvents(new BlockMovementTracker(sqliteManager, this), this);
        getServer().getPluginManager().registerEvents(new AchievementListener(sqliteManager), this);
        playerCoinsManager = new PlayerCoinsManager(this, sqliteManager);
        playerCoinsManager = new PlayerCoinsManager(this, sqliteManager);

        BlockMovementTracker movementTracker = new BlockMovementTracker(sqliteManager, this);
        getServer().getPluginManager().registerEvents(movementTracker, this);
        restaurerInventaire();
    }

    // Fonction pour vérifier si un item est déjà présent dans la boutique
    private boolean isItemAlreadyInShop(Material material) {
        try {
            if (!sqliteManager.isConnected()) {
                throw new SQLException("La connexion à la base de données est fermée.");
            }

            try (PreparedStatement selectPs = sqliteManager.getConnection().prepareStatement(
                    "SELECT COUNT(*) AS count FROM Shop WHERE item_name = ?")) {
                selectPs.setString(1, material.toString());
                ResultSet resultSet = selectPs.executeQuery();
                if (resultSet.next()) {
                    int count = resultSet.getInt("count");
                    return count > 0; // Retourne true si l'item est déjà présent, sinon false
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false; // En cas d'erreur, retourne false par défaut
    }


    @Override
    public void onDisable() {
        if (sqliteManager != null) {
            sqliteManager.closeConnection();
        }
        getLogger().info("Le Plugin MC vient de s'éteindre !");
    }

    private void restaurerInventaire() {
        InventoryListener inventoryListener = new InventoryListener(sqliteManager, getLogger());
    }
}
