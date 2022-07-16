package net.yuukosu.Commands;

import net.yuukosu.System.CorePlayer;
import net.yuukosu.System.GuiCreator.ExampleGui;
import net.yuukosu.System.PlayerRank;
import net.yuukosu.YuukosuCore;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ExampleCommand extends YuukosuCommand {

    public ExampleCommand() {
        super("example", PlayerRank.DEFAULT);
        super.setCanConsoleExecute(false);
    }

    @Override
    public void run(CommandSender sender, String label, String[] args) {
        Player player = (Player) sender;
        CorePlayer corePlayer = YuukosuCore.getCoreManager().getCorePlayer(player);
        corePlayer.openGui(new ExampleGui());
        player.sendMessage("§aEnjoy!");
    }
}
