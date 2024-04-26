package fr.api.commands;

import fr.api.InventoryManager.InventoryManager;
import fr.api.menu.Menu;
import fr.api.monster.MonsterDeathListener;
import fr.api.pluginmc.Main;
import fr.api.utils.VaultUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scoreboard.*;

import java.util.ArrayList;

public class Commands implements CommandExecutor {

    private final MonsterDeathListener monsterDeathListener;
    private Main plugin;

    public Commands(MonsterDeathListener monsterDeathListener, Main main) {
        this.monsterDeathListener = monsterDeathListener;
        this.plugin = main;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            if (cmd.getName().equalsIgnoreCase("test")) {
                player.sendMessage("§4Bravo tu as réussi le test");
                return true;
            }
            if (cmd.getName().equalsIgnoreCase("pute")) {
                player.sendMessage("§4Bravo tu as réussi le test");
                return true;
            }
            if (cmd.getName().equalsIgnoreCase("alert")) {
                if (args.length == 0) {
                    player.sendMessage("la commande est : /alert <message>");
                    return true;
                }

                StringBuilder bc = new StringBuilder();
                for (String part : args) {
                    bc.append(part).append(" ");
                }
                Bukkit.broadcastMessage("[" + player.getName() + "] " + bc.toString());
                return true;
            }

            if (cmd.getName().equalsIgnoreCase("stats")) {
                int zombieKills = monsterDeathListener.getMonsterKills(player, "ZOMBIE");
                int skeletonKills = monsterDeathListener.getMonsterKills(player, "SKELETON");
                int creeperKills = monsterDeathListener.getMonsterKills(player, "CREEPER");
                int spiderKills = monsterDeathListener.getMonsterKills(player, "SPIDER");

                player.sendMessage("Nombre de zombies tués : " + zombieKills);
                player.sendMessage("Nombre de squelettes tués : " + skeletonKills);
                player.sendMessage("Nombre de creeper tués : " + creeperKills);
                player.sendMessage("Nombre de araignées tués : " + spiderKills);
                return true;
            }
            if (cmd.getName().equalsIgnoreCase("shop")) {
                Menu.openCustomMenu(player);
                return true;
            }

            if (cmd.getName().equalsIgnoreCase("sb")) {
                double playerCoins = monsterDeathListener.getPlayerCoins(player.getUniqueId());

                ScoreboardManager manager = Bukkit.getScoreboardManager();
                Scoreboard scoreboard = manager.getNewScoreboard();

                Objective objective = scoreboard.registerNewObjective("Title", "dummy");
                objective.setDisplaySlot(DisplaySlot.SIDEBAR);
                objective.setDisplayName(ChatColor.BLUE + "Scoreboard Title");

                Score score = objective.getScore(ChatColor.GOLD + "Money: $" + ChatColor.GREEN + playerCoins);
                Score s2 = objective.getScore("");
                Score s3 = objective.getScore(ChatColor.DARK_PURPLE + "https://google.com");

                score.setScore(3);
                s2.setScore(2);
                s3.setScore(1);

                player.setScoreboard(scoreboard);
                return true;
            }

            if (args.length > 0) {
                Player p = (Player) sender;

                if (args[0].equalsIgnoreCase("open")) {
                    ArrayList<ItemStack> vaultItems = VaultUtils.getItems(p);

                    Inventory vault = Bukkit.createInventory(p, 54, "Your personal Vault");

                    vaultItems.forEach(vault::addItem);

                    p.openInventory(vault);
                    return true;
                }
            }

            if (plugin.getConfig().getBoolean("enable")) {
                if (sender instanceof Player) {
                    if (player.hasPermission("staffhomes.use")) {
                        if (args.length == 1 && args[0].equalsIgnoreCase("set")) {
                            if (plugin.getConfig().isConfigurationSection("savedlocations." + player.getName())) {
                                player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("override-message") + plugin.getConfig().getInt("savedlocations." + player.getName() + ".x") + " " + plugin.getConfig().getInt("savedlocations." + player.getName() + ".y") + " " + plugin.getConfig().getInt("savedlocations." + player.getName() + ".z")));
                                saveLocation(player);
                            } else {
                                saveLocation(player);
                            }
                        } else if (args.length == 1 && args[0].equalsIgnoreCase("return")) {
                            if (plugin.getConfig().isConfigurationSection("savedlocations." + player.getName())) {
                                Location return_location = new Location(player.getWorld(), plugin.getConfig().getInt("savedlocations." + player.getName() + ".x"), plugin.getConfig().getInt("savedlocations." + player.getName() + ".y"), plugin.getConfig().getInt("savedlocations." + player.getName() + ".z"));
                                player.teleport(return_location);
                                player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("return-message")));
                                plugin.getConfig().set("savedlocations." + player.getName(), null);
                                plugin.saveConfig();
                            } else {
                                player.sendMessage(ChatColor.DARK_RED + "You never set a staff home.");
                            }
                        } else if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
                            if (player.hasPermission("staffhomes.reload")) {
                                plugin.reloadConfig();
                                player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("reload-message")));
                            } else {
                                player.sendMessage(ChatColor.DARK_RED + "You don't have permission to use this command.");
                            }
                        } else if (args.length == 1) {
                            if (player.hasPermission("staffhomes.admin")) {
                                Player target = Bukkit.getPlayer(args[0]);
                                if (target != null) {
                                    if (plugin.getConfig().isConfigurationSection("savedlocations." + target.getName())) {
                                        player.sendMessage(ChatColor.GREEN + "Teleporting to temporary staff home(" + target.getName() + ") @: " + ChatColor.GRAY + plugin.getConfig().getInt("savedlocations." + target.getName() + ".x") + " " + plugin.getConfig().getInt("savedlocations." + target.getName() + ".y") + " " + plugin.getConfig().getInt("savedlocations." + target.getName() + ".z"));
                                        Location return_location = new Location(target.getWorld(), plugin.getConfig().getInt("savedlocations." + target.getName() + ".x"), plugin.getConfig().getInt("savedlocations." + target.getName() + ".y"), plugin.getConfig().getInt("savedlocations." + target.getName() + ".z"));
                                        player.teleport(return_location);
                                    } else {
                                        player.sendMessage(ChatColor.DARK_RED + "That player does not have a home set.");
                                    }
                                }
                            } else {
                                player.sendMessage(ChatColor.DARK_RED + "You don't have permission to use this command.");
                            }
                        } else {
                            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7==&a&lStaff&eHomes&7 by Illuminatiiiiii=="));
                            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6&o/staffhome set &7- &9Set a Temporary Home"));
                            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6&o/staffhome return &7- &9Return to Home and Remove it"));
                            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6&o/staffhome <name> &7- &9Teleport to a temporary home"));
                            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6&o/staffhome reload &7- &9Reload the configuration"));
                            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7========================="));
                        }
                    } else {
                        player.sendMessage(ChatColor.DARK_RED + "You don't have permission to use this command.");
                    }
                } else {
                    System.out.println("A player must execute this command.");
                }
                return true;
            }
        }
        return true;
    }

    private void saveLocation(Player p) {
        Location l = p.getLocation();
        p.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("set-message") + Math.round(l.getX()) + " " + Math.round(l.getY()) + " " + Math.round(l.getZ())));
        plugin.getConfig().createSection("savedlocations." + p.getName());
        plugin.getConfig().set("savedlocations." + p.getName() + ".x", l.getX());
        plugin.getConfig().set("savedlocations." + p.getName() + ".y", l.getY());
        plugin.getConfig().set("savedlocations." + p.getName() + ".z", l.getZ());
        plugin.saveConfig();
    }
}
