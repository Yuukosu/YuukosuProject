
package net.yuukosu.Commands;

import net.yuukosu.System.NPC.ClickableNPC;
import net.yuukosu.System.NPC.MinikloonNPC;
import net.yuukosu.System.PlayerRank;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MinikloonCommand extends YuukosuCommand {

    public MinikloonCommand() {
        super("minikloon", PlayerRank.ADMIN);
        super.setCanConsoleExecute(false);
    }

    @Override
    public void run(CommandSender sender, String label, String[] args) {
        Player player = (Player) sender;
        Location location = player.getLocation();
        ClickableNPC npc = new MinikloonNPC(location.getWorld());
        npc.spawnAll(location);
        player.sendMessage("§aBOOP!");
    }
}
