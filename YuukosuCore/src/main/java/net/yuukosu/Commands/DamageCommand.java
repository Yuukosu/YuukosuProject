package net.yuukosu.Commands;

import net.yuukosu.System.PlayerRank;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class DamageCommand extends YuukosuCommand {

    public DamageCommand() {
        super("damage", PlayerRank.ADMIN);
        super.setCanConsoleExecute(true);
    }

    @Override
    public void run(CommandSender sender, String label, String[] args) {
        if (args.length > 1) {
            double damage;

            try {
                damage = Double.parseDouble(args[0]);
            } catch (IllegalArgumentException e) {
                sender.sendMessage("§c正しい引数を入力してください。");
                return;
            }

            for (int i = 1; i <= args.length - 1; i++) {
                Player player = Bukkit.getPlayerExact(args[i]);

                if (player != null) {
                    player.damage(damage);
                    sender.sendMessage("§e" + player.getName() + " §cIS TAKE §e" + String.format("%,d", Math.round(damage)) + " §cDAMAGE!");
                }
            }

            return;
        }

        sender.sendMessage("§cUsage: /damage (Damage Amount) (PlayerName...)");
    }
}
