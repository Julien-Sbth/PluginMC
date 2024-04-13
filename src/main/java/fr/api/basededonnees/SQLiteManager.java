package fr.api.basededonnees;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SQLiteManager {
    private Connection connection;
    private final Logger logger;
    public SQLiteManager(String dbPath) {
        this.logger = Logger.getLogger("sqlite");
        try {
            Class.forName("org.sqlite.JDBC");
            this.connection = DriverManager.getConnection("jdbc:sqlite:" + dbPath);
        } catch (ClassNotFoundException e) {
            logger.log(Level.SEVERE, "Driver SQLite introuvable", e);
            closeConnection();
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Erreur lors de la connexion à la base de données", e);
            closeConnection();
        }
    }
    public Connection getConnection() {
        return this.connection;
    }

    public Logger getLogger() {
        return this.logger;
    }

    public void closeConnection() {
        try {
            if (this.connection != null && !this.connection.isClosed()) {
                this.connection.close();
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Erreur lors de la fermeture de la connexion à la base de données", e);
        }
    }
    public boolean isConnected() {
        try {
            return this.connection != null && !this.connection.isClosed();
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Erreur lors de la vérification de l'état de la connexion à la base de données", e);
            return false;
        }
    }
}