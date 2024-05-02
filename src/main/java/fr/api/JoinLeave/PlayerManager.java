package fr.api.JoinLeave;

import fr.api.basededonnees.SQLiteManager;
import org.bukkit.entity.Player;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class PlayerManager {
    private final SQLiteManager sqliteManager;

    public PlayerManager(SQLiteManager sqliteManager) {
        this.sqliteManager = sqliteManager;
    }

    public static void updatePlayerTable(SQLiteManager sqliteManager, Player player, int online) {
        Connection conn = sqliteManager.getConnection();

        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            stmt = conn.prepareStatement("CREATE TABLE IF NOT EXISTS players (uuid TEXT PRIMARY KEY, name TEXT, online INTEGER)");
            stmt.executeUpdate();

            UUID uuid = player.getUniqueId();
            String name = player.getName();

            stmt = conn.prepareStatement("SELECT * FROM players WHERE uuid = ?");
            stmt.setString(1, uuid.toString());
            rs = stmt.executeQuery();

            if (rs.next()) {
                stmt = conn.prepareStatement("UPDATE players SET name = ?, online = ? WHERE uuid = ?");
                stmt.setString(1, name);
                stmt.setInt(2, online);
                stmt.setString(3, uuid.toString());
                stmt.executeUpdate();
            } else {
                stmt = conn.prepareStatement("INSERT INTO players (uuid, name, online) VALUES (?, ?, ?)");
                stmt.setString(1, uuid.toString());
                stmt.setString(2, name);
                stmt.setInt(3, online);
                stmt.executeUpdate();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            // Fermer les ressources
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void playerJoined(Player player) {
        updatePlayerTable(sqliteManager, player, 1);
    }

    // Appelé lors de la déconnexion d'un joueur
    public void playerLeft(Player player) {
        updatePlayerTable(sqliteManager, player, 0);
    }


}
