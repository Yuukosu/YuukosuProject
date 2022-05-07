package net.yuukosu.System.BlockControl.ClickBlocks;

import net.minecraft.server.v1_8_R3.*;
import net.yuukosu.System.BlockControl.BlockData;
import net.yuukosu.System.BlockControl.BlockDataEx;
import net.yuukosu.System.BlockControl.ClickableBlock;
import net.yuukosu.YuukosuCore;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class ChestExample extends BlockDataEx implements ClickableBlock {

    @SuppressWarnings("deprecation")
    public ChestExample(Location location) {
        super(location, new BlockData(Material.CHEST, location.getBlock().getData()));
    }

    @Override
    public void onClick(PlayerInteractEvent e) {
        Player player = e.getPlayer();
        Block block = e.getClickedBlock();

        if (block != null && block.getType() == Material.CHEST) {
            if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
                e.setCancelled(true);

                Location location = block.getLocation();
                EntityItem entityItem = new EntityItem(((CraftWorld) location.getWorld()).getHandle());
                entityItem.setItemStack(CraftItemStack.asNMSCopy(new ItemStack(Material.BAKED_POTATO)));
                entityItem.setPosition(location.getX() + 0.5D, location.getY() + 1.0D, location.getZ() + 0.5D);
                BlockPosition position = new BlockPosition(location.getBlockX(), location.getBlockY(), location.getBlockZ());
                PacketPlayOutBlockAction packet1 = new PacketPlayOutBlockAction(position, net.minecraft.server.v1_8_R3.Block.getByName("chest"), 1, 1);
                PacketPlayOutSpawnEntity packet2 = new PacketPlayOutSpawnEntity(entityItem, 2);
                PacketPlayOutEntityMetadata packet3 = new PacketPlayOutEntityMetadata(entityItem.getId(), entityItem.getDataWatcher(), true);
                PacketPlayOutEntityVelocity packet4 = new PacketPlayOutEntityVelocity(entityItem.getId(), 0, 0.3F, 0);
                ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet1);
                ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet2);
                ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet3);
                ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet4);
                player.playSound(player.getLocation(), Sound.CHEST_OPEN, 3, 0);

                Bukkit.getScheduler().runTaskLater(YuukosuCore.getInstance(), () -> {
                    PacketPlayOutBlockAction packet5 = new PacketPlayOutBlockAction(position, net.minecraft.server.v1_8_R3.Block.getByName("chest"), 1, 0);
                    PacketPlayOutEntityDestroy packet6 = new PacketPlayOutEntityDestroy(entityItem.getId());
                    ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet5);
                    ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet6);
                    player.playSound(player.getLocation(), Sound.CHEST_CLOSE, 3, 0);
                }, 20 * 5);
            }
        }
    }
}
