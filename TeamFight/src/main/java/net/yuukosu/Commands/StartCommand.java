package net.yuukosu.Commands;

import net.yuukosu.Game.GameManager;
import net.yuukosu.Game.GamePhase;
import net.yuukosu.System.PlayerRank;
import net.yuukosu.TeamFight;
import org.bukkit.command.CommandSender;

public class StartCommand extends YuukosuCommand {

    public StartCommand() {
        super("start", PlayerRank.ADMIN);
        super.setCanConsoleExecute(true);
    }

    @Override
    public void run(CommandSender sender, String label, String[] args) {
        GameManager gameManager = TeamFight.getGameManager();

        if (gameManager.getGamePhase() == GamePhase.WAITING && !gameManager.getPlayers().isEmpty() && gameManager.getArenaManager().complete()) {
            gameManager.startCount(15, true);
            return;
        }

        sender.sendMessage("§cゲームをスタートできません。");
    }
}
