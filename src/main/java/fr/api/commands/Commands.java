package fr.api.commands;

import fr.api.menu.Menu;
import fr.api.monster.MonsterDeathListener;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public class Commands implements CommandExecutor {

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
            if(cmd.getName().equalsIgnoreCase("alert")) {

                if(args.length == 0) {
                    player.sendMessage("la commande est : /alert <message>");
                }

                if(args.length >= 1) {
                    StringBuilder bc = new StringBuilder();
                    for(String part : args) {
                        bc.append(part + " ");
                    }
                    Bukkit.broadcastMessage("[" + player.getName() + "] " + bc.toString());
                }

            }

            if (cmd.getName().equalsIgnoreCase("stats")) {
                int zombieKills = MonsterDeathListener.getMonsterKills(player, "ZOMBIE");
                int skeletonKills = MonsterDeathListener.getMonsterKills(player, "SKELETON");
                int creeperKills = MonsterDeathListener.getMonsterKills(player, "CREEPER");
                int spiderKills = MonsterDeathListener.getMonsterKills(player, "SPIDER");

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
        }
        return false;
    }
}
