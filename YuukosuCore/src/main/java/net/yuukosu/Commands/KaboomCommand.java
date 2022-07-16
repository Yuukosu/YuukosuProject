package net.yuukosu.Commands;

import net.yuukosu.System.PlayerRank;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class KaboomCommand extends YuukosuCommand {

    public KaboomCommand() {
        super("kaboom", PlayerRank.ADMIN);
        super.setCanConsoleExecute(true);
    }

    @Override
    public void run(CommandSender sender, String label, String[] args) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.setVelocity(new Vector(0, 10, 0));
            player.getWorld().strikeLightningEffect(player.getLocation());
            player.getWorld().strikeLightningEffect(player.getLocation());
            player.getWorld().strikeLightningEffect(player.getLocation());
            sender.sendMessage("§aLaunched " + player.getName());
        }
    }
}
