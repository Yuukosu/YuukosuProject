package net.yuukosu;

import net.yuukosu.System.CorePlayer;
import net.yuukosu.System.CustomItem.CustomItem;
import net.yuukosu.System.CustomItem.ItemNBT;
import net.yuukosu.System.GuiCreator.YuukosuGui;
import net.yuukosu.System.PacketHandler;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

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
        ItemStack itemStack = e.getItem();

        if (itemStack != null) {
            ItemNBT itemNBT = new ItemNBT(itemStack);

            if (itemNBT.hasTag("CUSTOM_ITEM")) {
                CustomItem customItem = YuukosuCore.getCoreManager().getCustomItem(itemNBT.getString("CUSTOM_ITEM"));

                if (customItem != null) {
                    customItem.getItemClickActions().forEach(itemClickAction -> {
                        if (itemClickAction.isDelaying(player)) {
                            return;
                        }

                        if (itemClickAction.getDelay() > 0) {
                            itemClickAction.startDelay(player);
                        }

                        itemClickAction.click(e);
                    });
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
}
