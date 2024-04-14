package fr.api.InventoryManager;

import fr.api.basededonnees.SQLiteManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.Material;
import org.bukkit.inventory.PlayerInventory;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;

public class InventoryManager {

    private static SQLiteManager sqliteManager;

    public InventoryManager(SQLiteManager sqliteManager) {
        InventoryManager.sqliteManager = sqliteManager;
    }

    public static void savePlayerItems(UUID playerId, List<ItemStack> items) {
        try {
            PreparedStatement ps = sqliteManager.getConnection().prepareStatement(
                    "INSERT INTO player_items (player_uuid, item_data, amount, player_name) VALUES (?, ?, ?, ?) " +
                            "ON CONFLICT(player_uuid, item_data) DO UPDATE SET amount = amount + ?");
            ps.setString(1, playerId.toString());

            for (ItemStack item : items) {
                String itemName = getItemName(item);
                ps.setString(2, itemName);
                ps.setInt(3, item.getAmount());
                ps.setInt(4, item.getAmount());
                ps.addBatch();
            }

            ps.executeBatch();
            ps.close();
        } catch (SQLException e) {
            sqliteManager.getLogger().log(Level.SEVERE, "Erreur lors de la sauvegarde des objets du joueur dans la base de données", e);
        }
    }

    private static String getItemName(ItemStack item) {
        if (item.hasItemMeta() && item.getItemMeta().hasDisplayName()) {
            return item.getItemMeta().getDisplayName();
        } else {
            return item.getType().name();
        }
    }

    public List<ItemStack> loadPlayerItems(UUID playerUUID) {
        List<ItemStack> items = new ArrayList<>();

        try (PreparedStatement statement = sqliteManager.getConnection().prepareStatement(
                "SELECT item_data FROM player_items WHERE player_uuid = ?"
        )) {
            statement.setString(1, playerUUID.toString());
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                String itemData = resultSet.getString("item_data");
                ItemStack item = deserializeItem(itemData);
                items.add(item);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return items;
    }

    public void updatePlayerInventory(UUID playerUUID, Inventory inventory) {
        String inventoryData = serializeInventory(inventory);

        try (PreparedStatement statement = sqliteManager.getConnection().prepareStatement(
                "UPDATE player_inventories SET inventory_data = ? WHERE player_uuid = ?"
        )) {
            statement.setString(1, inventoryData);
            statement.setString(2, playerUUID.toString());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private ItemStack deserializeItem(String itemData) {

        return new ItemStack(Material.STONE);
    }

    private String serializeInventory(Inventory inventory) {

        return "";
    }

    private Inventory deserializeInventory(String inventoryData) {
        // Implémentez ici votre logique de désérialisation de l'inventaire
        // Utilisez une bibliothèque comme Gson si nécessaire
        // Placeholder, remplacez par la logique de désérialisation réelle
        return Bukkit.createInventory(null, 27, "DefaultInventory");
    }

    public void exempleUtilisation(Player player) {
        UUID playerUUID = player.getUniqueId();
        Inventory playerInventory = player.getInventory();

        updatePlayerInventory(playerUUID, playerInventory);
        player.sendMessage("Inventaire sauvegardé avec succès !");

        savePlayerItems(playerUUID, List.of(playerInventory.getContents()));

        // Chargement de l'inventaire
        List<ItemStack> playerItems = loadPlayerItems(playerUUID);
        PlayerInventory loadedInventory = player.getInventory();

        loadedInventory.clear();

        for (ItemStack item : playerItems) {
            loadedInventory.addItem(item);
        }

        player.sendMessage("Inventaire chargé avec succès !");
    }
}
