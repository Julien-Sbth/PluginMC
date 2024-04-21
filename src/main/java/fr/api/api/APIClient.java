package fr.api.api;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import fr.api.basededonnees.SQLiteManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class APIClient {

    private final String API_ENDPOINT = "http://localhost:8080/data";
    private SQLiteManager sqliteManager;
    private Map<String, String> itemImageMap = new HashMap<>();
    public APIClient(SQLiteManager sqliteManager) {
        this.sqliteManager = sqliteManager;
    }
    private JsonArray convertResultSetToJson(ResultSet rs) throws SQLException {
        JsonArray jsonArray = new JsonArray();
        ResultSetMetaData metaData = rs.getMetaData();
        int numColumns = metaData.getColumnCount();
        while (rs.next()) {
            JsonObject obj = new JsonObject();
            for (int i = 1; i <= numColumns; ++i) {
                String columnName = metaData.getColumnName(i);
                Object value = rs.getObject(i);
                obj.addProperty(columnName, value.toString());
            }
            jsonArray.add(obj);
        }
        return jsonArray;
    }

    private void sendJsonToSite(JsonArray jsonArrayCoins, JsonArray jsonArrayKills, JsonArray jsonArrayBlock, JsonArray jsonArrayAchievements, JsonArray jsonArrayDestroyBlocks , JsonArray jsonArrayInventory, JsonArray jsonArrayShop, JsonArray jsonArrayShopItem ) {
        try {
            URL url = new URL(API_ENDPOINT);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            JsonObject jsonObject = new JsonObject();
            jsonObject.add("coins", jsonArrayCoins);
            jsonObject.add("kills", jsonArrayKills);
            jsonObject.add("blocks", jsonArrayBlock);
            jsonObject.add("achievements", jsonArrayAchievements);
            jsonObject.add("block_name", jsonArrayDestroyBlocks);
            jsonObject.add("nom_item", jsonArrayInventory);
            jsonObject.add("shop", jsonArrayShop);
            jsonObject.add("shop_item", jsonArrayShopItem);

            System.out.println("Données JSON envoyées au site : " + jsonObject.toString());

            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = jsonObject.toString().getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"))) {
                StringBuilder response = new StringBuilder();
                String responseLine;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }
                System.out.println("Réponse de l'API : " + response.toString());
            }

            conn.disconnect();

            System.out.println("Données envoyées à l'API avec succès.");
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Erreur lors de l'envoi des données à l'API : " + e.getMessage());
        }
    }

    public void sendPlayerDataToSite() {
        try {
            Connection conn = sqliteManager.getConnection();
            Statement stmt = conn.createStatement();

            ResultSet rsCoins = stmt.executeQuery("SELECT * FROM PlayerCoins");
            JsonArray jsonArrayCoins = convertResultSetToJson(rsCoins);

            ResultSet rsKills = stmt.executeQuery("SELECT * FROM PlayerKills");
            JsonArray jsonArrayKills = convertResultSetToJson(rsKills);

            ResultSet rsBlock = stmt.executeQuery("SELECT * FROM player_movement");
            JsonArray jsonArrayBlock = convertResultSetToJson(rsBlock);

            ResultSet rsAchievements = stmt.executeQuery("SELECT * FROM player_achievements");
            JsonArray jsonArrayAchievements = convertResultSetToJson(rsAchievements);

            ResultSet rsDestroyBlocks = stmt.executeQuery("SELECT * FROM player_destroyblocks");
            JsonArray jsonArrayDestroyBlocks = convertResultSetToJson(rsDestroyBlocks);

            ResultSet rsInventory = stmt.executeQuery("SELECT * FROM player_inventory");
            JsonArray jsonArrayInventory = convertResultSetToJson(rsInventory);

            ResultSet rsShop = stmt.executeQuery("SELECT * FROM Player_Shop");
            JsonArray jsonArrayShop = convertResultSetToJson(rsShop);

            ResultSet rsShopItem = stmt.executeQuery("SELECT * FROM Shop");
            JsonArray jsonArrayShopItem = convertResultSetToJson(rsShop);

            rsBlock.close();
            rsCoins.close();
            rsKills.close();
            rsBlock.close();
            rsInventory.close();
            rsInventory.close();
            rsDestroyBlocks.close();
            rsShopItem.close();
            stmt.close();

            sendJsonToSite(jsonArrayCoins, jsonArrayKills, jsonArrayBlock, jsonArrayAchievements, jsonArrayDestroyBlocks, jsonArrayInventory, jsonArrayShop, jsonArrayShopItem);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Erreur lors de l'envoi des données au site : " + e.getMessage());
        }
    }
}
