package fr.api.menu;

import fr.api.basededonnees.SQLiteManager;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DeductPlayerCoins {

    private final SQLiteManager sqliteManager;
    private final Logger logger;

    public DeductPlayerCoins(SQLiteManager sqliteManager, Logger logger) {
        this.sqliteManager = sqliteManager;
        this.logger = logger;
    }

    // Méthode pour déduire les pièces d'un joueur
    private void deductCoins(SQLiteManager sqliteManager, UUID playerId, double amount) {
        try {
            // Vérifier si la connexion à la base de données est ouverte
            if (!this.sqliteManager.isConnected()) {
                throw new SQLException("La connexion à la base de données est fermée.");
            }

            double currentCoins = getPlayerCoins(playerId);
            double newCoins = Math.max(0, currentCoins - amount);

            // Exécuter la mise à jour des pièces du joueur dans la base de données
            try (PreparedStatement updateStatement = this.sqliteManager.getConnection().prepareStatement(
                    "UPDATE PlayerCoins SET coins = ? WHERE player_id = ?")) {
                updateStatement.setDouble(1, newCoins);
                updateStatement.setString(2, playerId.toString());
                updateStatement.executeUpdate();
            } catch (SQLException e) {
                logger.severe("Erreur lors de la mise à jour des pièces du joueur dans la base de données : " + e.getMessage());
            }
        } catch (SQLException e) {
            logger.severe("Erreur lors de la déduction des pièces du joueur : " + e.getMessage());
        }
    }

    // Méthode pour récupérer les pièces d'un joueur
    private double getPlayerCoins(UUID playerId) throws SQLException {
        double coins = 0;

        try (PreparedStatement ps = sqliteManager.getConnection().prepareStatement(
                "SELECT coins FROM PlayerCoins WHERE player_id = ?")) {
            ps.setString(1, playerId.toString());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                coins = rs.getDouble("coins");
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Erreur lors de la récupération des pièces du joueur dans la base de données", e);
            throw e; // Propager l'exception vers le code appelant
        }

        return coins;
    }

    // Méthode pour enregistrer un achat dans la base de données
    public void recordPurchase(UUID playerId, String itemName, int quantity, int price) {
        try {
            // Vérifier si la connexion à la base de données est ouverte
            if (!sqliteManager.isConnected()) {
                throw new SQLException("La connexion à la base de données est fermée.");
            }

            // Exécuter l'insertion de l'achat dans la base de données
            try (PreparedStatement insertPs = sqliteManager.getConnection().prepareStatement(
                    "INSERT INTO Player_Shop (player_id, item_name, quantity, price) VALUES (?, ?, ?, ?)")) {
                insertPs.setString(1, playerId.toString());
                insertPs.setString(2, itemName);
                insertPs.setInt(3, quantity);
                insertPs.setInt(4, price);
                insertPs.executeUpdate();
            } catch (SQLException e) {
                logger.severe("Erreur lors de l'enregistrement de l'achat dans la base de données : " + e.getMessage());
            }
        } catch (SQLException e) {
            logger.severe("Erreur lors de l'enregistrement de l'achat : " + e.getMessage());
        }
    }
}
