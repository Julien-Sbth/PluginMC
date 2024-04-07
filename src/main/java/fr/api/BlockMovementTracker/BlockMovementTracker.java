package fr.api.BlockMovementTracker;

import fr.api.basededonnees.SQLiteManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class BlockMovementTracker implements Listener {

    private SQLiteManager sqliteManager;
    private Map<UUID, Integer> blocksMovedMap;
    private BukkitTask saveTask;

    public BlockMovementTracker(SQLiteManager sqliteManager, JavaPlugin plugin) {
        this.sqliteManager = sqliteManager;
        blocksMovedMap = new HashMap<>();

        saveTask = Bukkit.getScheduler().runTaskTimer(plugin, this::saveAllMovementsToDatabase, 300, 300);
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        UUID playerId = player.getUniqueId();

        if (event.getFrom().getBlockX() != event.getTo().getBlockX() ||
                event.getFrom().getBlockY() != event.getTo().getBlockY() ||
                event.getFrom().getBlockZ() != event.getTo().getBlockZ()) {

            int blocksMoved = blocksMovedMap.getOrDefault(playerId, 0);
            blocksMoved++;
            blocksMovedMap.put(playerId, blocksMoved);

            if (blocksMoved != 1) {
                System.out.println(player.getName() + " a parcouru " + blocksMoved + " blocs.");
            }
        }
    }

    private void saveAllMovementsToDatabase() {
        for (Map.Entry<UUID, Integer> entry : blocksMovedMap.entrySet()) {
            UUID playerId = entry.getKey();
            int blocksMoved = entry.getValue();
            savePlayerMovementToDatabase(playerId, blocksMoved);
        }
        blocksMovedMap.clear();
    }

    private void savePlayerMovementToDatabase(UUID playerId, int blocksMoved) {
        Connection connection = null;
        PreparedStatement statement = null;
        PreparedStatement insertStatement = null;
        PreparedStatement updateStatement = null;
        ResultSet resultSet = null;

        try {
            connection = sqliteManager.getConnection();
            if (connection == null || connection.isClosed()) {
                Bukkit.getLogger().warning("La connexion à la base de données est nulle ou fermée.");
                return;
            }

            statement = connection.prepareStatement("SELECT * FROM player_movement WHERE player_id = ?");
            insertStatement = connection.prepareStatement("INSERT INTO player_movement (player_id, blocks_moved) VALUES (?, ?)");
            updateStatement = connection.prepareStatement("UPDATE player_movement SET blocks_moved = blocks_moved + ? WHERE player_id = ?");

            statement.setString(1, playerId.toString());
            resultSet = statement.executeQuery();

            boolean playerExists = resultSet.next();

            if (playerExists) {
                updateStatement.setInt(1, blocksMoved);
                updateStatement.setString(2, playerId.toString());
                updateStatement.executeUpdate();
            } else {
                insertStatement.setString(1, playerId.toString());
                insertStatement.setInt(2, blocksMoved);
                insertStatement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (resultSet != null) resultSet.close();
                if (statement != null) statement.close();
                if (insertStatement != null) insertStatement.close();
                if (updateStatement != null) updateStatement.close();

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}