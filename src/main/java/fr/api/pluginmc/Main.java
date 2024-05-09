package fr.api.pluginmc;

import com.google.gson.JsonObject;
import fr.api.Block.BlockDestroyListener;
import fr.api.BlockMovementTracker.BlockMovementTracker;
import fr.api.InventoryManager.InventoryListener;
import fr.api.JoinLeave.JoinLeaveListener;
import fr.api.JoinLeave.PlayerJoinQuitListener;
import fr.api.JoinLeave.PlayerManager;
import fr.api.Listener.ListenerVaultPlayer;
import fr.api.menu.Menu;
import fr.api.advancement.AchievementListener;
import fr.api.api.APIClient;
import fr.api.commands.Commands;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import fr.api.BlockMovementTracker.BlockMovementTracker;
import fr.api.JoinLeave.PlayerJoinQuitListener;
import fr.api.JoinLeave.PlayerManager;
import fr.api.menu.PlayerCoinsManager;
import fr.api.InventoryManager.InventoryListener;
import fr.api.advancement.AchievementListener;
import fr.api.api.APIClient;
import fr.api.artefact.ArtefactItemsListener;
import fr.api.basededonnees.SQLiteManager;
import fr.api.menu.Menu;
import fr.api.menu.PlayerCoinsManager;
import fr.api.menu.ShopManager;
import fr.api.moneycommands.MoneyCommand;
import fr.api.monster.MonsterDeathListener;
import fr.api.shop.ShopADDItem;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.logging.Logger;

public class Main extends JavaPlugin {
    private PlayerManager playerManager;

    private SQLiteManager sqliteManager;
    private ShopManager shopManager;
    private MonsterDeathListener monsterDeathListener;
    private APIClient apiClient;
    private PlayerCoinsManager playerCoinsManager;
    private Menu menu;
    private ShopADDItem shopadditem;

    private static Main plugin;

    public static Main getPlugin() {
        return plugin;
    }

    @Override
    public void onEnable() {
        getCommand("money").setExecutor(new MoneyCommand(monsterDeathListener));
        getServer().getPluginManager().registerEvents(new ArtefactItemsListener(), this);

        getCommand("test").setExecutor(new Commands(monsterDeathListener, this));
        getCommand("alert").setExecutor(new Commands(monsterDeathListener, this));
        getCommand("stats").setExecutor(new Commands(monsterDeathListener, this));
        getCommand("shop").setExecutor(new Commands(monsterDeathListener, this));
        getCommand("sb").setExecutor(new Commands(monsterDeathListener, this));

        Logger logger = Logger.getLogger("MenuLogger");
        getConfig().options().copyDefaults();
        saveDefaultConfig();

        getCommand("staffhome").setExecutor(new Commands(monsterDeathListener, this));

        plugin = this;
        getCommand("vault").setExecutor(new Commands(monsterDeathListener, this));
        getServer().getPluginManager().registerEvents(new ListenerVaultPlayer(), this);
        sqliteManager = new SQLiteManager("database.sqlite");
        if (sqliteManager.isConnected()) {
            getLogger().info("Connexion à la base de données établie avec succès !");
        } else {
            getLogger().warning("La connexion à la base de données a échoué !");
        }
        menu = new Menu(this, sqliteManager, logger);

        getServer().getPluginManager().registerEvents(new JoinLeaveListener(), this);

        menu.registerEvents();
        Listener inventoryListener = new InventoryListener(sqliteManager, getLogger());

        getServer().getPluginManager().registerEvents(inventoryListener, this);
        apiClient = new APIClient(sqliteManager);
        getServer().getPluginManager().registerEvents(new BlockDestroyListener(sqliteManager), this);

        shopManager = new ShopManager(sqliteManager);
        shopadditem = new ShopADDItem(sqliteManager, shopManager);
        shopadditem.addToShop();
        apiClient.sendPlayerDataToSite();

        getServer().getPluginManager().registerEvents(new MonsterDeathListener(sqliteManager, getLogger(), List.of("")), this);
        getServer().getPluginManager().registerEvents(new BlockMovementTracker(sqliteManager, this), this);
        getServer().getPluginManager().registerEvents(new AchievementListener(sqliteManager), this);
        playerCoinsManager = new PlayerCoinsManager(this, sqliteManager);

        BlockMovementTracker movementTracker = new BlockMovementTracker(sqliteManager, this);
        getServer().getPluginManager().registerEvents(movementTracker, this);
        playerManager = new PlayerManager(sqliteManager);
        getServer().getPluginManager().registerEvents(new PlayerJoinQuitListener(sqliteManager), this);
        makeHTTPRequest();
    }
    @Override
    public void onDisable() {
        if (sqliteManager != null) {
            sqliteManager.closeConnection();
        }
        getLogger().info("Le Plugin MC vient de s'éteindre !");
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        playerManager.playerJoined(event.getPlayer());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        playerManager.playerLeft(event.getPlayer());
    }
    private void makeHTTPRequest() {
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
                String ItemsName = jsonObject.get("ItemsName").getAsString();
                if (itemID != null && playerName != null && ItemsName != null) {
                    Player player = getServer().getPlayerExact(playerName);
                    if (player != null && player.isOnline()) {
                        String giveCommand = "/give " + playerName + " minecraft:" + ItemsName;
                        player.performCommand(giveCommand);
                        getLogger().info("Commande /give exécutée avec succès pour " + playerName + " avec l'item " + ItemsName);
                        getLogger().info("Commande exécutée : " + giveCommand); // Ajout du log pour afficher la commande exécutée
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

}
