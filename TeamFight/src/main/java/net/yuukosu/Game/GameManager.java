package net.yuukosu.Game;

import lombok.Getter;
import lombok.NonNull;
import net.yuukosu.Arena.ArenaManager;
import net.yuukosu.Game.CustomItem.ClassSelectorItem;
import net.yuukosu.System.BlockControl.MultiBlock;
import net.yuukosu.System.CorePlayer;
import net.yuukosu.System.ItemCreator;
import net.yuukosu.TeamFight;
import net.yuukosu.YuukosuCore;
import org.bukkit.*;
import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.stream.Collectors;

public class GameManager {

    @Getter
    private final ArenaManager arenaManager;
    @Getter
    private final Set<GamePlayer> players = new HashSet<>();
    @Getter
    private final Set<GamePlayer> spectators = new HashSet<>();
    @Getter
    private final Map<EnumTeam, GameTeam> teams = new HashMap<>();
    @Getter
    private GamePhase gamePhase;
    @Getter
    private int countTime;
    @Getter
    private int gameTime;
    @Getter
    private boolean countDown;
    @Getter
    private final int minPlayers;
    @Getter
    private final int gameTime2;
    @Getter
    private final MultiBlock lobbyBlocks;

    public GameManager(ArenaManager arenaManager) {
        this.arenaManager = arenaManager;
        this.gamePhase = GamePhase.WAITING;
        this.countTime = -1;
        this.countDown = false;
        this.minPlayers = 2;
        this.gameTime2 = 60 * 8;
        this.lobbyBlocks = new MultiBlock();

        Bukkit.getScheduler().runTaskTimer(YuukosuCore.getInstance(), () -> {
            this.players.forEach(gamePlayer -> {
                if (this.gamePhase == GamePhase.WAITING) {
                    gamePlayer.getPlayerFrontend().updateActionbar();
                }
            });

            if (GameManager.this.gamePhase == GamePhase.STARTED) {
                Bukkit.getWorlds().forEach(world -> world.getEntities().forEach(entity -> {
                    if (entity instanceof Arrow) {
                        if (entity.isOnGround()) {
                            entity.remove();
                        }
                    }
                }));
            }
        }, 0L, 10L);
    }

    public void joinPlayer(CorePlayer corePlayer) {
        GamePlayer gamePlayer = new GamePlayer(corePlayer);
        gamePlayer.resetHealth();
        gamePlayer.clearInventory();
        gamePlayer.removePotionEffects();
        gamePlayer.getPlayer().setGameMode(GameMode.ADVENTURE);
        gamePlayer.getPlayer().teleport(this.arenaManager.getLobby());
        gamePlayer.getPlayer().getInventory().setItem(0, new ClassSelectorItem(this).create());
        this.players.add(gamePlayer);
        this.updateFrontend(true, true);

        if (this.gamePhase == GamePhase.WAITING) {
            this.sendMessage(corePlayer.getColoredName() + " §eがゲームに参加しました！");

            if (this.players.size() == this.minPlayers && this.countTime == -1) {
                this.startCount(15, false);
            }
        } else {
            this.spectators.add(gamePlayer);
            gamePlayer.getPlayer().setGameMode(GameMode.SPECTATOR);
            gamePlayer.getCorePlayer().sendTitle("§eYOU ARE NOW SPECTATING!", "§7すでにゲームは始まっているため、スペクテイターモードで参加しました。", 0, 20 * 3, 20);
            gamePlayer.getPlayer().playSound(gamePlayer.getPlayer().getLocation(), "random.click", 3F, 2F);
        }
    }

    public void quitPlayer(GamePlayer gamePlayer) {
        this.teams.values().forEach(gameTeam -> gameTeam.removeTeam(gamePlayer));
        this.spectators.remove(gamePlayer);
        this.players.remove(gamePlayer);
        this.updateFrontend(false, true);

        if (this.gamePhase == GamePhase.WAITING) {
            this.sendMessage(gamePlayer.getCorePlayer().getColoredName() + " §eが退出しました。");
        } else if (this.gamePhase == GamePhase.STARTED) {
            this.sendMessage(gamePlayer.getCorePlayer().getColoredName() + " §7が退出しました。");

            if (this.players.isEmpty()) {
                Bukkit.shutdown();
                return;
            }

            if (this.teams.values().stream().filter(gameTeam1 -> !gameTeam1.getPlayers().isEmpty()).count() <= 1) {
                this.judgeWinner();
            }
        } else {
            if (Bukkit.getOnlinePlayers().isEmpty()) {
                Bukkit.shutdown();
            }
        }
    }

    public void startCount(int count, boolean force) {
        if (!this.countDown) {
            this.countDown = true;
            this.countTime = count;

            new BukkitRunnable() {
                int countDown = count;

                @Override
                public void run() {
                    if (this.countDown <= 0) {
                        this.cancel();
                        GameManager.this.countTime = -1;
                        GameManager.this.countDown = false;
                        GameManager.this.startGame();
                        return;
                    }

                    if (GameManager.this.players.size() < GameManager.this.minPlayers && !force) {
                        this.cancel();
                        GameManager.this.countTime = -1;
                        GameManager.this.countDown = false;
                        GameManager.this.updateFrontend(false, true);
                        GameManager.this.sendTitle("§cプレイヤーを待っています...", "", 0, 20 * 3, 10);
                        GameManager.this.sendMessage("§cプレイヤー人数が足りなくなったため、カウントダウンがキャンセルされました。");
                        GameManager.this.playSound("random.click", 3, 1);
                        return;
                    }

                    switch (this.countDown) {
                        case 15:
                            GameManager.this.sendMessage("§eあと §a" + this.countDown + " §e秒でゲームがスタートします！");
                            GameManager.this.playSound("note.hat", 3, 1);
                            break;
                        case 10:
                            GameManager.this.sendMessage("§eあと §6" + this.countDown + " §e秒でゲームがスタートします！");
                            GameManager.this.playSound("note.hat", 3, 1);
                            break;
                        case 5:
                        case 4:
                            GameManager.this.sendMessage("§eあと §c" + this.countDown + " §e秒でゲームがスタートします！");
                            GameManager.this.sendTitle("§e" + this.countDown, "", 0, 20 * 2, 0);
                            GameManager.this.playSound("note.hat", 3, 1);
                            break;
                        case 3:
                        case 2:
                        case 1:
                            GameManager.this.sendMessage("§eあと §c" + this.countDown + " §e秒でゲームがスタートします！");
                            GameManager.this.sendTitle("§c" + this.countDown, "", 0, 20 * 2, 0);
                            GameManager.this.playSound("note.hat", 3, 1);
                            break;
                    }

                    GameManager.this.countTime = this.countDown--;
                    GameManager.this.updateFrontend(false, true);
                }
            }.runTaskTimer(TeamFight.getInstance(), 0L, 20L);
        }
    }

    public void destroyLobby(int radius) {
        Location lobby = this.arenaManager.getLobby();
        this.lobbyBlocks.insertAroundBlock(lobby, radius, radius, radius);
        this.lobbyBlocks.breakInsertedBlocks(false);
    }

    public void repairLobby() {
        this.lobbyBlocks.place(false);
    }

    public void shakeTeam() {
        this.arenaManager.getArenaTeamData().forEach((key, value) -> this.teams.put(key, new GameTeam(this)));

        for (GamePlayer gamePlayer : this.players) {
            GameTeam team = null;

            judgeTeam:
            for (int i = 0; i < this.teams.size(); i++) {
                team = this.teams.get(EnumTeam.getSortedTeam(i));

                final GameTeam finalTeam = team;
                if (this.teams.values().stream().allMatch(gameTeam -> gameTeam.getSize() == finalTeam.getSize())) {
                    break;
                }

                for (int i1 = 0; i1 < this.teams.size(); i1++) {
                    GameTeam team1 = this.teams.get(EnumTeam.getSortedTeam(i1));

                    if (team.getSize() > team1.getSize()) {
                        team = team1;
                        break judgeTeam;
                    }
                }
            }

            if (team != null) {
                team.joinTeam(gamePlayer);
            }
        }
    }

    public void teleportTeam() {
        this.players.forEach(this::teleportTeam);
    }

    public void teleportTeam(GamePlayer gamePlayer) {
        Location location = this.arenaManager.getLobby();

        if (this.isJoinedTeam(gamePlayer)) {
            int index = new Random().nextInt(this.arenaManager.getTeamArenaData(this.getTeam(gamePlayer)).getSpawns().size());
            location = this.arenaManager.getTeamArenaData(this.getTeam(gamePlayer)).getSpawns().get(index);
        }

        gamePlayer.getPlayer().teleport(location);
    }

    public void giveItem() {
        this.players.stream().filter(this::isJoinedTeam).collect(Collectors.toList()).forEach(this::giveItem);
    }

    public void giveItem(GamePlayer gamePlayer) {
        PlayerInventory inventory = gamePlayer.getPlayer().getInventory();
        inventory.setHelmet(new ItemCreator(Material.LEATHER_HELMET).setColor(this.getTeam(gamePlayer).getColor()).setEquipmentMeta().create());
        inventory.setChestplate(new ItemCreator(Material.LEATHER_CHESTPLATE).setColor(this.getTeam(gamePlayer).getColor()).setEquipmentMeta().create());
        inventory.setLeggings(new ItemCreator(Material.LEATHER_LEGGINGS).setColor(this.getTeam(gamePlayer).getColor()).setEquipmentMeta().create());
        inventory.setBoots(new ItemCreator(Material.LEATHER_BOOTS).setColor(this.getTeam(gamePlayer).getColor()).setEquipmentMeta().create());
        gamePlayer.getEnumClass().getItemClass().give(gamePlayer.getPlayer());
        gamePlayer.getPlayer().getInventory().setItem(8, new ClassSelectorItem(this).create());
    }


    public void startGame() {
        this.gamePhase = GamePhase.STARTED;
        this.startGameTimer(this.gameTime2);
        this.destroyLobby(10);
        this.shakeTeam();
        this.giveItem();
        this.teleportTeam();
        this.players.stream().filter(this::isJoinedTeam).collect(Collectors.toList()).forEach(gamePlayer -> {
            gamePlayer.getPlayer().setGameMode(GameMode.SURVIVAL);
            gamePlayer.getEnumClass().getItemClass().startBuff(gamePlayer);
            gamePlayer.getPlayerFrontend().createHealthBar();
        });
        this.updateFrontend(true, true);
        this.sendTitle("§c§lFIGHT!", "§eゲームがスタートしました！", 0, 20 * 3, 10);
        this.playSound("note.pling", 3, 2);
        this.playSound("mob.enderdragon.growl", 3, 1);
    }

    public void startGameTimer(int time) {
        new BukkitRunnable() {
            int count = time;

            @Override
            public void run() {
                if (this.count <= 0) {
                    this.cancel();
                    GameManager.this.judgeWinner();
                    return;
                }

                switch (this.count) {
                    case 60:
                    case 30:
                    case 15:
                        GameManager.this.sendMessage("§c§l残り §e§l" + this.count + " §c§l秒！");
                        GameManager.this.playSound("note.bass", 3, 1);
                        break;
                }

                GameManager.this.gameTime = this.count--;
                GameManager.this.updateFrontend(false, true);
            }
        }.runTaskTimer(TeamFight.getInstance(), 0L, 20L);
    }

    public void judgeWinner() {
        if (this.teams.values().stream().anyMatch(gameTeam -> !gameTeam.getPlayers().isEmpty()) && !this.teams.values().stream().allMatch(gameTeam -> gameTeam.getKills() == 0)) {
            List<GameTeam> winners = new ArrayList<GameTeam>() {{
                GameTeam winner = GameManager.this.getTopKillerTeams().get(0);
                this.add(winner);

                this.addAll(GameManager.this.teams.values().stream().filter(gameTeam -> !gameTeam.getPlayers().isEmpty() && winner.getKills() == gameTeam.getKills()).collect(Collectors.toList()));
            }};
            this.victory(winners.toArray(new GameTeam[0]));
            return;
        }

        this.draw();
    }

    public void victory(GameTeam... gameTeams) {
        if (gameTeams.length > 0) {
            this.gamePhase = GamePhase.ENDED;
            this.updateFrontend(false, true);

            List<GameTeam> winner = Arrays.asList(gameTeams);
            List<GameTeam> loser = this.teams.values().stream().filter(gameTeam -> !winner.contains(gameTeam)).collect(Collectors.toList());
            String winners = (winner.stream().anyMatch(gameTeam -> this.getTeam(gameTeam) == EnumTeam.RED) ? EnumTeam.RED.getColoredName() : "") +
                    (winner.stream().anyMatch(gameTeam -> this.getTeam(gameTeam) == EnumTeam.BLUE) ? EnumTeam.BLUE.getColoredName() : "") +
                    (winner.stream().anyMatch(gameTeam -> this.getTeam(gameTeam) == EnumTeam.GREEN) ? EnumTeam.GREEN.getColoredName() : "") +
                    (winner.stream().anyMatch(gameTeam -> this.getTeam(gameTeam) == EnumTeam.YELLOW) ? EnumTeam.YELLOW.getColoredName() : "");
            String subtitle = winners + " §7チームが勝利しました！";

            winner.forEach(gameTeam -> {
                gameTeam.sendTitle("§6§lVICTORY!", subtitle, 0, 20 * 5, 0);
                gameTeam.getPlayers().forEach(gamePlayer -> gamePlayer.getCorePlayer().sendActionbar("§aYOU WIN!"));
            });
            loser.forEach(gameTeam -> {
                gameTeam.sendTitle("§c§lDEFEAT!", subtitle, 0, 20 * 5, 0);
                gameTeam.getPlayers().forEach(gamePlayer -> gamePlayer.getCorePlayer().sendActionbar("§cあなたは負け犬です。"));
            });

            this.players.forEach(gamePlayer -> {
                gamePlayer.clearInventory();
                gamePlayer.removePotionEffects();

                GamePlayer top1 = this.getTopKillers().size() < 1 ? null : this.getTopKillers().get(0);
                GamePlayer top2 = this.getTopKillers().size() < 2 ? null : this.getTopKillers().get(1);
                GamePlayer top3 = this.getTopKillers().size() < 3 ? null : this.getTopKillers().get(2);

                gamePlayer.getPlayer().sendMessage("§a§l▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
                gamePlayer.getPlayer().sendMessage("§l                  Top Killer              ");
                gamePlayer.getPlayer().sendMessage("§l                                          ");
                gamePlayer.getPlayer().sendMessage("§e§l          1位 §7- " + (top1 != null ? top1.getCorePlayer().getRankName() + " §7- " + top1.getKills() + " kills" : "§cNone"));
                gamePlayer.getPlayer().sendMessage("§6§l          2位 §7- " + (top2 != null ? top2.getCorePlayer().getRankName() + " §7- " + top2.getKills() + " kills" : "§cNone"));
                gamePlayer.getPlayer().sendMessage("§c§l          3位 §7- " + (top3 != null ? top3.getCorePlayer().getRankName() + " §7- " + top3.getKills() + " kills" : "§cNone"));
                gamePlayer.getPlayer().sendMessage("§l                                          ");
                gamePlayer.getPlayer().sendMessage("§a§l▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
                gamePlayer.getPlayer().playSound(gamePlayer.getPlayer().getLocation(), "note.pling", 3, 2);
            });
            Bukkit.getScheduler().runTaskLater(TeamFight.getInstance(), this::endGame, 20 * 10L);
        }
    }

    public void draw() {
        this.gamePhase = GamePhase.ENDED;
        this.updateFrontend(false, true);

        this.players.forEach(gamePlayer -> {
            gamePlayer.clearInventory();
            gamePlayer.removePotionEffects();
            gamePlayer.getCorePlayer().sendTitle("§7§lDRAW!", "", 0, 20 * 3, 0);
            gamePlayer.getPlayer().playSound(gamePlayer.getPlayer().getLocation(), "note.pling", 3, 2);
        });
        Bukkit.getScheduler().runTaskLater(TeamFight.getInstance(), this::endGame, 20 * 10L);
    }

    public void endGame()
    {
        this.gamePhase = GamePhase.ENDED;
        this.updateFrontend(false, true);

        this.players.forEach(gamePlayer -> {
            gamePlayer.clearInventory();
            gamePlayer.removePotionEffects();
            gamePlayer.getCorePlayer().sendTitle("§c§lGAME END!", "", 0, 20 * 3, 0);
            gamePlayer.getPlayer().playSound(gamePlayer.getPlayer().getLocation(), Sound.ENDERDRAGON_DEATH, 3, 0);
        });
        Bukkit.getScheduler().runTaskLater(TeamFight.getInstance(), () -> this.players.forEach(gamePlayer -> gamePlayer.getCorePlayer().sendServer("lobby")), 20L * 10L);
        Bukkit.getScheduler().runTaskLater(TeamFight.getInstance(), Bukkit::reload, 20L * 15L);
    }

    public void deathPlayer(@NonNull GamePlayer victim, GamePlayer killer) {
        if (!this.isJoinedTeam(victim)) {
            victim.getPlayer().teleport(this.getArenaManager().getLobby());
            return;
        }

        if (killer != null && !this.isJoinedTeam(killer)) {
            killer = null;
        }

        String victimName = this.getColoredName(victim);
        String deathMessage = victimName + " §eは死亡した。";

        if (killer != null) {
            GameTeam killerTeam = this.getGameTeam(this.getTeam(killer));
            String killerName = this.getColoredName(killer);
            killerTeam.setKills(killerTeam.getKills() + 1);
            killer.setKills(killer.getKills() + 1);
            killer.getEnumClass().getItemClass().killBuff(killer);

            killer.getPlayer().getWorld().playEffect(victim.getPlayer().getLocation(), Effect.STEP_SOUND, Material.REDSTONE_BLOCK, 50);
            killer.getPlayer().getWorld().playEffect(victim.getPlayer().getLocation().add(0, 1, 0), Effect.STEP_SOUND, Material.REDSTONE_BLOCK, 50);
            killer.getPlayer().playSound(killer.getPlayer().getLocation(), "random.orb", 3F, 1F);

            if (victim.getLastDamageCause() != null) {
                switch (victim.getLastDamageCause()) {
                    case ENTITY_ATTACK:
                        deathMessage = victimName + " §eは " + killerName + " §eに殴り倒された。";
                        break;
                    case BLOCK_EXPLOSION:
                    case ENTITY_EXPLOSION:
                        deathMessage = victimName + " §eは " + killerName + " §eに吹き飛ばされた。";
                        break;
                    case PROJECTILE:
                        deathMessage = victimName + " §eは " + killerName + " §eに射貫かれた。";
                        break;
                    case MAGIC:
                        deathMessage = victimName + " §eは " + killerName + " §eの魔法で倒された。";
                        break;
                    case FALL:
                        deathMessage = victimName + " §eは " + killerName + " §eに突き落とされて足を挫いた。";
                        break;
                    case LAVA:
                        deathMessage = victimName + " §eは " + killerName + " §eに溶岩で溶かされた。";
                        break;
                    case FIRE:
                        deathMessage = victimName + " §eは " + killerName + " §eに串焼きにされた。";
                        break;
                    case FIRE_TICK:
                        deathMessage = victimName + " §eは " + killerName + " §eから燃やされた。";
                        break;
                    case POISON:
                        deathMessage = victimName + " §eは " + killerName + " §eの毒に耐えられなかった。";
                        break;
                    case VOID:
                        deathMessage = victimName + " §eは " + killerName + " §eから奈落への旅行チケットを受け取った。";
                        break;
                    default:
                        deathMessage = victimName + " §eは " + killerName + " §eに倒された。";
                        break;
                }
            }
        } else if (victim.getLastDamageCause() != null) {
            switch (victim.getLastDamageCause()) {
                case ENTITY_ATTACK:
                    deathMessage = victimName + " §eは敵に殴り倒された。";
                    break;
                case ENTITY_EXPLOSION:
                    deathMessage = victimName + " §eは敵に吹き飛ばされた。";
                    break;
                case BLOCK_EXPLOSION:
                    deathMessage = victimName + " §eは吹き飛ばされた。";
                    break;
                case PROJECTILE:
                    deathMessage = victimName + " §eは射貫かれた。";
                    break;
                case MAGIC:
                    deathMessage = victimName + " §eは魔法で死亡した。";
                    break;
                case POISON:
                    deathMessage = victimName + " §eは毒に耐えられなかった。";
                    break;
                case FALL:
                    deathMessage = victimName + " §eは足を挫いた。";
                    break;
                case SUFFOCATION:
                    deathMessage = victimName + " §eは生き埋めにされた。";
                    break;
                case LAVA:
                    deathMessage = victimName + " §eは溶岩で溶けた。";
                    break;
                case FIRE:
                    deathMessage = victimName + " §eは串焼きにされた。";
                    break;
                case FIRE_TICK:
                    deathMessage = victimName + " §eは体についた火を消す前に自身が消えてしまった。";
                    break;
                case VOID:
                    deathMessage = victimName + " §eは奈落の旅に出かけた。";
                    break;
            }
        }

        victim.setLastDamager(null);
        victim.setLastDamageCause(null);
        victim.startInvisibleTick(20 * 5);
        victim.resetHealth();
        victim.clearInventory();
        victim.removePotionEffects();
        victim.getEnumClass().getItemClass().respawnBuff(victim);
        this.teleportTeam(victim);
        this.giveItem(victim);
        this.sendMessage(deathMessage);
        this.updateFrontend(false, true);
    }

    public void updateFrontend(boolean b, boolean b1) {
        this.players.forEach(gamePlayer -> gamePlayer.getPlayerFrontend().update(b, b1));
    }

    public void sendMessage(String message) {
        this.getPlayers().forEach(gamePlayer -> gamePlayer.getPlayer().sendMessage(message));
    }

    public void sendTitle(String title, String subtitle, int fade, int stay, int out) {
        this.getPlayers().forEach(gamePlayer -> gamePlayer.getCorePlayer().sendTitle(title, subtitle, fade, stay, out));
    }

    public void playSound(String sound, float volume, int pitch) {
        this.getPlayers().forEach(gamePlayer -> gamePlayer.getPlayer().playSound(gamePlayer.getPlayer().getLocation(), sound, volume, pitch));
    }

    public GamePlayer getGamePlayer(Player player) {
        return this.players.stream().filter(gamePlayer -> gamePlayer.getPlayer().equals(player)).findFirst().orElse(null);
    }

    public GameTeam getGameTeam(EnumTeam enumTeam) {
        return this.teams.get(enumTeam);
    }

    public EnumTeam getTeam(GamePlayer gamePlayer) {
        return this.teams.keySet().stream().filter(gameTeam -> this.teams.get(gameTeam).getPlayers().contains(gamePlayer)).findFirst().orElse(null);
    }

    public EnumTeam getTeam(GameTeam gameTeam) {
        return this.teams.keySet().stream().filter(enumTeam -> this.teams.get(enumTeam).equals(gameTeam)).findFirst().orElse(null);
    }

    public List<GameTeam> getTopKillerTeams() {
        List<GameTeam> gameTeams = this.teams.values().stream().sorted(Comparator.comparingInt(GameTeam::getKills)).collect(Collectors.toList());
        Collections.reverse(gameTeams);

        return gameTeams;
    }

    public List<GamePlayer> getTopKillers() {
        List<GamePlayer> gamePlayers = this.players.stream().sorted(Comparator.comparingInt(GamePlayer::getKills)).collect(Collectors.toList());
        Collections.reverse(gamePlayers);

        return gamePlayers;
    }

    public String getColoredName(GamePlayer gamePlayer) {
        return this.getTeam(gamePlayer).getChatColor() + gamePlayer.getPlayer().getName();
    }

    public boolean isJoinedTeam(GamePlayer gamePlayer) {
        return gamePlayer != null && this.getTeam(gamePlayer) != null && !this.spectators.contains(gamePlayer);
    }

    public boolean hasTeam(EnumTeam enumTeam) {
        return this.teams.containsKey(enumTeam);
    }
}
