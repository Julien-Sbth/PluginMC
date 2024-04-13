package fr.api.advancement;

import fr.api.basededonnees.SQLiteManager;
import org.bukkit.Bukkit;
import org.bukkit.advancement.Advancement;
import org.bukkit.advancement.AdvancementProgress;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerAdvancementDoneEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.logging.Level;

public class AchievementListener implements Listener {
    private final SQLiteManager sqliteManager;

    public AchievementListener(SQLiteManager sqliteManager) {
        this.sqliteManager = sqliteManager;
    }

    @EventHandler
    public void onPlayerAdvancementDone(PlayerAdvancementDoneEvent event) {
        Player player = event.getPlayer();
        Advancement advancement = event.getAdvancement();
        String playerName = player.getName();
        String advancementId = advancement.getKey().toString();

        insertAchievement(playerName, advancementId);
        Bukkit.getLogger().info(playerName + " a réalisé l'achievement : " + advancement.getKey());
    }
    private void insertAchievement(String playerName, String achievementId) {
        if (sqliteManager.isConnected()) {
            Connection connection = sqliteManager.getConnection();
            String sql = "INSERT INTO player_achievements (player_id, player_name, achievements) VALUES (?, ?, ?)";
            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                pstmt.setString(1, playerName);
                pstmt.setString(2, playerName);
                pstmt.setString(3, achievementId);
                pstmt.executeUpdate();
            } catch (SQLException e) {
                sqliteManager.getLogger().log(Level.SEVERE, "Erreur lors de l'insertion du succès dans la base de données", e);
            }
        } else {
            Bukkit.getLogger().warning("La connexion à la base de données SQLite est fermée.");
        }
    }
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        Iterator<Advancement> advancements = Bukkit.getServer().advancementIterator();

        Bukkit.getLogger().info("Advancements inachevés pour " + player.getName() + ":");

        while (advancements.hasNext()) {
            Advancement advancement = advancements.next();
            AdvancementProgress progress = player.getAdvancementProgress(advancement);
            if (!progress.isDone()) {
                Bukkit.getLogger().info("- " + advancement.getKey());
            }
        }
    }
}