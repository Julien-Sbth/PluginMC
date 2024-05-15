package fr.api.shop;

import fr.api.basededonnees.SQLiteManager;
import fr.api.menu.ShopManager;
import org.bukkit.Material;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

import static fr.api.menu.Menu.getPrice;

public class ShopADDItem {
    private final SQLiteManager sqliteManager;
    private final ShopManager shopManager;

    public ShopADDItem(SQLiteManager sqliteManager, ShopManager shopManager) {
        this.sqliteManager = sqliteManager;
        this.shopManager = shopManager;
    }

    private final List<Material> displayedItems = Arrays.asList(
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
            Material.POTION,
            Material.DIRT,
            Material.STONE_SWORD
    );

    public void addToShop() {
        for (Material material : displayedItems) {
            int price = getPrice(material);
            if (!isItemAlreadyInShop(material)) {
                shopManager.addItemToShop(material, 1, price);
            }
        }
    }

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
                    return count > 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
