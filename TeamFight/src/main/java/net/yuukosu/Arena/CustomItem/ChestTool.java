package net.yuukosu.Arena.CustomItem;

import lombok.Getter;
import net.yuukosu.Arena.ArenaManager;
import net.yuukosu.System.CustomItem.CustomItem;
import net.yuukosu.System.CustomItem.ItemPlaceAction;
import net.yuukosu.System.ItemCreator;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockPlaceEvent;

public class ChestTool extends CustomItem {

    @Getter
    private final ArenaManager arenaManager;

    public ChestTool(ArenaManager arenaManager) {
        super("CHEST_TOOL", new ItemCreator(Material.CHEST).setDisplayName("§aチェストツール").create());
        super.setItemAction(new PlaceAction());
        this.arenaManager = arenaManager;
    }

    private final class PlaceAction extends ItemPlaceAction {

        @Override
        public void onPlace(BlockPlaceEvent e) {
            e.setCancelled(true);

            Player player = e.getPlayer();
            ArenaManager arenaManager = ChestTool.this.arenaManager;
            Location location = e.getBlockPlaced().getLocation();

            if (player.isOp()) {
                if (!arenaManager.containsChestLocation(location)) {
                    arenaManager.addChestLocation(location);
                    arenaManager.save();
                    player.sendMessage("§aAdded LuckyChest Location.");
                    return;
                }

                arenaManager.removeChestLocation(location);
                arenaManager.save();
                player.sendMessage("§aDeleted.");
            }
        }
    }
}
