package net.yuukosu.Commands;

import net.yuukosu.Game.EnumTeam;
import net.yuukosu.Game.GameManager;
import net.yuukosu.Game.GamePhase;
import net.yuukosu.System.PlayerRank;
import net.yuukosu.TeamFight;
import org.bukkit.command.CommandSender;

public class VictoryCommand extends YuukosuCommand {

    public VictoryCommand() {
        super("victory", PlayerRank.ADMIN);
        super.setCanConsoleExecute(true);
    }

    @Override
    public void run(CommandSender sender, String label, String[] args) {
        GameManager gameManager = TeamFight.getGameManager();

        if (args.length > 0) {
            EnumTeam enumTeam;

            try {
                enumTeam = EnumTeam.valueOf(args[0]);
            } catch (IllegalArgumentException e) {
                sender.sendMessage("§c正しい引数を入力してください。");
                return;
            }

            if (gameManager.getGamePhase() == GamePhase.STARTED && gameManager.getArenaManager().isRegistered(enumTeam)) {
                gameManager.victory(gameManager.getGameTeam(enumTeam));
            }
        }
    }
}
