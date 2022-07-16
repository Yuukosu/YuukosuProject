package net.yuukosu.Commands;

import net.yuukosu.System.CorePlayer;
import net.yuukosu.System.PlayerRank;
import net.yuukosu.YuukosuCore;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;

public class LobbyCommand extends YuukosuCommand {

    public LobbyCommand() {
        super("lobby", PlayerRank.DEFAULT);
        super.setCanConsoleExecute(false);
        super.setAliases(Arrays.asList("hub", "l", "quit", "exit", "leave"));
    }

    @Override
    public void run(CommandSender sender, String label, String[] args) {
        Player player = (Player) sender;
        CorePlayer corePlayer = YuukosuCore.getCoreManager().getCorePlayer(player);

        player.sendMessage("§7サーバーへ接続中...");
        corePlayer.sendServer("lobby");
    }
}
