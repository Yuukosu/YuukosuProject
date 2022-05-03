package net.yuukosu.Game;

import lombok.Getter;
import net.minecraft.server.v1_8_R3.*;
import net.yuukosu.System.PlayerRank;
import net.yuukosu.System.Sidebar;
import net.yuukosu.YuukosuCore;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_8_R3.scoreboard.CraftScoreboard;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public class PlayerFrontend {

    @Getter
    private final GamePlayer gamePlayer;
    @Getter
    private final GameManager gameManager;
    private final Scoreboard scoreboard;
    private final Sidebar sidebar;

    public PlayerFrontend(GamePlayer gamePlayer, GameManager gameManager) {
        this.gamePlayer = gamePlayer;
        this.gameManager = gameManager;
        this.scoreboard = ((CraftScoreboard) YuukosuCore.getCoreManager().getCoreScoreboard()).getHandle();
        this.sidebar = new Sidebar(this.scoreboard, new ScoreboardObjective(this.scoreboard, "sidebar", IScoreboardCriteria.b));
    }

    public void update(boolean displayName, boolean sidebar) {
        if (displayName) {
            this.updateDisplayName();
        }

        if (sidebar) {
            this.updateSidebar();
        }
    }

    public void updateActionbar() {
        this.gamePlayer.getCorePlayer().sendActionbar("§e選択中のクラス: §a" + this.gamePlayer.getEnumClass().getName());
    }

    public void createHealthBar() {
        ScoreboardObjective healthBarObjective = new ScoreboardObjective(this.scoreboard, "healthBar", IScoreboardCriteria.g);
        healthBarObjective.setDisplayName("§c❤");
        PacketPlayOutScoreboardObjective updatePacket = new PacketPlayOutScoreboardObjective(healthBarObjective, 0);
        PacketPlayOutScoreboardDisplayObjective displayPacket = new PacketPlayOutScoreboardDisplayObjective(2, healthBarObjective);
        this.gamePlayer.getCorePlayer().sendPackets(updatePacket, displayPacket);
    }

    public void updateSidebar() {
        String date = new SimpleDateFormat("yy/MM/dd").format(new Date());
        this.sidebar.setTitle("§e§lTEAM FIGHT");

        if (this.gameManager.getGamePhase() == GamePhase.WAITING) {
            this.sidebar.setSidebarMessages(new String[]{
                            "§7" + date,
                            "",
                            "マップ: §a" + this.gameManager.getArenaManager().getMapName(),
                            "プレイヤー人数: §a" + this.gameManager.getPlayers().size(),
                            " ",
                            this.gameManager.isCountDown() ? "スタートまで §a" + this.gameManager.getCountTime() + " §r秒！" : "プレイヤーを待っています...",
                            "  ",
                            "§ewww.example.net"
                    }
            );
        } else {
            this.sidebar.setSidebarMessages(new String[]{
                            "§7" + date,
                            "",
                            "残り時間: §a" + (this.gameManager.getGamePhase() == GamePhase.STARTED ? new SimpleDateFormat("mm:ss").format(this.gameManager.getGameTime() * 1000) : "終了！"),
                            " ",
                            this.gameManager.hasTeam(EnumTeam.RED) ? EnumTeam.RED.getColoredName() + "§rチームのキル数: §a" + this.gameManager.getGameTeam(EnumTeam.RED).getKills() : null,
                            this.gameManager.hasTeam(EnumTeam.BLUE) ? EnumTeam.BLUE.getColoredName() + "§rチームのキル数: §a" + this.gameManager.getGameTeam(EnumTeam.BLUE).getKills() : null,
                            this.gameManager.hasTeam(EnumTeam.GREEN) ? EnumTeam.GREEN.getColoredName() + "§rチームのキル数: §a" + this.gameManager.getGameTeam(EnumTeam.GREEN).getKills() : null,
                            this.gameManager.hasTeam(EnumTeam.YELLOW) ? EnumTeam.YELLOW.getColoredName() + "§rチームのキル数: §a" + this.gameManager.getGameTeam(EnumTeam.YELLOW).getKills() : null,
                            "  ",
                            "あなたのキル数: §a" + this.gamePlayer.getKills(),
                            "   ",
                            "§ewww.example.net"
                    }
            );
        }

        this.sidebar.updateSidebar(this.gamePlayer.getPlayer());
    }

    public void updateDisplayName() {
        if (this.gameManager.getGamePhase() == GamePhase.WAITING) {
            for (PlayerRank playerRank : PlayerRank.values()) {
                ScoreboardTeam scoreboardTeam = new ScoreboardTeam(this.scoreboard, playerRank.getSortNumber() + playerRank.name());
                scoreboardTeam.setPrefix(playerRank.getColor().toString());
                Set<String> playerNames = new HashSet<String>(){{
                    PlayerFrontend.this.gameManager.getPlayers().stream()
                            .filter(gamePlayer -> gamePlayer.getCorePlayer().getPlayerRank() == playerRank)
                            .forEach(gamePlayer -> this.add(gamePlayer.getPlayer().getName()));
                }};

                if (playerNames.isEmpty()) {
                    continue;
                }

                PacketPlayOutScoreboardTeam removeTeamPacket = new PacketPlayOutScoreboardTeam(scoreboardTeam, 1);
                PacketPlayOutScoreboardTeam createTeamPacket = new PacketPlayOutScoreboardTeam(scoreboardTeam, 0);
                PacketPlayOutScoreboardTeam addTeamPacket = new PacketPlayOutScoreboardTeam(scoreboardTeam, playerNames, 3);
                PacketPlayOutScoreboardTeam updateTeamPacket = new PacketPlayOutScoreboardTeam(scoreboardTeam, 2);

                this.gameManager.getPlayers().forEach(gamePlayer -> gamePlayer.getCorePlayer().sendPackets(removeTeamPacket, createTeamPacket, addTeamPacket, updateTeamPacket));
            }
        } else {
            for (GameTeam gameTeam : this.gameManager.getTeams().values()) {
                EnumTeam enumTeam = this.gameManager.getTeam(gameTeam);
                ScoreboardTeam scoreboardTeam = new ScoreboardTeam(this.scoreboard, this.gameManager.getTeam(gameTeam).getSortNumber() + this.gameManager.getTeam(gameTeam).name());
                scoreboardTeam.setPrefix(enumTeam.getChatColor().toString() + ChatColor.BOLD + enumTeam.name().charAt(0) + " " + enumTeam.getChatColor());
                scoreboardTeam.setAllowFriendlyFire(false);
                scoreboardTeam.setCanSeeFriendlyInvisibles(true);
                scoreboardTeam.setNameTagVisibility(ScoreboardTeamBase.EnumNameTagVisibility.ALWAYS);

                Set<String> playerNames = new HashSet<String>() {{
                    gameTeam.getPlayers().forEach(gamePlayer -> this.add(gamePlayer.getPlayer().getName()));
                }};

                if (playerNames.isEmpty()) {
                    continue;
                }

                PacketPlayOutScoreboardTeam removeTeamPacket = new PacketPlayOutScoreboardTeam(scoreboardTeam, 1);
                PacketPlayOutScoreboardTeam createTeamPacket = new PacketPlayOutScoreboardTeam(scoreboardTeam, 0);
                PacketPlayOutScoreboardTeam addTeamPacket = new PacketPlayOutScoreboardTeam(scoreboardTeam, playerNames, 3);
                PacketPlayOutScoreboardTeam updateTeamPacket = new PacketPlayOutScoreboardTeam(scoreboardTeam, 2);

                this.gameManager.getPlayers().forEach(gamePlayer -> gamePlayer.getCorePlayer().sendPackets(removeTeamPacket, createTeamPacket, addTeamPacket, updateTeamPacket));
            }
        }
    }
}
