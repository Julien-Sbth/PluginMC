package fr.api.menu;

import fr.api.artefact.ArtefactItemsListener;
import fr.api.basededonnees.SQLiteManager;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;
import java.util.logging.Logger;

import static fr.api.menu.ArtefactItems.createItem;
import static fr.api.menu.ItemUtils.createArmorItem;

public class Menu implements Listener {
    protected static JavaPlugin plugin;
    private SQLiteManager sqliteManager;
    private final Logger logger;
    static final Map<Material, Integer> prices = new HashMap<>();
    private final ArtefactItemsListener artefactListener;

    public Menu(JavaPlugin plugin, SQLiteManager sqliteManager, Logger logger) {
        Menu.plugin = plugin;
        this.sqliteManager = sqliteManager;
        this.logger = logger;

        ItemStack customAxeNetherite = customaxenetherite();
        ItemStack customAxeWood = customaxewood();
        ItemStack customAxeIron = customaxeiron();
        ItemStack customAxeDiamond = customaxediamond();
        ItemStack customAxeGolden = customaxegolden();
        ItemStack customSword = customsword();
        ItemStack customBoots = customboots();
        ItemStack customChestplate = customchestplate();
        ItemStack customLeggings = customleggins();
        ItemStack customSwordDiamond = customswordiamond();
        ItemStack customSwordWood = customswordwood();
        ItemStack customSwordIron = customswordiron();
        ItemStack customSwordGolden = customswordgolden();
        ItemStack customBootsDiamond = custombootsDiamond();
        ItemStack customBootsGold = custombootsGold();
        ItemStack customBootsCopper = custombootsCopper();
        ItemStack customChestplateDiamond = customchestplateDiamond();
        ItemStack customChestplateGold = customchestplateGold();
        ItemStack customChestplateCopper = customchestplateCopper();
        ItemStack customChestplateIron = customchestplateIron();
        ItemStack customLeggingsDiamond = customlegginsDiamond();
        ItemStack customLeggingsGold = customlegginsGold();
        ItemStack customLeggingsCopper = customlegginsCopper();
        ItemStack customHelmetDiamond = customhelmetDiamond();
        ItemStack customHelmetGold = customhelmetGold();
        ItemStack customHelmetCopper = customhelmetCopper();
        ItemStack customshield = customshield();
        ItemStack customLeggingsIron = customLegginsIron();
        ItemStack customHelmetIron = customhelmetIron();
        ItemStack customBow = customBow();
        ItemStack customPickaxe = custompickaxe();
        ItemStack customBootsIron = customBootsIron();
        ItemStack customHelmet = customHelmet();
        ItemStack customPickaxeIron = customPickaxeIron();
        ItemStack customPickaxeGolden = customPickaxeGolden();
        ItemStack customPickaxeWooden = customPickaxeWooden();
        ItemStack customPickaxeDiamond = customPickaxeDiamond();
        ItemStack customSwordNetherite = customSwordNetherite();
        ItemStack customElytra = customElytra();
        ItemStack customStonePickaxe= customStonePickaxe();
        ItemStack customPotion= customPotion();
        ItemStack customDirt= customDirt();
        ItemStack customStoneSword= customStoneSword();
        ItemStack customFishingRog= customFishingRog();



        prices.put(customDirt.getType(), 1);
        prices.put(customStonePickaxe.getType(), 2500);
        prices.put(customStoneSword.getType(), 1);
        prices.put(customFishingRog.getType(), 600);
        prices.put(customElytra.getType(), 2500);
        prices.put(customPotion.getType(), 150);
        prices.put(customPickaxeDiamond.getType(), 500);
        prices.put(customPickaxeGolden.getType(), 250);
        prices.put(customPickaxeWooden.getType(), 50);
        prices.put(customHelmet.getType(), 250);
        prices.put(customBootsIron.getType(), 150);
        prices.put(customPickaxe.getType(), 1000);
        prices.put(customLeggingsIron.getType(), 350);
        prices.put(customAxeNetherite.getType(), 1000);
        prices.put(customAxeDiamond.getType(), 500);
        prices.put(customAxeIron.getType(), 350);
        prices.put(customAxeGolden.getType(), 350);
        prices.put(customAxeWood.getType(), 150);
        prices.put(customBow.getType(), 375);
        prices.put(customSwordDiamond.getType(), 750);
        prices.put(customSwordGolden.getType(), 375);
        prices.put(customSwordIron.getType(), 500);
        prices.put(customSword.getType(), 150);
        prices.put(customSwordWood.getType(), 150);
        prices.put(customBootsDiamond.getType(), 750);
        prices.put(customBootsGold.getType(), 300);
        prices.put(customBootsCopper.getType(), 150);
        prices.put(customChestplateDiamond.getType(), 750);
        prices.put(customChestplateGold.getType(), 400);
        prices.put(customChestplateCopper.getType(), 150);
        prices.put(customChestplateIron.getType(), 500);
        prices.put(customLeggingsDiamond.getType(), 500);
        prices.put(customLeggingsGold.getType(), 250);
        prices.put(customLeggingsCopper.getType(), 150);
        prices.put(customHelmetDiamond.getType(), 350);
        prices.put(customHelmetGold.getType(), 150);
        prices.put(customHelmetCopper.getType(), 150);
        prices.put(customSwordDiamond.getType(), 750);
        prices.put(customBoots.getType(), 150);
        prices.put(customChestplate.getType(), 1500);
        prices.put(customLeggings.getType(), 350);
        prices.put(customHelmetIron.getType(), 250);
        prices.put(customshield.getType(), 500);
        prices.put(customPickaxeIron.getType(), 400);
        prices.put(customSwordNetherite.getType(), 1000);

        artefactListener = new ArtefactItemsListener();
    }

    private ItemStack customFishingRog() {
        return createArmorItem(Material.FISHING_ROD, "§6Tank", "§7Des jambières très confortables", "§7et ultra résistantes !");
    }

    private ItemStack customStoneSword() {
        return createArmorItem(Material.STONE_SWORD, "§6Tank", "§7Des jambières très confortables", "§7et ultra résistantes !");
    }

    private ItemStack customDirt() {
        return createArmorItem(Material.DIRT, "§6Tank", "§7Des jambières très confortables", "§7et ultra résistantes !");
    }

    private ItemStack customPotion() {
        return createArmorItem(Material.POTION, "§6Tank", "§7Des jambières très confortables", "§7et ultra résistantes !");

    }

    private ItemStack customStonePickaxe() {
        return createArmorItem(Material.STONE_PICKAXE, "§6Tank", "§7Des jambières très confortables", "§7et ultra résistantes !");

    }

    private ItemStack customElytra() {
        return createArmorItem(Material.ELYTRA, "§6Tank", "§7Des jambières très confortables", "§7et ultra résistantes !");
    }

    private ItemStack customPickaxeDiamond() {
        return createArmorItem(Material.DIAMOND_PICKAXE, "§6Tank", "§7Des jambières très confortables", "§7et ultra résistantes !");

    }

    private ItemStack customSwordNetherite() {
        return createArmorItem(Material.NETHERITE_SWORD, "§6Tank", "§7Des jambières très confortables", "§7et ultra résistantes !");

    }

    private ItemStack customPickaxeWooden() {
        return createArmorItem(Material.WOODEN_PICKAXE, "§6Tank", "§7Des jambières très confortables", "§7et ultra résistantes !");
    }

    private ItemStack customPickaxeGolden() {
        return createArmorItem(Material.GOLDEN_PICKAXE, "§6Tank", "§7Des jambières très confortables", "§7et ultra résistantes !");
    }

    private ItemStack customPickaxeIron() {
        return createArmorItem(Material.IRON_PICKAXE, "§6Tank", "§7Des jambières très confortables", "§7et ultra résistantes !");
    }

    private ItemStack customHelmet() {
        return createArmorItem(Material.NETHERITE_HELMET, "§6Tank", "§7Des jambières très confortables", "§7et ultra résistantes !");
    }

    private ItemStack customBootsIron() {
        return createArmorItem(Material.IRON_BOOTS, "§6Tank", "§7Des jambières très confortables", "§7et ultra résistantes !");
    }

    private ItemStack customLegginsIron() {
        return createArmorItem(Material.IRON_LEGGINGS, "§6Tank", "§7Des jambières très confortables", "§7et ultra résistantes !");
    }

    private ItemStack custombootsGold() {
        return createArmorItem(Material.GOLDEN_BOOTS, "§6Tank", "§7Des jambières très confortables", "§7et ultra résistantes !");
    }

    private ItemStack custompickaxe() {
        return createArmorItem(Material.NETHERITE_PICKAXE, "§6Tank", "§7Des jambières très confortables", "§7et ultra résistantes !");
    }

    private ItemStack customlegginsGold() {
        return createArmorItem(Material.GOLDEN_LEGGINGS, "§6Tank", "§7Des jambières très confortables", "§7et ultra résistantes !");
    }

    private ItemStack customlegginsCopper() {
        return createArmorItem(Material.LEATHER_LEGGINGS, "§6Tank", "§7Des jambières très confortables", "§7et ultra résistantes !");
    }

    private ItemStack customlegginsDiamond() {
        return createArmorItem(Material.DIAMOND_LEGGINGS, "§6Tank", "§7Des jambières très confortables", "§7et ultra résistantes !");
    }

    private static ItemStack customBow() {
        return createArmorItem(Material.BOW, "", "");
    }

    private ItemStack customaxenetherite() {
        return createArmorItem(Material.NETHERITE_AXE, "", "");
    }

    private ItemStack customaxediamond() {
        return createArmorItem(Material.DIAMOND_AXE, "", "");
    }

    private ItemStack customaxeiron() {
        return createArmorItem(Material.IRON_AXE, "", "");
    }

    private ItemStack customaxegolden() {
        return createArmorItem(Material.GOLDEN_AXE, "", "");
    }

    private ItemStack customaxewood() {
        return createArmorItem(Material.WOODEN_AXE, "", "");
    }

    private static ItemStack customsword() {
        return createArmorItem(Material.NETHERITE_SWORD, "§6Tank", "§7Des jambières très confortables", "§7et ultra résistantes !");
    }

    private static ItemStack customswordgolden() {
        return createArmorItem(Material.GOLDEN_SWORD, "§6Tank", "§7Des jambières très confortables", "§7et ultra résistantes !");
    }

    private static ItemStack customswordiron() {
        return createArmorItem(Material.IRON_SWORD, "§6Tank", "§7Des jambières très confortables", "§7et ultra résistantes !");
    }

    private static ItemStack customswordwood() {
        return createArmorItem(Material.WOODEN_SWORD, "§6Tank", "§7Des jambières très confortables", "§7et ultra résistantes !");
    }

    private static ItemStack customswordiamond() {
        return createArmorItem(Material.DIAMOND_SWORD, "§6Tank", "§7Des jambières très confortables", "§7et ultra résistantes !");
    }

    private ItemStack customboots() {
        return createArmorItem(Material.NETHERITE_BOOTS, "§6Tank", "§7Des jambières très confortables", "§7et ultra résistantes !");
    }

    private ItemStack customleggins() {
        return createArmorItem(Material.NETHERITE_LEGGINGS, "§6Tank", "§7Des jambières très confortables", "§7et ultra résistantes !");
    }

    private static ItemStack customchestplate() {
        return createArmorItem(Material.NETHERITE_CHESTPLATE, "§6Tank", "§7Des jambières très confortables", "§7et ultra résistantes !");
    }

    private static ItemStack customshield() {
        return createArmorItem(Material.SHIELD, "§6Tank", "§7Des jambières très confortables", "§7et ultra résistantes !");
    }

    private ItemStack custombootsCopper() {
        return createArmorItem(Material.LEATHER_BOOTS, "§6Tank", "§7Des jambières très confortables", "§7et ultra résistantes !");
    }

    private static ItemStack customchestplateDiamond() {
        return createArmorItem(Material.DIAMOND_CHESTPLATE, "§6Tank", "§7Des jambières très confortables", "§7et ultra résistantes !");
    }

    private ItemStack custombootsDiamond() {
        return createArmorItem(Material.DIAMOND_BOOTS, "§6Tank", "§7Des jambières très confortables", "§7et ultra résistantes !");
    }

    private static ItemStack customchestplateGold() {
        return createArmorItem(Material.GOLDEN_CHESTPLATE, "§6Tank", "§7Des jambières très confortables", "§7et ultra résistantes !");
    }

    private static ItemStack customchestplateCopper() {
        return createArmorItem(Material.LEATHER_CHESTPLATE, "§6Tank", "§7Des jambières très confortables", "§7et ultra résistantes !");
    }

    private static ItemStack customchestplateIron() {
        return createArmorItem(Material.IRON_CHESTPLATE, "§6Tank", "§7Des jambières très confortables", "§7et ultra résistantes !");
    }

    private static ItemStack customhelmetDiamond() {
        return createArmorItem(Material.DIAMOND_HELMET, "§6Tank", "§7Des jambières très confortables", "§7et ultra résistantes !");
    }

    private static ItemStack customhelmet() {
        return createArmorItem(Material.NETHERITE_HELMET, "§6Tank", "§7Des jambières très confortables", "§7et ultra résistantes !");
    }

    private static ItemStack customhelmetGold() {
        return createArmorItem(Material.GOLDEN_HELMET, "§6Tank", "§7Des jambières très confortables", "§7et ultra résistantes !");
    }

    private static ItemStack customhelmetCopper() {
        return createArmorItem(Material.LEATHER_HELMET, "§6Tank", "§7Des jambières très confortables", "§7et ultra résistantes !");
    }

    private static ItemStack customhelmetIron() {
        return createArmorItem(Material.IRON_HELMET, "§6Tank", "§7Des jambières très confortables", "§7et ultra résistantes !");
    }

    public static void openCustomMenu(Player player) {
        Inventory menu = Bukkit.createInventory(null, 54, "Menu Shop");

        menu.setItem(0, createArmorItem(Material.DIAMOND_PICKAXE, "Mining", "Categorie"));
        menu.setItem(9, createArmorItem(Material.NETHERITE_SWORD, "Combat", "Categorie"));
        menu.setItem(18, createArmorItem(Material.FISHING_ROD, "Fish", "Categorie"));
        menu.setItem(27, createArmorItem(Material.NETHERITE_CHESTPLATE, "Plastron", "Categorie"));
        menu.setItem(36, createArmorItem(Material.POTION, "Health", "Categorie"));

        for (int i = 1; i <= 54; i += 9) {
            menu.setItem(i, createGlassPane(DyeColor.RED));
        }
        menu.setItem(2, customhelmet());

        menu.setItem(4, customchestplate());

        menu.setItem(3, customsword());

        player.openInventory(menu);
    }

    public void registerEvents() {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    private static ItemStack createGlassPane(DyeColor color) {
        ItemStack glassPane = new ItemStack(Material.RED_STAINED_GLASS_PANE);

        ItemMeta itemMeta = glassPane.getItemMeta();

        if (itemMeta instanceof LeatherArmorMeta) {
            LeatherArmorMeta leatherArmorMeta = (LeatherArmorMeta) itemMeta;

            leatherArmorMeta.setColor(Color.fromRGB(color.getColor().asRGB()));

            glassPane.setItemMeta(leatherArmorMeta);
        }

        return glassPane;
    }

    public static int getPrice(Material material) {
        return prices.getOrDefault(material, 0);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getWhoClicked();
        ItemStack clickedItem = event.getCurrentItem();

        CheckPlayerMoney checkPlayerMoney = new CheckPlayerMoney(plugin);

        if (event.getView().getTitle().equals("Menu Shop")) {
            if (clickedItem != null && clickedItem.getType() != Material.AIR) {
                Material categoryMaterial = clickedItem.getType();

                if (categoryMaterial == Material.DIAMOND_PICKAXE) {
                    openSubMenu(player, "Pioches", Material.NETHERITE_PICKAXE, Material.DIAMOND_PICKAXE, Material.IRON_PICKAXE, Material.WOODEN_PICKAXE);
                } else if (categoryMaterial == Material.NETHERITE_SWORD) {
                    openSubMenu(player, "Épées", Material.NETHERITE_SWORD, Material.DIAMOND_SWORD, Material.IRON_SWORD, Material.WOODEN_SWORD, Material.GOLDEN_SWORD, Material.NETHERITE_AXE, Material.DIAMOND_AXE, Material.IRON_AXE, Material.WOODEN_AXE, Material.GOLDEN_AXE, Material.BOW);
                } else if (categoryMaterial == Material.NETHERITE_CHESTPLATE) {
                    openSubMenu(player, "Armures", Material.GOLDEN_BOOTS, Material.SHIELD, Material.IRON_CHESTPLATE, Material.GOLDEN_CHESTPLATE, Material.LEATHER_CHESTPLATE, Material.DIAMOND_CHESTPLATE, Material.NETHERITE_CHESTPLATE, Material.NETHERITE_BOOTS, Material.LEATHER_BOOTS, Material.IRON_BOOTS, Material.DIAMOND_BOOTS, Material.LEATHER_LEGGINGS, Material.NETHERITE_LEGGINGS, Material.DIAMOND_LEGGINGS, Material.IRON_LEGGINGS, Material.GOLDEN_LEGGINGS, Material.NETHERITE_HELMET, Material.LEATHER_HELMET, Material.DIAMOND_HELMET, Material.GOLDEN_HELMET, Material.IRON_HELMET);
                } else if (categoryMaterial == Material.POTION) {
                    openSubMenu(player, "Santé", Material.POTION);
                } else if (categoryMaterial == Material.FISHING_ROD) {
                    openSubMenu(player, "Pêche", Material.FISHING_ROD);
                }

                event.setCancelled(true);
            }
        } else if (event.getView().getTitle().startsWith("Submenu")) {
            if (clickedItem != null && clickedItem.getType() != Material.AIR) {
                Material itemMaterial = clickedItem.getType();

                if (isNavigationItem(itemMaterial)) {
                    handleNavigationClick(player, itemMaterial);
                    event.setCancelled(true);
                    return;
                }

                int price = getPrice(itemMaterial);

                if (price > 0) {
                    if (checkPlayerMoney.hasEnoughMoney(player, price)) {
                        player.getInventory().addItem(createItem(itemMaterial, null, "").clone());
                        player.sendMessage("Tu as acheté " + createItem(itemMaterial, null, "").getItemMeta().getDisplayName());
                        recordPurchase(player.getUniqueId(), player.getName(), createItem(itemMaterial, null, "").getItemMeta().getDisplayName(), 1, price);

                    } else {
                        player.sendMessage("Tu n'as pas assez d'argent pour acheter " + createItem(itemMaterial, null, "").getItemMeta().getDisplayName());
                    }
                } else {
                    player.sendMessage("Cet objet n'est pas à vendre.");
                }

                event.setCancelled(true);
            }
        }
    }

    public void recordPurchase(UUID playerId, String playerName, String itemName, int quantity, int price) {
        try {
            if (!sqliteManager.isConnected()) {
                throw new SQLException("La connexion à la base de données est fermée.");
            }

            try (PreparedStatement insertPs = sqliteManager.getConnection().prepareStatement(
                    "INSERT INTO Player_Shop (player_id, player_name, item_name, quantity, price) VALUES (?, ?, ?, ?, ?)")) {
                insertPs.setString(1, playerId.toString());
                insertPs.setString(2, playerName);
                insertPs.setString(3, itemName);
                insertPs.setInt(4, quantity);
                insertPs.setInt(5, price);
                insertPs.executeUpdate();
            } catch (SQLException e) {
                logger.severe("Erreur lors de l'enregistrement de l'achat dans la base de données : " + e.getMessage());
            }
        } catch (SQLException e) {
            logger.severe("Erreur lors de l'enregistrement de l'achat : " + e.getMessage());
        }
    }

    private boolean isNavigationItem(Material material) {
        List<Material> navigationItems = Arrays.asList(
                Material.NETHERITE_SWORD,
                Material.DIAMOND_SWORD,
                Material.FISHING_ROD,
                Material.DIAMOND_PICKAXE,
                Material.NETHERITE_CHESTPLATE,
                Material.POTION
        );

        return navigationItems.contains(material);
    }

    private void handleNavigationClick(Player player, Material navigationItem) {
        switch (navigationItem) {
            case DIAMOND_PICKAXE:
                openSubMenu(player, "Pioches", Material.NETHERITE_PICKAXE, Material.DIAMOND_PICKAXE, Material.IRON_PICKAXE, Material.WOODEN_PICKAXE);
                break;
            case NETHERITE_SWORD:
                openSubMenu(player, "Épées", Material.NETHERITE_SWORD, Material.DIAMOND_SWORD, Material.IRON_SWORD, Material.WOODEN_SWORD, Material.GOLDEN_SWORD, Material.NETHERITE_AXE, Material.DIAMOND_AXE, Material.IRON_AXE, Material.WOODEN_AXE, Material.GOLDEN_AXE, Material.BOW);
                break;
            case FISHING_ROD:
                openSubMenu(player, "Fishing", Material.FISHING_ROD);
                break;
            case NETHERITE_CHESTPLATE:
                openSubMenu(player, "Armures", Material.IRON_HELMET, Material.GOLDEN_BOOTS, Material.NETHERITE_CHESTPLATE, Material.DIAMOND_CHESTPLATE, Material.IRON_CHESTPLATE, Material.GOLDEN_CHESTPLATE, Material.NETHERITE_BOOTS, Material.LEATHER_CHESTPLATE, Material.LEATHER_BOOTS, Material.LEATHER_LEGGINGS, Material.NETHERITE_LEGGINGS, Material.DIAMOND_LEGGINGS, Material.IRON_LEGGINGS, Material.GOLDEN_LEGGINGS, Material.SHIELD, Material.NETHERITE_HELMET, Material.DIAMOND_HELMET, Material.GOLDEN_HELMET, Material.LEATHER_HELMET, Material.IRON_BOOTS, Material.DIAMOND_BOOTS);
                break;
            case POTION:
                openSubMenu(player, "Santé", Material.POTION);
                break;
            default:
                break;
        }
    }

    private void openSubMenu(Player player, String category, Material... items) {
        Inventory subMenu = Bukkit.createInventory(null, 54, "Submenu " + category);

        int bootsColumn = 2;
        int chestplateColumn = 3;
        int leggingsColumn = 4;
        int helmetColumn = 5;
        int shieldColumn = 6;
        int swordColumn = 2;
        int axeColumn = 3;
        int arcColumn = 4;
        int potionColumn = 2;
        int pickaxeColumn = 2;

        for (Material itemMaterial : items) {
            if (isItemInCategory(itemMaterial, category)) {
                ItemStack item = createItem(itemMaterial, null, "");
                if (isBoots(itemMaterial)) {
                    subMenu.setItem(bootsColumn, item);
                    bootsColumn += 9;
                } else if (isChestplate(itemMaterial)) {
                    subMenu.setItem(chestplateColumn, item);
                    chestplateColumn += 9;
                } else if (isLeggings(itemMaterial)) {
                    subMenu.setItem(leggingsColumn, item);
                    leggingsColumn += 9;
                } else if (isHelmet(itemMaterial)) {
                    subMenu.setItem(helmetColumn, item);
                    helmetColumn += 9;
                } else if (isShield(itemMaterial)) {
                    subMenu.setItem(shieldColumn, item);
                    shieldColumn += 9;
                } else if (isSword(itemMaterial)) {
                    subMenu.setItem(swordColumn, item);
                    swordColumn += 9;
                } else if (isArc(itemMaterial)) {
                    subMenu.setItem(arcColumn, item);
                    arcColumn += 9;
                } else if (isPotion(itemMaterial)) {
                    subMenu.setItem(potionColumn, item);
                    potionColumn += 9;
                } else if (isPickaxe(itemMaterial)) {
                    subMenu.setItem(pickaxeColumn, item);
                    pickaxeColumn += 9;
                } else if (isAxe(itemMaterial)) {
                    subMenu.setItem(axeColumn, item);
                    axeColumn += 9;
                }
            }
        }

        subMenu.setItem(0, createNavigationItem(Material.DIAMOND_PICKAXE));
        subMenu.setItem(9, createNavigationItem(Material.NETHERITE_SWORD));
        subMenu.setItem(18, createNavigationItem(Material.FISHING_ROD));
        subMenu.setItem(27, createNavigationItem(Material.NETHERITE_CHESTPLATE));
        subMenu.setItem(36, createNavigationItem(Material.POTION));

        for (int i = 1; i <= 54; i += 9) {
            subMenu.setItem(i, createGlassPane(DyeColor.RED));
        }
        player.openInventory(subMenu);
    }

    private ItemStack createNavigationItem(Material material) {
        return new ItemStack(material);
    }

    private boolean isBoots(Material material) {
        return material == Material.GOLDEN_BOOTS || material == Material.NETHERITE_BOOTS || material == Material.LEATHER_BOOTS || material == Material.DIAMOND_BOOTS || material == Material.IRON_BOOTS;
    }

    private boolean isChestplate(Material material) {
        return material == Material.GOLDEN_CHESTPLATE || material == Material.NETHERITE_CHESTPLATE || material == Material.IRON_CHESTPLATE || material == Material.LEATHER_CHESTPLATE || material == Material.DIAMOND_CHESTPLATE;
    }

    private boolean isShield(Material material) {
        return material == Material.SHIELD;
    }

    private boolean isLeggings(Material material) {
        return material == Material.NETHERITE_LEGGINGS || material == Material.DIAMOND_LEGGINGS || material == Material.IRON_LEGGINGS || material == Material.LEATHER_LEGGINGS || material == Material.GOLDEN_LEGGINGS;
    }

    private boolean isHelmet(Material material) {
        return material == Material.DIAMOND_HELMET || material == Material.GOLDEN_HELMET || material == Material.LEATHER_HELMET || material == Material.NETHERITE_HELMET || material == Material.IRON_HELMET;
    }

    private boolean isSword(Material material) {
        return material == Material.NETHERITE_SWORD || material == Material.DIAMOND_SWORD || material == Material.GOLDEN_SWORD || material == Material.IRON_SWORD || material == Material.WOODEN_SWORD;
    }

    private boolean isAxe(Material material) {
        return material == Material.NETHERITE_AXE || material == Material.DIAMOND_AXE || material == Material.GOLDEN_AXE || material == Material.IRON_AXE || material == Material.WOODEN_AXE;
    }

    private boolean isArc(Material material) {
        return material == Material.BOW;
    }

    private boolean isPotion(Material material) {
        return material == Material.POTION;
    }

    private boolean isPickaxe(Material material) {
        return material == Material.NETHERITE_PICKAXE || material == Material.DIAMOND_PICKAXE || material == Material.IRON_PICKAXE || material == Material.WOODEN_PICKAXE;
    }

    private boolean isItemInCategory(Material itemMaterial, String category) {
        switch (category) {
            case "Pioches":
                return itemMaterial == Material.NETHERITE_PICKAXE || itemMaterial == Material.DIAMOND_PICKAXE || itemMaterial == Material.IRON_PICKAXE || itemMaterial == Material.WOODEN_PICKAXE;
            case "Épées":
                return itemMaterial == Material.NETHERITE_SWORD || itemMaterial == Material.DIAMOND_SWORD || itemMaterial == Material.IRON_SWORD || itemMaterial == Material.WOODEN_SWORD || itemMaterial == Material.GOLDEN_SWORD || itemMaterial == Material.NETHERITE_AXE || itemMaterial == Material.DIAMOND_AXE || itemMaterial == Material.IRON_AXE || itemMaterial == Material.WOODEN_AXE || itemMaterial == Material.GOLDEN_AXE || itemMaterial == Material.BOW;
            case "Armures":
                return itemMaterial == Material.IRON_HELMET || itemMaterial == Material.GOLDEN_BOOTS || itemMaterial == Material.NETHERITE_CHESTPLATE || itemMaterial == Material.DIAMOND_CHESTPLATE || itemMaterial == Material.IRON_CHESTPLATE || itemMaterial == Material.GOLDEN_CHESTPLATE || itemMaterial == Material.NETHERITE_BOOTS || itemMaterial == Material.LEATHER_CHESTPLATE || itemMaterial == Material.LEATHER_BOOTS || itemMaterial == Material.LEATHER_LEGGINGS || itemMaterial == Material.NETHERITE_LEGGINGS || itemMaterial == Material.DIAMOND_LEGGINGS || itemMaterial == Material.IRON_LEGGINGS || itemMaterial == Material.GOLDEN_LEGGINGS || itemMaterial == Material.SHIELD || itemMaterial == Material.NETHERITE_HELMET || itemMaterial == Material.DIAMOND_HELMET || itemMaterial == Material.GOLDEN_HELMET || itemMaterial == Material.LEATHER_HELMET || itemMaterial == Material.IRON_BOOTS || itemMaterial == Material.DIAMOND_BOOTS;
            case "Santé":
                return itemMaterial == Material.POTION;
            case "Pêcher":
                return itemMaterial == Material.FISHING_ROD;
            default:
                return false;
        }
    }
}