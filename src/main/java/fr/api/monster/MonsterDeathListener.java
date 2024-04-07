package fr.api.monster;

import fr.api.basededonnees.SQLiteManager;
import org.bukkit.entity.Player;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;

public class MonsterDeathListener implements Listener {
    private double money = 0;
    public static SQLiteManager sqliteManager;

    public MonsterDeathListener(SQLiteManager sqliteManager) {
        MonsterDeathListener.sqliteManager = sqliteManager;
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        LivingEntity entity = event.getEntity();

        if (!(entity instanceof Player) && entity.getKiller() instanceof Player) {
            Player player = entity.getKiller();
            UUID playerId = player.getUniqueId();
            String entityType = entity.getType().name();

            List<String> monsterTypes = Arrays.asList("ZOMBIE", "CREEPER", "SKELETON", "SPIDER", "ENDERMAN", "WITCH", "BLAZE", "SLIME", "GHAST", "WITHER", "ENDER_DRAGON", "GUARDIAN", "PIG_ZOMBIE", "CREEPER", "SILVERFISH", "WITHER_SKELETON", "ENDERMITES", "STRAY", "PHANTOM", "DROWNED", "SHULKER", "VEX", "EVOKER", "ILLUSIONER", "VINDICATOR", "PILLAGER", "RAVAGER", "HOGLIN", "ZOGLIN", "PIGLIN", "STRIDER", "WARDEN");

            List<String> animalTypes = Arrays.asList("CHICKEN", "COW", "PIG", "SHEEP", "WOLF", "OCELOT", "HORSE", "RABBIT", "BAT", "POLAR_BEAR", "LLAMA", "PARROT", "DOLPHIN", "CAT", "TURTLE", "FOX", "BEE", "PIG", "COD", "SALMON", "TROPICAL_FISH");

            if (monsterTypes.contains(entityType) || animalTypes.contains(entityType)) {
                int moneyEarned = 10;
                if (animalTypes.contains(entityType)) {
                    moneyEarned /= 2;
                }

                money += moneyEarned;

                savePlayerCoins(playerId, money);
                saveEntityKill(playerId, entityType);
                player.sendMessage(entityType + " tué !");
            }
        }
    }

    public static void savePlayerCoins(UUID playerId, double coins) {
        try {
            PreparedStatement selectPs = sqliteManager.getConnection().prepareStatement(
                    "SELECT player_id FROM PlayerCoins WHERE player_id = ?");
            selectPs.setString(1, playerId.toString());
            ResultSet resultSet = selectPs.executeQuery();

            if (resultSet.next()) {
                PreparedStatement updatePs = sqliteManager.getConnection().prepareStatement(
                        "UPDATE PlayerCoins SET coins = ? WHERE player_id = ?");
                updatePs.setDouble(1, coins);
                updatePs.setString(2, playerId.toString());
                updatePs.executeUpdate();
                updatePs.close();
            } else {
                PreparedStatement insertPs = sqliteManager.getConnection().prepareStatement(
                        "INSERT INTO PlayerCoins (player_id, coins) VALUES (?, ?)");
                insertPs.setString(1, playerId.toString());
                insertPs.setDouble(2, coins);
                insertPs.executeUpdate();
                insertPs.close();
            }

            resultSet.close();
            selectPs.close();
        } catch (SQLException e) {
            sqliteManager.getLogger().log(Level.SEVERE, "Erreur lors de la sauvegarde des coins du joueur dans la base de données", e);
        }
    }

    private void saveEntityKill(UUID playerId, String entityType) {
        try {
            PreparedStatement ps = sqliteManager.getConnection().prepareStatement(
                    "INSERT INTO PlayerKills (player_id, entity_type, kills) VALUES (?, ?, 1)" +
                            "ON CONFLICT(player_id, entity_type) DO UPDATE SET kills = kills + 1");
            ps.setString(1, playerId.toString());
            ps.setString(2, entityType);
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
}