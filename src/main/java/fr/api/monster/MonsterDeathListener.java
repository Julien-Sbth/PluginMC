package fr.api.monster;

import fr.api.basededonnees.SQLiteManager;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MonsterDeathListener implements Listener {
    private double money = 0;
    private static SQLiteManager sqliteManager;
    private final Logger logger;
    private final List<String> monsterTypes;

    public MonsterDeathListener(SQLiteManager sqliteManager, Logger logger, List<String> monsterTypes) {
        MonsterDeathListener.sqliteManager = sqliteManager;
        this.logger = logger;
        this.monsterTypes = monsterTypes;
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        LivingEntity entity = event.getEntity();

        if (!(entity instanceof Player) && entity.getKiller() instanceof Player) {
            Player player = entity.getKiller();
            UUID playerId = player.getUniqueId();
            String entityType = entity.getType().name();
            String playerName = player.getName();
            String getImageBase64 = getImageBase64(entityType); // Correction
            List<String> monsterTypes = Arrays.asList("ZOMBIE", "CREEPER", "SKELETON", "SPIDER", "ENDERMAN", "WITCH", "BLAZE", "SLIME", "GHAST", "WITHER", "ENDER_DRAGON", "GUARDIAN", "PIG_ZOMBIE", "CREEPER", "SILVERFISH", "WITHER_SKELETON", "ENDERMITES", "STRAY", "PHANTOM", "DROWNED", "SHULKER", "VEX", "EVOKER", "ILLUSIONER", "VINDICATOR", "PILLAGER", "RAVAGER", "HOGLIN", "ZOGLIN", "PIGLIN", "STRIDER", "WARDEN");
            List<String> animalTypes = Arrays.asList("CHICKEN", "COW", "PIG", "SHEEP", "WOLF", "OCELOT", "HORSE", "RABBIT", "BAT", "POLAR_BEAR", "LLAMA", "PARROT", "DOLPHIN", "CAT", "TURTLE", "FOX", "BEE", "COD", "SALMON", "TROPICAL_FISH"); // Removed "PIG"

            if (monsterTypes.contains(entityType) || animalTypes.contains(entityType)) {
                int moneyEarned = 10;
                if (animalTypes.contains(entityType)) {
                    moneyEarned /= 2;
                }

                money += moneyEarned;

                savePlayerCoins(playerId, money, playerName);
                saveEntityKill(playerId, entityType, playerName, getImageBase64); // Correction

                Material monsterHead = null;
                if (isMonsterHead(entityType)) {
                    monsterHead = getMonsterHead(entity.getType());
                }

                player.sendMessage(entityType + " tué !");
                if (monsterHead != null) {
                }
            }
        }
    }

    private boolean isMonsterHead(String entityType) {
        Material[] monsterHeads = {
                Material.ZOMBIE_HEAD,
                Material.CREEPER_HEAD,
                Material.SKELETON_SKULL,
                Material.WITHER_SKELETON_SKULL,
                Material.DRAGON_HEAD,
                Material.PLAYER_HEAD,
                Material.CREEPER_HEAD,
                Material.DRAGON_HEAD,
                Material.PLAYER_HEAD,
        };

        for (Material head : monsterHeads) {
            if (head.name().equals(entityType)) {
                return true;
            }
        }
        return false;
    }

    private Material getMonsterHead(EntityType entityType) {
        Material[] monsterHeads = {
                Material.ZOMBIE_HEAD,
                Material.CREEPER_HEAD,
                Material.SKELETON_SKULL,
                Material.WITHER_SKELETON_SKULL,
                Material.DRAGON_HEAD,
                Material.PIGLIN_HEAD
        };

        for (int i = 0; i < monsterTypes.size(); i++) {
            if (monsterTypes.get(i).equals(entityType.name())) {
                return monsterHeads[i];
            }
        }
        return null;
    }

    public static void savePlayerCoins(UUID playerId, double coins, String playerName) {
        try {
            PreparedStatement selectPs = sqliteManager.getConnection().prepareStatement(
                    "SELECT player_id FROM PlayerCoins WHERE player_id = ?");
            selectPs.setString(1, playerId.toString());
            ResultSet resultSet = selectPs.executeQuery();

            if (resultSet.next()) {
                PreparedStatement updatePs = sqliteManager.getConnection().prepareStatement(
                        "UPDATE PlayerCoins SET coins = ?, player_name = ? WHERE player_id = ?");
                updatePs.setDouble(1, coins);
                updatePs.setString(2, playerName);
                updatePs.setString(3, playerId.toString());
                updatePs.executeUpdate();
                updatePs.close();
            } else {
                PreparedStatement insertPs = sqliteManager.getConnection().prepareStatement(
                        "INSERT INTO PlayerCoins (player_id, coins, player_name) VALUES (?, ?, ?)");
                insertPs.setString(1, playerId.toString());
                insertPs.setDouble(2, coins);
                insertPs.setString(3, playerName);
                insertPs.executeUpdate();
                insertPs.close();
            }

            resultSet.close();
            selectPs.close();
        } catch (SQLException e) {
            sqliteManager.getLogger().log(Level.SEVERE, "Erreur lors de la sauvegarde des coins du joueur dans la base de données", e);
        }
    }

    private void saveEntityKill(UUID playerId, String entityType, String playerName, String getImageBase64) {
        try {
            PreparedStatement ps = sqliteManager.getConnection().prepareStatement(
                    "INSERT INTO PlayerKills (player_id, entity_type, kills, player_name, kills_monster) VALUES (?, ?, 1, ?, ?)" +
                            "ON CONFLICT(player_id, entity_type, player_name, kills_monster) DO UPDATE SET kills = kills + 1");
            ps.setString(1, playerId.toString());
            ps.setString(2, entityType);
            ps.setString(3, playerName);
            ps.setString(4, getImageBase64);
            ps.executeUpdate();
            ps.close();
        } catch (SQLException e) {
            sqliteManager.getLogger().log(Level.SEVERE, "Erreur lors de l'enregistrement de la tueries dans la base de données", e);
        }
    }

    public static int getMonsterKills(Player player, String entityType) {
        int kills = 0;
        try {
            PreparedStatement ps = sqliteManager.getConnection().prepareStatement(
                    "SELECT kills FROM PlayerKills WHERE player_id = ? AND entity_type = ?");
            ps.setString(1, player.getUniqueId().toString());
            ps.setString(2, entityType);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                kills = rs.getInt("kills");
            }
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return kills;
    }

    public static double getPlayerCoins(UUID playerId) {
        double coins = 0.0;
        try {
            PreparedStatement ps = sqliteManager.getConnection().prepareStatement(
                    "SELECT coins FROM PlayerCoins WHERE player_id = ?");
            ps.setString(1, playerId.toString());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                coins = rs.getDouble("coins");
            }
            rs.close();
            ps.close();
        } catch (SQLException e) {
            sqliteManager.getLogger().log(Level.SEVERE, "Erreur lors de la récupération des coins du joueur dans la base de données", e);
        }
        return coins;
    }
    private String getImageBase64(String nomItem) {
        String dossierImages = "kills" + File.separator;

        String imageBase64 = "";

        try {
            Path imagePath = Paths.get(dossierImages + nomItem + ".png");
            byte[] imageBytes = Files.readAllBytes(imagePath);
            imageBase64 = Base64.getEncoder().encodeToString(imageBytes);
        } catch (IOException e) {
            logger.severe("Erreur lors de la lecture de l'image : " + e.getMessage());
        }

        return imageBase64;
    }
}
