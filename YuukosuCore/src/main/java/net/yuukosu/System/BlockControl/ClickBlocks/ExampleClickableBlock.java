package net.yuukosu.System.BlockControl.ClickBlocks;

import net.yuukosu.System.BlockControl.BlockData;
import net.yuukosu.System.BlockControl.BlockDataEx;
import net.yuukosu.System.BlockControl.ClickableBlock;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;

public class ExampleClickableBlock extends BlockDataEx implements ClickableBlock {

    @SuppressWarnings("deprecation")
    public ExampleClickableBlock(Location location) {
        super(location, new BlockData(Material.SPONGE, location.getBlock().getData()));
    }

    @Override
    public void onClick(PlayerInteractEvent e) {
        Player player = e.getPlayer();
        player.sendMessage("§aDONE!");
        player.playSound(player.getLocation(), Sound.ORB_PICKUP, 3F, 1F);
    }
}
