package fr.api.menu;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import fr.api.menu.Menu;

import java.util.*;

import static fr.api.menu.Menu.getPrice;


public class ArtefactItems {

    static ItemStack createItem(Material material, String displayName, String... lore) {
        if (!isDisplayedItem(material)) {
            return new ItemStack(material);
        }

        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();

        Map<Material, String> displayNames = new HashMap<>();
        displayNames.put(Material.NETHERITE_SWORD, "§4Kratos");
        displayNames.put(Material.DIAMOND_SWORD, "§6Diamond Sword");
        displayNames.put(Material.IRON_SWORD, "§6Iron Sword");
        displayNames.put(Material.GOLDEN_SWORD, "§6Golden Sword");
        displayNames.put(Material.WOODEN_SWORD, "§6Sword Wood");
        displayNames.put(Material.STONE_SWORD, "§6Stone Sword");
        displayNames.put(Material.GOLDEN_HELMET, "§6Golden Helmet");
        displayNames.put(Material.GOLDEN_CHESTPLATE, "§6Golden Chestplate");
        displayNames.put(Material.GOLDEN_LEGGINGS, "§6Golden Leggins");
        displayNames.put(Material.GOLDEN_BOOTS, "§6Hermès");
        displayNames.put(Material.NETHERITE_HELMET, "§6Néthérite Helmet");
        displayNames.put(Material.NETHERITE_CHESTPLATE, "§6Néthérite Chestplate");
        displayNames.put(Material.NETHERITE_LEGGINGS, "§6Néthérite Leggins");
        displayNames.put(Material.NETHERITE_BOOTS, "§6Néthérite Boot");
        displayNames.put(Material.DIAMOND_HELMET, "§6Diamond Helmet");
        displayNames.put(Material.DIAMOND_CHESTPLATE, "§6Diamond Chestplate");
        displayNames.put(Material.DIAMOND_LEGGINGS, "§6Diamond Leggins");
        displayNames.put(Material.DIAMOND_BOOTS, "§6Golden Boots");
        displayNames.put(Material.IRON_HELMET, "§6Iron Helmet");
        displayNames.put(Material.IRON_CHESTPLATE, "§6Iron Chestplate");
        displayNames.put(Material.IRON_LEGGINGS, "§6Iron Leggins");
        displayNames.put(Material.IRON_BOOTS, "§6Iron Boots");
        displayNames.put(Material.LEATHER_HELMET, "§6Leather Helmet");
        displayNames.put(Material.LEATHER_CHESTPLATE, "§6Leather Chestplate");
        displayNames.put(Material.LEATHER_LEGGINGS, "§6Leather Leggings");
        displayNames.put(Material.LEATHER_BOOTS, "§6Leather Boots");
        displayNames.put(Material.NETHERITE_AXE, "§6Nétherite Axe");
        displayNames.put(Material.DIAMOND_AXE, "§6Diamond Axe");
        displayNames.put(Material.IRON_AXE, "§6Iron Axe");
        displayNames.put(Material.GOLDEN_AXE, "§6Golden Axe");
        displayNames.put(Material.STONE_AXE, "§6Stone Axe");
        displayNames.put(Material.WOODEN_AXE, "§6Wood Axe");
        displayNames.put(Material.NETHERITE_PICKAXE, "§6Nétherite Pickaxe");
        displayNames.put(Material.DIAMOND_PICKAXE, "§fils de pute");
        displayNames.put(Material.IRON_PICKAXE, "§6Iron Pickaxe");
        displayNames.put(Material.GOLDEN_PICKAXE, "§6Golden Pickaxe");
        displayNames.put(Material.STONE_PICKAXE, "§6Stone Pickaxe");
        displayNames.put(Material.WOODEN_PICKAXE, "§6Wood Pickaxe");
        displayNames.put(Material.SHIELD, "§6Shield");
        displayNames.put(Material.BOW, "§6ARC DE LA MORT");

        if (displayNames.containsKey(material)) {
            meta.setDisplayName(displayNames.get(material));
            List<String> itemLore = new ArrayList<>(Arrays.asList("Prix: " + getPrice(material) + "coins"));
            itemLore.addAll(Arrays.asList(lore));
            meta.setLore(itemLore);
        }

        item.setItemMeta(meta);
        return item;
    }

    private static boolean isDisplayedItem(Material material) {
        List<Material> displayedItems = Arrays.asList(
                Material.NETHERITE_SWORD, Material.NETHERITE_BOOTS, Material.DIAMOND_SWORD,
                Material.GOLDEN_BOOTS, Material.LEATHER_BOOTS, Material.WOODEN_SWORD,
                Material.IRON_SWORD, Material.GOLDEN_SWORD, Material.NETHERITE_LEGGINGS,
                Material.NETHERITE_CHESTPLATE, Material.LEATHER_LEGGINGS, Material.WOODEN_SWORD,
                Material.GOLDEN_SWORD, Material.NETHERITE_HELMET,
                Material.IRON_HELMET, Material.GOLDEN_HELMET, Material.DIAMOND_HELMET,
                Material.LEATHER_HELMET, Material.NETHERITE_HELMET, Material.LEATHER_LEGGINGS,
                Material.NETHERITE_PICKAXE, Material.DIAMOND_PICKAXE, Material.DIAMOND_AXE,
                Material.NETHERITE_AXE, Material.IRON_AXE, Material.GOLDEN_AXE,
                Material.WOODEN_AXE, Material.STONE_AXE, Material.BOW,
                Material.IRON_BOOTS, Material.DIAMOND_BOOTS, Material.DIAMOND_CHESTPLATE,
                Material.GOLDEN_CHESTPLATE, Material.LEATHER_CHESTPLATE, Material.IRON_CHESTPLATE,
                Material.DIAMOND_LEGGINGS, Material.IRON_LEGGINGS, Material.GOLDEN_LEGGINGS, Material.SHIELD,
                Material.IRON_PICKAXE, Material.WOODEN_PICKAXE, Material.STONE_PICKAXE, Material.RED_STAINED_GLASS_PANE,
                Material.DIAMOND_PICKAXE,
                Material.NETHERITE_SWORD,
                Material.FISHING_ROD,
                Material.NETHERITE_CHESTPLATE,
                Material.POTION
        );

        return displayedItems.contains(material);
    }
}