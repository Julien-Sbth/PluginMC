package fr.api.InventoryManager;


import fr.api.basededonnees.SQLiteManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Base64;
import java.util.UUID;
import java.util.logging.Logger;

public class InventoryListener implements Listener {
    private final Connection connection;
    private final Logger logger;
    private final SQLiteManager sqliteManager;

    public InventoryListener(SQLiteManager sqliteManager, Logger logger) {
        this.connection = sqliteManager.getConnection();
        this.logger = logger;
        this.sqliteManager = sqliteManager;
        verifierDonneesExistantes();

    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        String nomItem = event.getItemDrop().getItemStack().getType().toString();
        int quantite = event.getItemDrop().getItemStack().getAmount();

        UUID joueurUUID = event.getPlayer().getUniqueId(); // Utiliser getUniqueId() pour récupérer l'UUID du joueur
        supprimerItem(joueurUUID, nomItem, quantite);
    }

    @EventHandler
    public void onPlayerPickupItem(PlayerPickupItemEvent event) {
        String nomItem = event.getItem().getItemStack().getType().toString();
        int quantite = event.getItem().getItemStack().getAmount();
        UUID joueurUUID = event.getPlayer().getUniqueId(); // Utiliser getUniqueId() pour récupérer l'UUID du joueur

        // Obtention de l'image Base64 correspondante
        String imageBase64 = getImageBase64(nomItem);
        ajouterItem(joueurUUID, nomItem, quantite, imageBase64);
    }
    public void verifierDonneesExistantes() {
        try {
            // Compter le nombre de lignes avec des données
            PreparedStatement countStatement = connection.prepareStatement(
                    "SELECT COUNT(*) AS total FROM player_inventory"
            );
            ResultSet countResult = countStatement.executeQuery();

            int totalItems = 0;
            if (countResult.next()) {
                totalItems = countResult.getInt("total");
            }

            countStatement.close();

            if (totalItems < 36) {
                // Ajouter des lignes vides jusqu'à ce qu'il y en ait 36 au total
                for (int i = totalItems + 1; i <= 36; i++) {
                    PreparedStatement insertStatement = connection.prepareStatement(
                            "INSERT INTO player_inventory (player_uuid, nom_item, quantite, position, image_base64) VALUES (?, '', 0, 0, '')"
                    );
                    insertStatement.setString(1, "");
                    insertStatement.executeUpdate();
                    insertStatement.close();
                }
            }
        } catch (SQLException e) {
            logger.severe("Erreur lors de la vérification des données existantes : " + e.getMessage());
        }
    }



    private void ajouterItem(UUID joueurUUID, String nomItem, int quantite, String imageBase64) {
        try {
            if (connection != null) {
                // Vérifier si l'élément existe déjà dans l'inventaire du joueur
                PreparedStatement selectItemStatement = connection.prepareStatement(
                        "SELECT id, quantite FROM player_inventory WHERE player_uuid = ? AND nom_item = ?"
                );
                selectItemStatement.setString(1, joueurUUID.toString());
                selectItemStatement.setString(2, nomItem);
                ResultSet itemResult = selectItemStatement.executeQuery();

                if (itemResult.next()) {
                    // L'élément existe déjà, donc mettre à jour la quantité
                    int itemId = itemResult.getInt("id");
                    int existingQuantity = itemResult.getInt("quantite");
                    int updatedQuantity = existingQuantity + quantite;

                    PreparedStatement updateStatement = connection.prepareStatement(
                            "UPDATE player_inventory SET quantite = ? WHERE id = ?"
                    );
                    updateStatement.setInt(1, updatedQuantity);
                    updateStatement.setInt(2, itemId);
                    updateStatement.executeUpdate();
                    updateStatement.close();
                } else {
                    // L'élément n'existe pas encore, donc l'insérer dans la base de données
                    PreparedStatement selectEmptyStatement = connection.prepareStatement(
                            "SELECT id FROM player_inventory WHERE nom_item = '' AND quantite = 0 AND position = 0 AND image_base64 = '' OR id = (SELECT MIN(id) FROM player_inventory WHERE id > (SELECT MAX(id) FROM player_inventory WHERE nom_item != '' AND quantite != 0 AND position != 0 AND image_base64 != '')) LIMIT 1"
                    );
                    ResultSet emptyResult = selectEmptyStatement.executeQuery();

                    if (emptyResult.next()) {
                        // S'il y a une ligne vide ou une ID libre, mettre à jour ses données
                        int idVide = emptyResult.getInt("id");
                        PreparedStatement updateStatement = connection.prepareStatement(
                                "UPDATE player_inventory SET player_uuid = ?, nom_item = ?, quantite = ?, position = ?, image_base64 = ? WHERE id = ?"
                        );
                        updateStatement.setString(1, joueurUUID.toString());
                        updateStatement.setString(2, nomItem);
                        updateStatement.setInt(3, quantite);
                        updateStatement.setInt(4, getNextAvailablePosition(joueurUUID));
                        updateStatement.setString(5, imageBase64);
                        updateStatement.setInt(6, idVide);
                        updateStatement.executeUpdate();
                        updateStatement.close();
                    } else {
                        // Aucun emplacement vide trouvé, utiliser INSERT INTO pour ajouter un nouvel élément
                        logger.warning("Aucun emplacement vide trouvé dans l'inventaire.");
                    }

                    selectEmptyStatement.close();
                }

                selectItemStatement.close();
            } else {
                logger.severe("La connexion à la base de données est nulle.");
            }
        } catch (SQLException e) {
            logger.severe("Erreur lors de l'ajout d'un nouvel élément à l'inventaire : " + e.getMessage());
        }
    }






    private int getNextAvailablePosition(UUID joueurUUID) {
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT position FROM player_inventory WHERE player_uuid = ? ORDER BY position");
            statement.setString(1, joueurUUID.toString());
            ResultSet resultSet = statement.executeQuery();

            int position = 1;

            while (resultSet.next()) {
                int currentPosition = resultSet.getInt("position");
                if (currentPosition != position) {
                    return position;
                }
                position++;
            }

            return position;

        } catch (SQLException e) {
            logger.severe("Erreur lors de la récupération de la position de l'item dans l'inventaire : " + e.getMessage());
            return -1;
        }
    }


    private void supprimerItem(UUID joueurUUID, String nomItem, int quantite) {
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT position, quantite FROM player_inventory WHERE player_uuid = ? AND nom_item = ?");
            statement.setString(1, joueurUUID.toString());
            statement.setString(2, nomItem);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                int position = resultSet.getInt("position");
                int remainingQuantity = resultSet.getInt("quantite") - quantite;

                if (remainingQuantity > 0) {
                    statement = connection.prepareStatement("UPDATE player_inventory SET quantite = ? WHERE player_uuid = ? AND nom_item = ?");
                    statement.setInt(1, remainingQuantity);
                    statement.setString(2, joueurUUID.toString());
                    statement.setString(3, nomItem);
                    statement.executeUpdate();
                    statement.close();
                } else if (remainingQuantity == 0) {
                    // Réinitialisation des données lorsque la quantité atteint 0
                    statement = connection.prepareStatement("UPDATE player_inventory SET quantite = 0, player_uuid = '', nom_item = '', position = 0, image_base64 = '' WHERE player_uuid = ? AND nom_item = ?");
                    statement.setString(1, joueurUUID.toString());
                    statement.setString(2, nomItem);
                    statement.executeUpdate();
                    statement.close();
                } else {
                    logger.warning("La quantité restante de l'élément est négative. Veuillez vérifier les données.");
                }
            }
        } catch (SQLException e) {
            logger.severe("Erreur lors de la mise à jour de la quantité de l'élément de l'inventaire : " + e.getMessage());
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
            logger.severe("Erreur lors de la lecture de l'image : " + e.getMessage());
        }

        return imageBase64;
    }
}