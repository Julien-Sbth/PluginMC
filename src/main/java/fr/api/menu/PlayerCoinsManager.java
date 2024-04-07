package fr.api.menu;

import fr.api.basededonnees.SQLiteManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;
import java.util.logging.Level;

public class PlayerCoinsManager {

    private final SQLiteManager sqliteManager;
    private final JavaPlugin plugin;

    public PlayerCoinsManager(JavaPlugin plugin, SQLiteManager sqliteManager) {
        this.plugin = plugin;
        this.sqliteManager = sqliteManager;
    }

    public double getPlayerCoins(UUID playerId) {
        double coins = 0;

        try {
            PreparedStatement selectPs = sqliteManager.getConnection().prepareStatement(
                    "SELECT coins FROM PlayerCoins WHERE player_id = ?");
            selectPs.setString(1, playerId.toString());
            ResultSet resultSet = selectPs.executeQuery();

            if (resultSet.next()) {
                coins = resultSet.getDouble("coins");
            }

            resultSet.close();
            selectPs.close();
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Erreur lors de la récupération des coins du joueur depuis la base de données", e);
        }

        return coins;
    }
}
