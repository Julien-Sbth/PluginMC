package fr.api.HTTPRequest;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class HTTPRequest extends JavaPlugin {

    public void makeHTTPRequest() {
        try {
            URL url = new URL("http://localhost:8080/items");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();

            getLogger().info("Réponse du serveur Go : " + response.toString());

            JsonArray jsonArray = JsonParser.parseString(response.toString()).getAsJsonArray();

            for (int i = 0; i < jsonArray.size(); i++) {
                JsonObject jsonObject = jsonArray.get(i).getAsJsonObject();

                String itemID = jsonObject.get("itemID").getAsString();
                String playerName = jsonObject.get("playerName").getAsString();
                String itemName = jsonObject.get("itemName").getAsString();
                if (itemID != null && playerName != null && itemName != null) {
                    Player player = getServer().getPlayerExact(playerName);
                    if (player != null && player.isOnline()) {
                        Material itemType = Material.matchMaterial(itemName.toUpperCase());
                        if (itemType != null) {
                            ItemStack item = new ItemStack(itemType);
                            player.getInventory().addItem(item);
                            getLogger().info("Item " + itemName + " donné avec succès à " + playerName);

                            sendItemReceivedConfirmation(itemID);
                        } else {
                            getLogger().warning("Type d'item invalide : " + itemName);
                        }
                    } else {
                        getLogger().warning("Le joueur " + playerName + " n'est pas en ligne !");
                    }
                }
            }

            conn.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendItemReceivedConfirmation(String itemID) {
        try {
            URL url = new URL("http://localhost:8080/items/received");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);

            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("itemID", itemID);

            OutputStream os = conn.getOutputStream();
            os.write(jsonObject.toString().getBytes());
            os.flush();
            os.close();

            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                getLogger().info("Confirmation d'item envoyée avec succès pour l'item " + itemID);
            } else {
                getLogger().warning("Erreur lors de l'envoi de la confirmation d'item pour l'item " + itemID);
            }

            conn.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
