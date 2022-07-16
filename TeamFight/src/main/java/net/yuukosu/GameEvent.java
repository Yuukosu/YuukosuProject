package net.yuukosu;

import lombok.Getter;
import net.yuukosu.Game.*;
import net.yuukosu.System.CorePlayer;
import net.yuukosu.System.PlayerRank;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.*;
import org.bukkit.event.weather.WeatherChangeEvent;

import java.util.stream.Collectors;

public class GameEvent implements Listener {

    @Getter
    private final GameManager gameManager;

    public GameEvent(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();

        if (YuukosuCore.getCoreManager().contains(player)) {
            CorePlayer corePlayer = YuukosuCore.getCoreManager().getCorePlayer(player);

            if (this.gameManager.getArenaManager().complete()) {
                this.gameManager.joinPlayer(corePlayer);
            }

            return;
        }

        player.kickPlayer("§cあなたはゲームに参加する資格がありません。");
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerQuit(PlayerQuitEvent e) {
        Player player = e.getPlayer();

        if (YuukosuCore.getCoreManager().contains(player)) {
            GamePlayer gamePlayer = this.gameManager.getGamePlayer(player);

            if (gamePlayer != null) {
                this.gameManager.quitPlayer(gamePlayer);
            }
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent e) {
        Player player = e.getPlayer();
        GamePlayer gamePlayer = this.getGameManager().getGamePlayer(player);

        if (this.gameManager.getGamePhase() == GamePhase.STARTED) {
            if (this.gameManager.isJoinedTeam(gamePlayer)) {
                if (gamePlayer.getCorePlayer().getPlayerRank().hasPriority(PlayerRank.ADMIN) && gamePlayer.getPlayer().getGameMode() == GameMode.CREATIVE) {
                    return;
                }

                this.gameManager.getTeams().values().stream().filter(gameTeam -> this.gameManager.getTeam(gamePlayer) != this.gameManager.getTeam(gameTeam)).collect(Collectors.toList()).forEach(gameTeam -> {
                    if (this.gameManager.getArenaManager().getTeamArenaData(this.gameManager.getTeam(gameTeam)).getCenter().distance(gamePlayer.getPlayer().getLocation()) <= 5.0D) {
                        this.gameManager.deathPlayer(gamePlayer, gamePlayer.getLastDamager());
                        player.sendMessage("§c敵チームのスポーン地点に近づいたため、死亡しました。");
                        player.getWorld().playSound(player.getLocation(), Sound.ZOMBIE_METAL, 5F, 2F);
                    }
                });
            }
        }
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent e) {
        if (e.getEntity() instanceof Player) {
            Player player = (Player) e.getEntity();
            GamePlayer gamePlayer = this.gameManager.getGamePlayer(player);

            if (this.gameManager.getGamePhase() == GamePhase.STARTED) {
                if (this.gameManager.isJoinedTeam(gamePlayer)) {
                    if (e.getCause() == EntityDamageEvent.DamageCause.VOID) {
                        e.setCancelled(true);
                        gamePlayer.setLastDamageCause(EntityDamageEvent.DamageCause.VOID);
                        this.gameManager.deathPlayer(gamePlayer, gamePlayer.getLastDamager());
                        return;
                    }

                    if (!gamePlayer.isNoDamage() && !gamePlayer.isInvisibleTick()) {
                        if ((player.getHealth() - e.getFinalDamage()) <= 0.0D) {
                            e.setCancelled(true);
                            gamePlayer.setLastDamageCause(e.getCause());
                            Bukkit.getScheduler().runTaskLater(YuukosuCore.getInstance(), () -> this.gameManager.deathPlayer(gamePlayer, gamePlayer.getLastDamager()), 3L);
                        }

                        if (e.getCause() == EntityDamageEvent.DamageCause.SUFFOCATION) {
                            gamePlayer.setLastDamageCause(EntityDamageEvent.DamageCause.SUFFOCATION);
                            gamePlayer.startNoDamageTick(3);
                            return;
                        }

                        gamePlayer.startNoDamageTick(10);
                        return;
                    }
                }
            }
        }

        e.setCancelled(true);
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent e) {
        Entity entity1 = e.getEntity();
        Entity entity2 = e.getDamager();

        if (entity2 instanceof Projectile) {
            entity2 = (Player) ((Projectile) e.getDamager()).getShooter();
        } else if (entity2 instanceof TNTPrimed) {
            entity2 = ((TNTPrimed) e.getDamager()).getSource();
        }

        if (entity1 instanceof Player && entity2 instanceof Player) {
            Player player1 = (Player) entity1;
            Player player2 = (Player) entity2;
            GamePlayer victim = this.gameManager.getGamePlayer(player1);
            GamePlayer attacker = this.gameManager.getGamePlayer(player2);

            if (this.gameManager.getGamePhase() == GamePhase.STARTED) {
                if ((victim != null && attacker != null) && (this.gameManager.isJoinedTeam(victim) && this.gameManager.isJoinedTeam(attacker)) && this.gameManager.getTeam(victim) != this.gameManager.getTeam(attacker)) {
                    if (!victim.equals(attacker)) {
                        victim.setLastDamager(attacker);
                        victim.setLastDamageCause(e.getCause());
                    }

                    if (attacker.isInvisibleTick()) {
                        attacker.setInvisibleTick(false);
                        attacker.getPlayer().sendMessage("§c無敵時間中に攻撃したため、無敵時間を解除されました。");
                    }

                    return;
                }
            }

            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent e) {
        Player player = e.getPlayer();
        GamePlayer gamePlayer = this.gameManager.getGamePlayer(player);

        if (gamePlayer != null) {
            if (this.gameManager.isJoinedTeam(gamePlayer)) {
                this.gameManager.teleportTeam(gamePlayer);
                return;
            }

            gamePlayer.getPlayer().teleport(this.gameManager.getArenaManager().getLobby());
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        Player player = e.getPlayer();

        if (YuukosuCore.getCoreManager().contains(player)) {
            CorePlayer corePlayer = YuukosuCore.getCoreManager().getCorePlayer(player);

            if (corePlayer.getPlayerRank().hasPriority(PlayerRank.ADMIN) && player.getGameMode() == GameMode.CREATIVE) {
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

            if (corePlayer.getPlayerRank().hasPriority(PlayerRank.ADMIN) && player.getGameMode() == GameMode.CREATIVE) {
                return;
            }
        }

        e.setCancelled(true);
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {
        Player player = e.getPlayer();
        Block block = e.getClickedBlock();

        if (block != null) {
            if (this.gameManager.getGamePhase() == GamePhase.STARTED) {
                if (block.getType() == Material.CHEST) {
                    e.setCancelled(true);
                }
            }
        }

        if (!player.isOp()) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        Player player = (Player) e.getWhoClicked();
        GamePlayer gamePlayer = this.gameManager.getGamePlayer(player);

        if (gamePlayer != null) {
            CorePlayer corePlayer = gamePlayer.getCorePlayer();

            if (corePlayer.getPlayerRank().hasPriority(PlayerRank.ADMIN) && player.getGameMode() == GameMode.CREATIVE) {
                return;
            }

            if (this.gameManager.getGamePhase() == GamePhase.STARTED) {
                if (this.gameManager.isJoinedTeam(gamePlayer)) {
                    if (e.getSlotType() != InventoryType.SlotType.ARMOR) {
                        return;
                    }
                }
            }

            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent e) {
        Player player = e.getPlayer();

        if (YuukosuCore.getCoreManager().contains(player)) {
            CorePlayer corePlayer = YuukosuCore.getCoreManager().getCorePlayer(player);

            if (corePlayer.getPlayerRank().hasPriority(PlayerRank.ADMIN) && player.getGameMode() == GameMode.CREATIVE) {
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

            if (corePlayer.getPlayerRank().hasPriority(PlayerRank.ADMIN) && player.getGameMode() == GameMode.CREATIVE) {
                return;
            }
        }

        e.setCancelled(true);
    }

    @EventHandler
    public void onAsyncPlayerChat(AsyncPlayerChatEvent e) {
        e.setCancelled(true);
        Player player = e.getPlayer();

        if (YuukosuCore.getCoreManager().contains(player)) {
            CorePlayer corePlayer = YuukosuCore.getCoreManager().getCorePlayer(player);
            GamePlayer gamePlayer = this.gameManager.getGamePlayer(player);
            String playerName = corePlayer.getRankName();
            String message = corePlayer.getPlayerRank().hasPriority(PlayerRank.VIP) ? ChatColor.translateAlternateColorCodes('&', e.getMessage()) : "";

            if (this.gameManager.getGamePhase() == GamePhase.STARTED) {
                if (gamePlayer != null && this.gameManager.isJoinedTeam(gamePlayer)) {
                    EnumTeam enumTeam = this.gameManager.getTeam(gamePlayer);
                    playerName = enumTeam.getChatColor() + "[" + enumTeam.getName() + "] " + corePlayer.getRankName();
                }
            }

            for (GamePlayer gamePlayer1 : this.gameManager.getPlayers()) {
                gamePlayer1.getPlayer().sendMessage(playerName + (corePlayer.getPlayerRank() == PlayerRank.DEFAULT ? PlayerRank.DEFAULT.getColor() : ChatColor.WHITE) + ": " + message);
            }
        }
    }

    @EventHandler
    public void onWeatherChange(WeatherChangeEvent e) {
        if (e.toWeatherState()) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onCreatureSpawn(CreatureSpawnEvent e) {
        if (e.getSpawnReason() != CreatureSpawnEvent.SpawnReason.CUSTOM) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onFoodLevelChange(FoodLevelChangeEvent e) {
        e.setCancelled(true);
    }
}
