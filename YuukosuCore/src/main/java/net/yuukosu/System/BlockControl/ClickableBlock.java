package net.yuukosu.System.BlockControl;

import org.bukkit.event.player.PlayerInteractEvent;

public interface ClickableBlock {
    void onClick(PlayerInteractEvent e);
}
