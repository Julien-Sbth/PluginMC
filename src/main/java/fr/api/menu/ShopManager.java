package fr.api.menu;

import fr.api.basededonnees.SQLiteManager;
import org.bukkit.Material;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Base64;

public class ShopManager {

    private final SQLiteManager sqliteManager;

    public ShopManager(SQLiteManager sqliteManager) {
        this.sqliteManager = sqliteManager;
    }

    public void addItemToShop(Material material, int quantity, int price) {
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
                    if (count > 0) {
                        System.out.println("L'item " + material.toString() + " est déjà présent dans la boutique pour ce joueur.");
                        return;
                    }
                }
            }

            String imageBase64 = getImageBase64(material.toString());

            try (PreparedStatement insertPs = sqliteManager.getConnection().prepareStatement(
                    "INSERT INTO Shop (item_name, quantity, price, image_base64) VALUES (?, ?, ?, ?)")) {
                insertPs.setString(1, material.toString());
                insertPs.setInt(2, quantity);
                insertPs.setInt(3, price);
                insertPs.setString(4, imageBase64);

                insertPs.executeUpdate();
                System.out.println("Item " + material.toString() + " ajouté à la boutique pour ce joueur.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private String getImageBase64(String nomItem) {
        String dossierImages = "images" + File.separator;

        String imageBase64 = "";

        try {
            Path imagePath = Paths.get(dossierImages + nomItem + ".png");
            byte[] imageBytes = Files.readAllBytes(imagePath);
            imageBase64 = Base64.getEncoder().encodeToString(imageBytes);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return imageBase64;
    }
}
