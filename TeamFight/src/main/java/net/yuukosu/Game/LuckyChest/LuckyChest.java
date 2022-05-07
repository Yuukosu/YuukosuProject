package net.yuukosu.Game.LuckyChest;

import lombok.Getter;
import net.minecraft.server.v1_8_R3.*;
import net.yuukosu.Game.GameManager;
import net.yuukosu.System.BlockControl.BlockData;
import net.yuukosu.System.BlockControl.BlockDataEx;
import net.yuukosu.System.BlockControl.ClickableBlock;
import net.yuukosu.System.Hologram;
import net.yuukosu.System.ItemCreator;
import net.yuukosu.TeamFight;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class LuckyChest extends BlockDataEx implements ClickableBlock {

    @Getter
    private final GameManager gameManager;
    @Getter
    private final Hologram hologram;
    @Getter
    private final EntityItem displayItem;
    @Getter
    private boolean spawned;
    @Getter
    private boolean opened;

    public LuckyChest(GameManager gameManager, Location location, byte data) {
        super(location, new BlockData(Material.CHEST, data));
        this.gameManager = gameManager;
        this.hologram = new Hologram(location, 0.3F);
        this.hologram.addMessage("§e§lCLICK TO OPEN");
        this.hologram.addMessage("§bLucky Chest");
        this.displayItem = new EntityItem(((CraftWorld) location.getWorld()).getHandle());
    }

    public void spawn() {
        if (!this.spawned) {
            this.spawned = true;
            this.opened = false;
            this.placeBlock();
            this.hologram.spawnAll();
        }
    }

    public void open(ItemStack displayItem) {
        if (!this.spawned && !this.opened) {
            this.opened = true;
            this.hologram.destroyAll();
            this.displayItem.setItemStack(CraftItemStack.asNMSCopy(displayItem));
            this.displayItem.setCustomName(displayItem.getItemMeta().hasDisplayName() ? displayItem.getItemMeta().getDisplayName() : "");
            this.displayItem.setCustomNameVisible(true);

            Location loc = this.getLocation();
            BlockPosition position = new BlockPosition(loc.getBlockX() + 0.5D, loc.getBlockY() + 1.0D, loc.getBlockZ() + 0.5D);
            PacketPlayOutBlockAction packet1 = new PacketPlayOutBlockAction(position, Block.getByName("chest"), 1, 1);
            PacketPlayOutSpawnEntity packet2 = new PacketPlayOutSpawnEntity(this.displayItem, 2);
            PacketPlayOutEntityMetadata packet3 = new PacketPlayOutEntityMetadata(this.displayItem.getId(), this.displayItem.getDataWatcher(), true);
            PacketPlayOutEntityVelocity packet4 = new PacketPlayOutEntityVelocity(this.displayItem.getId(), 0.0D, 0.3D, 0.0D);
            this.gameManager.getPlayers().forEach(gamePlayer -> gamePlayer.getCorePlayer().sendPackets(packet1, packet2, packet3, packet4));
            loc.getWorld().playSound(loc, Sound.CHEST_OPEN, 3, 2);
        }
    }

    public void destroy() {
        this.spawned = false;
        this.opened = false;
        this.breakBlock(false);
        this.hologram.destroyAll();
    }

    @Override
    public void click(PlayerInteractEvent e) {
        Player player = e.getPlayer();
        this.open(new ItemCreator(Material.GOLDEN_APPLE).setDisplayName("§6ゴールデンアップル").create());
        player.sendMessage("§aOpened LuckyChest!");
        player.playSound(player.getLocation(), Sound.ORB_PICKUP, 3, 1.0F);
        Bukkit.getScheduler().runTaskLater(TeamFight.getInstance(), () -> player.playSound(player.getLocation(), Sound.ORB_PICKUP, 3, 1.5F), 5L);
    }
}
