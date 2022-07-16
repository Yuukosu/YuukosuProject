package net.yuukosu.Commands;

import net.yuukosu.System.PlayerRank;
import net.yuukosu.YuukosuHub;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SetSpawnCommand extends YuukosuCommand {

    public SetSpawnCommand() {
        super("setspawn", PlayerRank.ADMIN);
        super.setCanConsoleExecute(false);
    }

    @Override
    public void run(CommandSender sender, String label, String[] args) {
        Player player = (Player) sender;
        YuukosuHub.getHubManager().getHubData().setSpawn(player.getLocation());
        YuukosuHub.getHubManager().getHubData().save();

        sender.sendMessage("§aスポーン地点を設定しました。");
    }
}
