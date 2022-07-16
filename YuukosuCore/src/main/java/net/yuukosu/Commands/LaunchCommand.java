package net.yuukosu.Commands;

import net.yuukosu.System.PlayerRank;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class LaunchCommand extends YuukosuCommand {

    public LaunchCommand() {
        super("launch", PlayerRank.ADMIN);
        super.setCanConsoleExecute(true);
    }

    @Override
    public void run(CommandSender sender, String label, String[] args) {
        if (args.length > 0) {
            for (int i = 0; i <= (args.length - 1); i++) {
                String name = args[i];
                Player player = Bukkit.getPlayerExact(name);

                if (player != null) {
                    player.setVelocity(new Vector(0, 10, 0));
                    player.getWorld().strikeLightningEffect(player.getLocation());
                    sender.sendMessage("§aLaunched " + player.getName());
                }
            }

            return;
        }

        sender.sendMessage("§cUsage: /launch (PlayerName...)");
    }
}
