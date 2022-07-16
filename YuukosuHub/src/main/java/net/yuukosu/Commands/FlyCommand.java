package net.yuukosu.Commands;

import net.yuukosu.System.PlayerRank;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class FlyCommand extends YuukosuCommand {

    public FlyCommand() {
        super("fly", PlayerRank.VIP);
        super.setCanConsoleExecute(false);
    }

    @Override
    public void run(CommandSender sender, String label, String[] args) {
        Player player = (Player) sender;
        player.setAllowFlight(!player.getAllowFlight());
        player.sendMessage(ChatColor.GREEN + (player.getAllowFlight() ? "飛行が有効になりました！" : "飛行が無効になりました！"));
    }
}
