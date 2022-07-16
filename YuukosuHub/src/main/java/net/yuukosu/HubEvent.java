package net.yuukosu;

import net.yuukosu.System.CorePlayer;
import net.yuukosu.System.HubPlayer;
import net.yuukosu.System.PlayerRank;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.weather.WeatherChangeEvent;

public class HubEvent implements Listener {

    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();

        if (YuukosuCore.getCoreManager().contains(player)) {
            CorePlayer corePlayer = YuukosuCore.getCoreManager().getCorePlayer(player);
            YuukosuHub.getHubManager().registerPlayer(corePlayer);

            HubPlayer hubPlayer = YuukosuHub.getHubManager().getHubPlayer(player);
            hubPlayer.init();
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerQuit(PlayerQuitEvent e) {
        Player player = e.getPlayer();
        YuukosuHub.getHubManager().unregisterPlayer(player.getUniqueId());
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent e) {
        Player player = e.getPlayer();

        if (player.getLocation().getY() < -50) {
            if (YuukosuHub.getHubManager().getHubData().getSpawn() != null) {
                player.teleport(YuukosuHub.getHubManager().getHubData().getSpawn());
            }
        }
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent e) {
        if (e.getEntity() instanceof Player) {
            if (e.getCause() == EntityDamageEvent.DamageCause.CUSTOM) {
                return;
            }

            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent e) {
        e.setDeathMessage(null);
        e.setKeepInventory(true);
        e.setKeepLevel(true);
        e.setDroppedExp(0);
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent e) {
        if (YuukosuHub.getHubManager().getHubData().getSpawn() != null) {
            e.setRespawnLocation(YuukosuHub.getHubManager().getHubData().getSpawn());
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        Player player = e.getPlayer();

        if (YuukosuCore.getCoreManager().contains(player)) {
            CorePlayer corePlayer = YuukosuCore.getCoreManager().getCorePlayer(player);

            if (corePlayer.getPlayerRank().isStaff() && player.getGameMode() == GameMode.CREATIVE) {
                return;
            }
        }

        e.setCancelled(true);
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent e) {
        Player player = e.getPlayer();

        if (YuukosuCore.getCoreManager().contains(player)) {
            CorePlayer corePlayer = YuukosuCore.getCoreManager().getCorePlayer(player);

            if (corePlayer.getPlayerRank().isStaff() && player.getGameMode() == GameMode.CREATIVE) {
                return;
            }
        }

        e.setCancelled(true);
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent e) {
        Player player = e.getPlayer();

        if (YuukosuCore.getCoreManager().contains(player)) {
            CorePlayer corePlayer = YuukosuCore.getCoreManager().getCorePlayer(player);

            if (corePlayer.getPlayerRank().isStaff()) {
                return;
            }
        }

        e.setCancelled(true);
    }

    @EventHandler
    public void onPlayerPickupItem(PlayerPickupItemEvent e) {
        Player player = e.getPlayer();

        if (YuukosuCore.getCoreManager().contains(player)) {
            CorePlayer corePlayer = YuukosuCore.getCoreManager().getCorePlayer(player);

            if (corePlayer.getPlayerRank().isStaff()) {
                return;
            }
        }

        e.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onAsyncPlayerChat(AsyncPlayerChatEvent e) {
        e.setCancelled(true);

        Player player = e.getPlayer();

        if (YuukosuCore.getCoreManager().contains(player)) {
            CorePlayer corePlayer = YuukosuCore.getCoreManager().getCorePlayer(player);
            String message = (corePlayer.getPlayerRank().hasPriority(PlayerRank.VIP) ? ChatColor.translateAlternateColorCodes('&', e.getMessage()) : e.getMessage());
            Bukkit.getOnlinePlayers().forEach(player1 -> player1.sendMessage(corePlayer.getRankName() + (corePlayer.getPlayerRank() == PlayerRank.DEFAULT ? PlayerRank.DEFAULT.getColor() : ChatColor.WHITE) + ": " + message));
        }
    }

    @EventHandler
    public void onFoodLevelChange(FoodLevelChangeEvent e) {
        e.setCancelled(true);
    }

    @EventHandler
    public void onWeatherChange(WeatherChangeEvent e) {
        if (e.toWeatherState()) {
            e.setCancelled(true);
        }
    }
}
