package net.yuukosu.Commands;

import net.yuukosu.Game.GameManager;
import net.yuukosu.Game.GamePhase;
import net.yuukosu.System.PlayerRank;
import net.yuukosu.TeamFight;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GameDebugCommand extends YuukosuCommand {

    public GameDebugCommand() {
        super("gamedebug", PlayerRank.ADMIN);
        super.setCanConsoleExecute(true);
    }

    @Override
    public void run(CommandSender sender, String label, String[] args) {
        if (args.length > 0) {
            GameManager gameManager = TeamFight.getGameManager();

            if (args[0].equalsIgnoreCase("repairlobby")) {
                if (gameManager.getGamePhase() == GamePhase.STARTED) {
                    sender.sendMessage("§aProcessing...");
                    gameManager.repairLobby();
                    sender.sendMessage("§aDone!");

                    if (sender instanceof Player) {
                        Player player = (Player) sender;
                        player.playSound(player.getLocation(), Sound.NOTE_PLING, 3, 2);
                    }
                }

                return;
            }
        }

        sender.sendMessage("§cUsage: /gamedebug (DebugName)");
    }
}
