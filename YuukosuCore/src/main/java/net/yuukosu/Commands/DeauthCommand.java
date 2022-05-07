package net.yuukosu.Commands;

import net.yuukosu.System.CorePlayer;
import net.yuukosu.System.PlayerRank;
import net.yuukosu.YuukosuCore;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class DeauthCommand extends YuukosuCommand {

    public DeauthCommand() {
        super("deauth", PlayerRank.ADMIN);
        super.setCanConsoleExecute(true);
    }

    @Override
    public void run(CommandSender sender, String label, String[] args) {
        if (args.length > 0) {
            Player player = Bukkit.getPlayerExact(args[0]);

            if (player == null) {
                sender.sendMessage("§cプレイヤーは現在オンラインではありません。");
                return;
            }

            if (YuukosuCore.getCoreManager().contains(player)) {
                CorePlayer corePlayer = YuukosuCore.getCoreManager().getCorePlayer(player);
                corePlayer.setInviteCode(null);
                corePlayer.save();
                player.kickPlayer("§c管理者によって、認証が解除されました。");
                sender.sendMessage("§a" + player.getName() + " の認証を解除しました。");
            }
        }
    }
}
