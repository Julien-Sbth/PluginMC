package fr.api.menu;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ItemUtils {
    public static ItemStack createArmorItem(Material material, String displayName, String... lore) {
        ItemStack armorItem = new ItemStack(material, 1);
        ItemMeta meta = armorItem.getItemMeta();
        meta.setDisplayName(displayName);
        meta.setLore(Arrays.asList(lore));
        armorItem.setItemMeta(meta);
        return armorItem;
    }
}
