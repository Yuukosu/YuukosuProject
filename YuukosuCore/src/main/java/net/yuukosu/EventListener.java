package net.yuukosu;

import net.yuukosu.System.BlockControl.BlockDataEx;
import net.yuukosu.System.BlockControl.ClickBlocks.ChestExample;
import net.yuukosu.System.BlockControl.ClickableBlock;
import net.yuukosu.System.BlockControl.ClickBlocks.ExampleClickableBlock;
import net.yuukosu.System.CorePlayer;
import net.yuukosu.System.CustomItem.*;
import net.yuukosu.System.GuiCreator.YuukosuGui;
import net.yuukosu.System.PacketHandler;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.MetadataValue;

import java.util.List;

public class EventListener implements Listener {

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerJoin(PlayerJoinEvent e) {
        e.setJoinMessage(null);

        Player player = e.getPlayer();
        YuukosuCore.getCoreManager().registerPlayer(player);
        CorePlayer corePlayer = YuukosuCore.getCoreManager().getCorePlayer(player);
        corePlayer.cleanPlayerStatus(GameMode.ADVENTURE);

        corePlayer.getPlayer().sendMessage("§7認証中...");
        if (corePlayer.hasData()) {
            corePlayer.load();
        } else {
            Bukkit.getLogger().info(player.getName() + " is first joined!");
            corePlayer.save();
        }

        corePlayer.registerPacketHandler(new PacketHandler(corePlayer));
        if (!corePlayer.hasActiveInviteCode()) {
            Bukkit.getScheduler().runTaskLater(YuukosuCore.getInstance(), corePlayer::requestInviteCode, 10L);

            corePlayer.setCanMove(false);
            corePlayer.getPlayer().sendMessage("§c招待コードを入力してください。");
            corePlayer.getPlayer().playSound(corePlayer.getPlayer().getLocation(), "mob.villager.no", 3F, 0F);
            return;
        }

        corePlayer.setCanMove(true);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerQuit(PlayerQuitEvent e) {
        e.setQuitMessage(null);

        Player player = e.getPlayer();

        if (YuukosuCore.getCoreManager().contains(player)) {
            CorePlayer corePlayer = YuukosuCore.getCoreManager().getCorePlayer(player);
            corePlayer.unregisterPacketHandler();

            YuukosuCore.getCoreManager().unregisterPlayer(player.getUniqueId());
        }
    }

    @EventHandler
    public void onInventoryOpen(InventoryOpenEvent e) {
        Player player = (Player) e.getPlayer();

        if (YuukosuCore.getCoreManager().contains(player)) {
            CorePlayer corePlayer = YuukosuCore.getCoreManager().getCorePlayer(player);
            YuukosuGui gui = corePlayer.getCurrentGui();

            if (gui != null) {
                if (gui.isAutoClear()) {
                    gui.clearInventory();
                    gui.clearButtons();
                }

                if (gui.isAutoUpdate()) {
                    gui.update();
                }

                gui.open(e);
            }
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        Player player = (Player) e.getWhoClicked();

        if (YuukosuCore.getCoreManager().contains(player)) {
            CorePlayer corePlayer = YuukosuCore.getCoreManager().getCorePlayer(player);
            YuukosuGui gui = corePlayer.getCurrentGui();

            if (gui != null) {
                e.setCancelled(true);

                if (gui.isDelaying(player)) {
                    return;
                }

                gui.startDelay(player);

                if (gui.getButtons().containsKey(e.getSlot())) {
                    gui.getButtons().get(e.getSlot()).click(e);

                    if (gui.isAutoClear()) {
                        gui.clearInventory();
                        gui.clearButtons();
                    }

                    if (gui.isAutoUpdate()) {
                        gui.update();
                    }
                }
            }
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {
        Player player = e.getPlayer();
        Block block = e.getClickedBlock();
        ItemStack itemStack = e.getItem();

        if (block != null) {
            if (block.hasMetadata("CLICKABLE_BLOCK")) {
                List<MetadataValue> valueList = block.getMetadata("CLICKABLE_BLOCK");

                for (MetadataValue value : valueList) {
                    if (value.value() instanceof ClickableBlock) {
                        ((ClickableBlock) value.value()).onClick(e);
                    }
                }
            }
        }

        if (itemStack != null) {
            ItemNBT itemNBT = new ItemNBT(itemStack);

            if (itemNBT.hasTag("CUSTOM_ITEM")) {
                CustomItem customItem = YuukosuCore.getCoreManager().getCustomItem(itemNBT.getString("CUSTOM_ITEM"));

                if (customItem != null) {
                    ItemAction itemAction = customItem.getItemAction();

                    if (itemAction instanceof ItemClickAction) {
                        ItemClickAction itemClickAction = (ItemClickAction) itemAction;

                        if (itemClickAction.isDelaying(player)) {
                            return;
                        }

                        itemClickAction.startDelay(player);
                        itemClickAction.onClick(e);
                    }
                }
            }

            if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
                if (itemStack.getType() == Material.BLAZE_ROD) {
                    if (block != null) {
                        e.setCancelled(true);

                        BlockDataEx b = new ExampleClickableBlock(block.getLocation());
                        b.placeBlock();
                    }
                }

                if (itemStack.getType() == Material.BONE) {
                    if (block != null) {
                        e.setCancelled(true);

                        BlockDataEx b = new ChestExample(block.getLocation());
                        b.placeBlock();
                        player.sendMessage("§aCreated!");
                        player.playSound(player.getLocation(), Sound.ZOMBIE_METAL, 3F, 0F);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent e) {
        Player player = (Player) e.getPlayer();

        if (YuukosuCore.getCoreManager().contains(player)) {
            CorePlayer corePlayer = YuukosuCore.getCoreManager().getCorePlayer(player);

            if (corePlayer.getCurrentGui() != null) {
                corePlayer.getCurrentGui().close(e);
            }

            corePlayer.setCurrentGui(null);
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        Block block = e.getBlock();

        if (block.hasMetadata("CLICKABLE_BLOCK")) {
            block.removeMetadata("CLICKABLE_BLOCK", YuukosuCore.getInstance());
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent e) {
        ItemStack itemStack = e.getItemInHand();

        if (itemStack != null) {
            ItemNBT itemNBT = new ItemNBT(itemStack);

            if (itemNBT.hasTag("CUSTOM_ITEM")) {
                CustomItem customItem = YuukosuCore.getCoreManager().getCustomItem(itemNBT.getString("CUSTOM_ITEM"));

                if (customItem != null) {
                    ItemAction itemAction = customItem.getItemAction();

                    if (itemAction instanceof ItemPlaceAction) {
                        ((ItemPlaceAction) itemAction).onPlace(e);
                    }
                }
            }
        }
    }
}
