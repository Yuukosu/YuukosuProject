package net.yuukosu.Commands;

import net.yuukosu.System.CorePlayer;
import net.yuukosu.System.PlayerRank;
import net.yuukosu.YuukosuCore;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;

public class RankCommand extends YuukosuCommand {

    public RankCommand() {
        super("rank", PlayerRank.ADMIN);
        super.setCanConsoleExecute(true);
    }

    @Override
    public void run(CommandSender sender, String label, String[] args) {
        if (args.length > 1) {
            String playerName = args[0];
            String rankName = args[1];
            PlayerRank playerRank;
            Player player = Bukkit.getPlayerExact(playerName);

            if (player == null) {
                sender.sendMessage("§cプレイヤーは現在オンラインではありません。");
                return;
            }

            try {
                playerRank = PlayerRank.valueOf(rankName.toUpperCase());
            } catch (IllegalArgumentException e) {
                sender.sendMessage("§cRanks: " + Arrays.toString(PlayerRank.values()).replace("[", "").replace("]", ""));
                return;
            }

            if (YuukosuCore.getCoreManager().contains(player)) {
                CorePlayer corePlayer = YuukosuCore.getCoreManager().getCorePlayer(player);
                corePlayer.setPlayerRank(playerRank);
                corePlayer.save();

                sender.sendMessage("§a" + player.getName() + " に " + playerRank.getName() + " ランクを付与しました！");

                if (!sender.equals(player)) {
                    if (sender instanceof Player) {
                        CorePlayer senderPlayer = YuukosuCore.getCoreManager().getCorePlayer((Player) sender);
                        player.sendMessage(senderPlayer.getRankName() + " §aから " + playerRank.getName() + " ランクを付与されました！");
                        return;
                    }

                    player.sendMessage("§a" + playerRank.getName() + " ランクを付与されました！");
                }
            }

            return;
        }

        sender.sendMessage("§cUsage: /rank (PlayerName) (Rank)");
    }
}
