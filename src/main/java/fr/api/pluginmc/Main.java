package fr.api.pluginmc;

import fr.api.Block.BlockDestroyListener;
import fr.api.menu.Menu;
import fr.api.BlockMovementTracker.BlockMovementTracker;
import fr.api.advancement.AchievementListener;
import fr.api.api.APIClient;
import fr.api.menu.PlayerCoinsManager;
import fr.api.InventoryManager.InventoryListener;
import fr.api.artefact.ArtefactItemsListener;
import fr.api.basededonnees.SQLiteManager;
import fr.api.commands.Commands;
import fr.api.moneycommands.MoneyCommand;
import fr.api.monster.MonsterDeathListener;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;
import java.util.logging.Logger;

public class Main extends JavaPlugin implements Listener {
    private SQLiteManager sqliteManager;

    private MonsterDeathListener monsterDeathListener;
    private APIClient apiClient;
    private PlayerCoinsManager playerCoinsManager;
    private Menu menu;

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
        getCommand("money").setExecutor(new MoneyCommand(monsterDeathListener));
        getServer().getPluginManager().registerEvents(new ArtefactItemsListener(), this);

        getCommand("test").setExecutor(new Commands());
        getCommand("alert").setExecutor(new Commands());
        getCommand("stats").setExecutor(new Commands());
        getCommand("shop").setExecutor(new Commands());

        Logger logger = Logger.getLogger("MenuLogger");

        sqliteManager = new SQLiteManager("database.sqlite");
        if (sqliteManager.isConnected()) {
            getLogger().info("Connexion à la base de données établie avec succès !");
        } else {
            getLogger().warning("La connexion à la base de données a échoué !");
        }
        menu = new Menu(this, sqliteManager, logger);

        menu.registerEvents();
        Listener inventoryListener = new InventoryListener(sqliteManager, getLogger());

        getServer().getPluginManager().registerEvents(inventoryListener, this);
        apiClient = new APIClient(sqliteManager);
        getServer().getPluginManager().registerEvents(new BlockDestroyListener(sqliteManager), this);

        apiClient.sendPlayerDataToSite();

        getServer().getPluginManager().registerEvents(new MonsterDeathListener(sqliteManager, getLogger(), List.of("")), this);
        getServer().getPluginManager().registerEvents(new BlockMovementTracker(sqliteManager, this), this);
        getServer().getPluginManager().registerEvents(new AchievementListener(sqliteManager), this);
        playerCoinsManager = new PlayerCoinsManager(this, sqliteManager);
        playerCoinsManager = new PlayerCoinsManager(this, sqliteManager);

        BlockMovementTracker movementTracker = new BlockMovementTracker(sqliteManager, this);
        getServer().getPluginManager().registerEvents(movementTracker, this);
        restaurerInventaire();
    }

    @Override
    public void onDisable() {
        if (sqliteManager != null) {
            sqliteManager.closeConnection();
        }
        getLogger().info("Le Plugin MC vient de s'éteindre !");
    }

    private void restaurerInventaire() {
        InventoryListener inventoryListener = new InventoryListener(sqliteManager, getLogger());
    }
}
