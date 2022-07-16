package net.yuukosu.System.CustomItem;

import org.bukkit.event.block.BlockPlaceEvent;

public abstract class ItemPlaceAction implements ItemAction {

    public ItemPlaceAction() {
    }

    public abstract void onPlace(BlockPlaceEvent e);
}
