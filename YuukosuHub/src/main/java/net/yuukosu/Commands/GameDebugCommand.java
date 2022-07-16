package net.yuukosu.Commands;

import net.yuukosu.System.PlayerRank;
import org.bukkit.command.CommandSender;

import java.util.Collections;

public class GameDebugCommand extends YuukosuCommand {

    public GameDebugCommand() {
        super("gamedebug", PlayerRank.ADMIN);
        super.setCanConsoleExecute(true);
        super.setAliases(Collections.singletonList("gdb"));
    }

    @Override
    public void run(CommandSender sender, String label, String[] args) {
    }
}
