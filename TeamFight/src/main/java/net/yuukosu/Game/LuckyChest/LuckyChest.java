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

    @SuppressWarnings("deprecation")
    public LuckyChest(GameManager gameManager, Location location) {
        super(location, new BlockData(Material.CHEST, location.getBlock().getData()));
        this.gameManager = gameManager;
        this.hologram = new Hologram(location, 0.3F);
        this.hologram.addMessage("§e§lCLICK TO OPEN");
        this.hologram.addMessage("§bLucky Chest");
        this.displayItem = new EntityItem(((CraftWorld) location.getWorld()).getHandle());
    }


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
        } else if (this.opened) {
            this.close();
        }
    }

    public void open(ItemStack displayItem) {
        if (this.spawned && !this.opened) {
            this.opened = true;

            Location loc = this.getLocation();
            loc.getWorld().playSound(loc, Sound.CHEST_OPEN, 3, 2);

            BlockPosition position = new BlockPosition(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
            PacketPlayOutBlockAction packet = new PacketPlayOutBlockAction(position, Block.getByName("chest"), 1, 1);

            this.gameManager.getPlayers().forEach(gamePlayer -> gamePlayer.getCorePlayer().sendPackets(packet));
            this.spawnDisplayItem(displayItem, new Location(loc.getWorld(), loc.getX() + 0.5D, loc.getY() + 1.0D, loc.getZ() + 0.5D));
            this.hologram.destroyAll();
        }
    }

    public void close() {
        if (this.spawned && this.opened) {
            this.opened = false;

            Location loc = this.getLocation();
            loc.getWorld().playSound(loc, Sound.CHEST_CLOSE, 3, 0);

            BlockPosition position = new BlockPosition(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
            PacketPlayOutBlockAction packet = new PacketPlayOutBlockAction(position, Block.getByName("chest"), 1, 0);

            this.gameManager.getPlayers().forEach(gamePlayer -> gamePlayer.getCorePlayer().sendPackets(packet));
            this.destroyDisplayItem();
            this.hologram.spawnAll();
        }
    }

    public void destroy() {
        this.spawned = false;
        this.opened = false;
        this.breakBlock(false);
        this.destroyDisplayItem();
        this.hologram.destroyAll();
    }

    private void spawnDisplayItem(ItemStack itemStack, Location location) {
        this.displayItem.setItemStack(CraftItemStack.asNMSCopy(itemStack));
        this.displayItem.setPosition(location.getX(), location.getY(), location.getZ());
        this.displayItem.setCustomName(itemStack.getItemMeta().hasDisplayName() ? itemStack.getItemMeta().getDisplayName() : null);
        this.displayItem.setCustomNameVisible(true);

        PacketPlayOutSpawnEntity packet1 = new PacketPlayOutSpawnEntity(this.displayItem, 2);
        PacketPlayOutEntityMetadata packet2 = new PacketPlayOutEntityMetadata(this.displayItem.getId(), this.displayItem.getDataWatcher(), true);
        PacketPlayOutEntityVelocity packet3 = new PacketPlayOutEntityVelocity(this.displayItem.getId(), 0, 0, 0);

        this.gameManager.getPlayers().forEach(gamePlayer -> gamePlayer.getCorePlayer().sendPackets(packet1, packet2, packet3));
    }

    private void destroyDisplayItem() {
        this.gameManager.getPlayers().forEach(gamePlayer -> gamePlayer.getCorePlayer().sendPackets(new PacketPlayOutEntityDestroy(this.displayItem.getId())));
    }

    @Override
    public void onClick(PlayerInteractEvent e) {
        Player player = e.getPlayer();
        this.open(new ItemCreator(Material.GOLDEN_APPLE).setDisplayName("§6ゴールデンアップル").create());
        Bukkit.getScheduler().runTaskLater(TeamFight.getInstance(), this::destroy, 20L * 5L);
        player.sendMessage("§aラッキーチェストを開けた！");
        player.playSound(player.getLocation(), Sound.ORB_PICKUP, 3, 1.0F);
        Bukkit.getScheduler().runTaskLater(TeamFight.getInstance(), () -> player.playSound(player.getLocation(), Sound.ORB_PICKUP, 3, 1.5F), 5L);
    }
}
