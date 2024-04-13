package fr.api.moneycommands;

import fr.api.monster.MonsterDeathListener;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MoneyCommand implements CommandExecutor {
    private final MonsterDeathListener monsterDeathListener;

    public MoneyCommand(MonsterDeathListener monsterDeathListener) {
        this.monsterDeathListener = monsterDeathListener;
    }
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            double playerCoins = monsterDeathListener.getPlayerCoins(player.getUniqueId());
            player.sendMessage("Coins: " + playerCoins);
        }
        return true;
    }
}

