package net.yuukosu.Commands;

import net.yuukosu.System.CorePlayer;
import net.yuukosu.System.PlayerRank;
import net.yuukosu.YuukosuCore;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class StatusCommand extends YuukosuCommand {

    public StatusCommand() {
        super("status", PlayerRank.MODERATOR);
        super.setCanConsoleExecute(true);
    }

    @Override
    public void run(CommandSender sender, String label, String[] args) {
        if (args.length > 0) {
            for (int i = 0; i <= args.length - 1; i++) {
                Player player = Bukkit.getPlayerExact(args[i]);

                if (YuukosuCore.getCoreManager().contains(player)) {
                    CorePlayer corePlayer = YuukosuCore.getCoreManager().getCorePlayer(player);

                    sender.sendMessage(corePlayer.getRankName() + "§a's status");
                    sender.sendMessage("   ランク: §a" + corePlayer.getPlayerRank().getColoredName());
                    sender.sendMessage("   招待コード: §a" + (corePlayer.getInviteCode() != null ? corePlayer.getInviteCode().getCode() : "§cNone"));
                } else {
                    sender.sendMessage("§c" + player.getName() + " はサーバーに登録されていません。");
                }
            }

            return;
        }

        sender.sendMessage("§cUsage: /status (PlayerName...)");
    }
}
