package fr.api.pluginmc;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class Menu extends JavaPlugin {

    private final Map<Material, Supplier<ItemStack>> materialCreators = new HashMap<>();

    public Menu() {
        materialCreators.put(Material.DIAMOND_PICKAXE, this::customPickaxeDiamond);
        materialCreators.put(Material.NETHERITE_SWORD, this::customSwordNetherite);
        materialCreators.put(Material.WOODEN_PICKAXE, this::customPickaxeWooden);
        materialCreators.put(Material.GOLDEN_PICKAXE, this::customPickaxeGolden);
        materialCreators.put(Material.IRON_PICKAXE, this::customPickaxeIron);
        materialCreators.put(Material.NETHERITE_HELMET, this::customHelmet);
        materialCreators.put(Material.IRON_BOOTS, this::customBootsIron);
        materialCreators.put(Material.IRON_LEGGINGS, this::customLegginsIron);
        materialCreators.put(Material.GOLDEN_BOOTS, this::custombootsGold);
        materialCreators.put(Material.NETHERITE_PICKAXE, this::custompickaxe);
        materialCreators.put(Material.GOLDEN_LEGGINGS, this::customlegginsGold);
        materialCreators.put(Material.LEATHER_LEGGINGS, this::customlegginsCopper);
        materialCreators.put(Material.DIAMOND_LEGGINGS, this::customlegginsDiamond);
        materialCreators.put(Material.BOW, Menu::customBow);
        materialCreators.put(Material.NETHERITE_AXE, this::customaxenetherite);
        materialCreators.put(Material.DIAMOND_AXE, this::customaxediamond);
        materialCreators.put(Material.IRON_AXE, this::customaxeiron);
        materialCreators.put(Material.GOLDEN_AXE, this::customaxegolden);
        materialCreators.put(Material.WOODEN_AXE, this::customaxewood);
        materialCreators.put(Material.NETHERITE_SWORD, Menu::customsword);
        materialCreators.put(Material.GOLDEN_SWORD, Menu::customswordgolden);
        materialCreators.put(Material.IRON_SWORD, Menu::customswordiron);
        materialCreators.put(Material.WOODEN_SWORD, Menu::customswordwood);
        materialCreators.put(Material.DIAMOND_SWORD, Menu::customswordiamond);
        materialCreators.put(Material.NETHERITE_BOOTS, this::customboots);
        materialCreators.put(Material.NETHERITE_LEGGINGS, this::customleggins);
        materialCreators.put(Material.NETHERITE_CHESTPLATE, Menu::customchestplate);
        materialCreators.put(Material.SHIELD, Menu::customshield);
        materialCreators.put(Material.LEATHER_BOOTS, this::custombootsCopper);
        materialCreators.put(Material.DIAMOND_CHESTPLATE, Menu::customchestplateDiamond);
        materialCreators.put(Material.GOLDEN_CHESTPLATE, Menu::customchestplateGold);
        materialCreators.put(Material.LEATHER_CHESTPLATE, Menu::customchestplateCopper);
        materialCreators.put(Material.IRON_CHESTPLATE, Menu::customchestplateIron);
        materialCreators.put(Material.DIAMOND_HELMET, Menu::customhelmetDiamond);
        materialCreators.put(Material.NETHERITE_HELMET, Menu::customhelmet);
        materialCreators.put(Material.GOLDEN_HELMET, Menu::customhelmetGold);
        materialCreators.put(Material.LEATHER_HELMET, Menu::customhelmetCopper);
        materialCreators.put(Material.IRON_HELMET, Menu::customhelmetIron);
    }

    // Méthodes de création des ItemStack
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

    // Méthode pour créer un ItemStack avec des attributs prédéfinis
    private static ItemStack createArmorItem(Material material, String displayName, String... lore) {
        ItemStack itemStack = new ItemStack(material);
        // Vous devrez implémenter une méthode pour définir le nom et la description de l'item
        // Par exemple:
        // ItemMeta meta = itemStack.getItemMeta();
        // meta.setDisplayName(displayName);
        // meta.setLore(Arrays.asList(lore));
        // itemStack.setItemMeta(meta);
        return itemStack;
    }

    // Méthode pour récupérer un ItemStack pour un matériau donné
    public ItemStack getCustomItem(Material material) {
        Supplier<ItemStack> creator = materialCreators.get(material);
        if (creator != null) {
            return creator.get();
        } else {
            // Retourner un ItemStack vide si aucun créateur n'est trouvé pour ce matériau
            return new ItemStack(material);
        }
    }

    // Méthode principale (utilisée uniquement pour des tests)
    public static void main(String[] args) {
        Menu menu = new Menu();

        // Afficher tous les ItemStack créés
        for (Material material : menu.materialCreators.keySet()) {
            ItemStack itemStack = menu.getCustomItem(material);
            System.out.println("Material: " + material);
            System.out.println("ItemStack: " + itemStack);
            // Vous pouvez faire plus avec l'ItemStack si nécessaire
        }
    }
}
