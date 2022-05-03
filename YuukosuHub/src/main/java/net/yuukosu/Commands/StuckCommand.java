package net.yuukosu.Commands;

import net.yuukosu.System.PlayerRank;
import net.yuukosu.YuukosuHub;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class StuckCommand extends YuukosuCommand {

    public StuckCommand() {
        super("stuck", PlayerRank.DEFAULT);
        super.setCanConsoleExecute(false);
    }

    @Override
    public void run(CommandSender sender, String label, String[] args) {
        Player player = (Player) sender;

        if (YuukosuHub.getHubManager().getHubData().getSpawn() != null) {
            player.teleport(YuukosuHub.getHubManager().getHubData().getSpawn());
        }
    }
}
