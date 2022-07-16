package net.yuukosu.Commands;

import net.yuukosu.System.PlayerRank;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

public class StrikeCommand extends YuukosuCommand {

    public StrikeCommand() {
        super("strike", PlayerRank.ADMIN);
        super.setCanConsoleExecute(true);
    }

    @Override
    public void run(CommandSender sender, String label, String[] args) {
        Bukkit.getOnlinePlayers().forEach(player -> {
            player.getWorld().strikeLightningEffect(player.getLocation());
            player.getWorld().strikeLightningEffect(player.getLocation());
            player.getWorld().strikeLightningEffect(player.getLocation());
        });

        sender.sendMessage("§aBoom!");
    }
}
