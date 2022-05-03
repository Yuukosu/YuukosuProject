package net.yuukosu.System;

import lombok.Getter;
import net.minecraft.server.v1_8_R3.*;
import net.yuukosu.YuukosuCore;
import net.yuukosu.YuukosuHub;
import org.bukkit.craftbukkit.v1_8_R3.scoreboard.CraftScoreboard;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashSet;
import java.util.Set;

public class FrontEnd {

    @Getter
    private final CorePlayer corePlayer;
    protected final Scoreboard scoreboard;
    protected final ScoreboardObjective scoreboardObjective;
    protected ScoreboardTeam scoreboardTeam;
    protected final Sidebar sidebar;
    protected PlayerRank oldPlayerRank;

    public FrontEnd(CorePlayer corePlayer) {
        this.corePlayer = corePlayer;
        this.scoreboard = ((CraftScoreboard) YuukosuCore.getCoreManager().getCoreScoreboard()).getHandle();
        this.scoreboardObjective = new ScoreboardObjective(this.scoreboard, "sidebar", IScoreboardCriteria.b);
        this.scoreboardTeam = new ScoreboardTeam(this.scoreboard, this.corePlayer.getPlayer().getName());
        this.sidebar = new Sidebar(this.scoreboard, this.scoreboardObjective);
    }

    public void startTask(long delay) {
        new BukkitRunnable() {

            @Override
            public void run() {
                if (!YuukosuHub.getHubManager().contains(FrontEnd.this.corePlayer.getPlayer())) {
                    this.cancel();
                    return;
                }

                FrontEnd.this.updateDisplayName(false);
                FrontEnd.this.updateSidebar();
            }
        }.runTaskTimer(YuukosuHub.getInstance(), 0L, delay);
    }

    public void updateDisplayName(boolean force) {
        if (this.oldPlayerRank != this.corePlayer.getPlayerRank() || force) {
            this.scoreboardTeam = new ScoreboardTeam(this.scoreboard, this.corePlayer.getPlayerRank().getSortNumber() + this.corePlayer.getPlayerRank().name());
            this.scoreboardTeam.setPrefix(this.corePlayer.getPlayerRank().getColoredPrefix() + (this.corePlayer.getPlayerRank() == PlayerRank.DEFAULT ? "" : " "));

            Set<String> players = new HashSet<>();

            YuukosuCore.getCoreManager().getPlayers().values().forEach(corePlayer -> {
                if (corePlayer.getPlayerRank() == FrontEnd.this.corePlayer.getPlayerRank()) {
                    players.add(corePlayer.getPlayer().getName());
                }
            });

            PacketPlayOutScoreboardTeam removeTeamPacket = new PacketPlayOutScoreboardTeam(this.scoreboardTeam, 1);
            PacketPlayOutScoreboardTeam createTeamPacket = new PacketPlayOutScoreboardTeam(this.scoreboardTeam, 0);
            PacketPlayOutScoreboardTeam addTeamPacket = new PacketPlayOutScoreboardTeam(this.scoreboardTeam, players, 3);
            PacketPlayOutScoreboardTeam updateTeamPacket = new PacketPlayOutScoreboardTeam(this.scoreboardTeam, 2);

            YuukosuHub.getHubManager().getPlayers().values().forEach(hubPlayer -> hubPlayer.getCorePlayer().sendPackets(removeTeamPacket, createTeamPacket, addTeamPacket, updateTeamPacket));

            this.oldPlayerRank = this.corePlayer.getPlayerRank();
        }
    }

    public void updateSidebar() {
        this.sidebar.setTitle("§e§lNETWORK");
        this.sidebar.setSidebarMessages(new String[]{
                "",
                "ランク: " + this.corePlayer.getPlayerRank().getColoredName(),
                " ",
                "§ewww.example.net"
        });
        this.sidebar.updateSidebar(this.corePlayer.getPlayer());
    }
}
