package fr.api.artefact;

import java.util.Arrays;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
public class ArtefactItemsListener implements Listener {

    private final ItemStack customsword = new ItemStack(Material.NETHERITE_SWORD, 1);
    private final ItemStack customBoots = new ItemStack(Material.NETHERITE_BOOTS, 1);
    private final ItemStack customleggins = new ItemStack(Material.NETHERITE_LEGGINGS, 1);
    private final ItemStack customchestplate = new ItemStack(Material.NETHERITE_CHESTPLATE, 1);
    private final ItemStack customemerald = new ItemStack(Material.EMERALD, 1);

    public ArtefactItemsListener() {
        ItemMeta customSwordMeta = customsword.getItemMeta();
        customSwordMeta.setDisplayName("§4Hercule");
        customSwordMeta.setLore(Arrays.asList("Un Artefact "));
        customSwordMeta.addEnchant(Enchantment.DAMAGE_ALL, 250, true);
        customsword.setItemMeta(customSwordMeta);

        ItemMeta customBootsMeta = customBoots.getItemMeta();
        customBootsMeta.setDisplayName("§6Hermès ");
        customBootsMeta.setLore(Arrays.asList("§7Un Artefact"));
        customBoots.setItemMeta(customBootsMeta);

        ItemMeta customChestplateMeta = customchestplate.getItemMeta();
        customChestplateMeta.setDisplayName("§6Tank ");
        customChestplateMeta.setLore(Arrays.asList("§7Des plastrons très confortables", "§7et ultra résistantes !"));
        customchestplate.setItemMeta(customChestplateMeta);

        ItemMeta customLegginsMeta = customleggins.getItemMeta();
        customLegginsMeta.setDisplayName("§6Tank");
        customLegginsMeta.setLore(Arrays.asList("§7Des jambières très confortables", "§7et ultra résistantes !"));
        customleggins.setItemMeta(customLegginsMeta);

        ItemMeta customEmeraldMeta = customemerald.getItemMeta();
        customEmeraldMeta.setDisplayName("Shop");
        customEmeraldMeta.setLore(Arrays.asList(""));
        customemerald.setItemMeta(customEmeraldMeta);

    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        ItemStack boots = player.getInventory().getBoots();

        if (boots != null && boots.hasItemMeta() && boots.getItemMeta().getDisplayName().equals("§6Hermès ")) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 20, 1));
        }
    }

    /*@EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        player.getInventory().clear();

        player.getInventory().addItem(customsword);
        player.getInventory().addItem(customchestplate);
        player.getInventory().addItem(customleggins);
        player.getInventory().addItem(customemerald);

        boolean hasCustomBoots = false;
        for (ItemStack item : player.getInventory().getContents()) {
            if (item != null && item.getType() == Material.NETHERITE_BOOTS && item.getItemMeta().getDisplayName().equals("§6Hermès ")) {
                hasCustomBoots = true;
                break;
            }
        }
        if (!hasCustomBoots) {
            player.getInventory().addItem(customBoots);
        }

        player.updateInventory();
    }

     */
}
