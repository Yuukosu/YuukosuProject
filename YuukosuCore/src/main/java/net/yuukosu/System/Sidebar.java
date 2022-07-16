package net.yuukosu.System;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.Arrays;

public class Sidebar {

    @Getter
    private final Scoreboard scoreboard;
    @Getter
    private final ScoreboardObjective scoreboardObjective;
    @Setter
    @Getter
    private String title;
    @Setter
    @Getter
    private String[] sidebarMessages;
    protected String[] oldSidebarMessages;

    public Sidebar(@NonNull String title, @NonNull String[] sidebarMessages, @NonNull Scoreboard scoreboard, @NonNull ScoreboardObjective scoreboardObjective) {
        this.scoreboard = scoreboard;
        this.scoreboardObjective = scoreboardObjective;
        this.title = title;
        this.sidebarMessages = sidebarMessages;
    }

    public Sidebar(@NonNull Scoreboard scoreboard, @NonNull ScoreboardObjective scoreboardObjective) {
        this.scoreboard = scoreboard;
        this.scoreboardObjective = scoreboardObjective;
        this.title = "";
        this.sidebarMessages = new String[]{};
    }

    protected final ScoreboardScore[] convertStringToScoreArray(@NonNull String[] sidebarMessages) {
        int length = Math.min(sidebarMessages.length, 15);
        ScoreboardScore[] scores = new ScoreboardScore[length];

        int nullCount = 0;
        for (int i = length - 1; i >= 0; i--) {
            if (sidebarMessages[i] != null) {
                scores[i] = new ScoreboardScore(this.scoreboard, this.scoreboardObjective, sidebarMessages[i]);
                scores[i].setScore((length - i) - nullCount);
                continue;
            }

            nullCount++;
        }

        return scores;
    }

    protected final void sendPackets(Player player, Packet<?>... packets) {
        Arrays.asList(packets).forEach(packet -> ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet));
    }

    public void updateSidebar(Player player) {
        this.scoreboardObjective.setDisplayName(this.title);

        ScoreboardScore[] scores = this.convertStringToScoreArray(this.sidebarMessages);
        ScoreboardScore[] oldScores = this.oldSidebarMessages == null ? null : this.convertStringToScoreArray(this.oldSidebarMessages);

        PacketPlayOutScoreboardObjective updateObj = new PacketPlayOutScoreboardObjective(this.scoreboardObjective, oldScores == null ? 0 : 2);
        this.sendPackets(player, updateObj);

        if (!Arrays.equals(scores, oldScores)) {
            for (int i = 0; i < scores.length; i++) {
                String newName = scores[i] == null ? null : scores[i].getPlayerName();

                if (oldScores != null && oldScores.length > i) {
                    String oldName = oldScores[i] == null ? null : oldScores[i].getPlayerName();

                    if ((newName == null || !newName.equals(oldName)) && oldName != null) {
                        PacketPlayOutScoreboardScore packet1 = new PacketPlayOutScoreboardScore(oldName, this.scoreboardObjective);
                        this.sendPackets(player, packet1);
                    }
                }

                if (scores[i] != null) {
                    PacketPlayOutScoreboardScore packet2 = new PacketPlayOutScoreboardScore(scores[i]);
                    this.sendPackets(player, packet2);
                }
            }

            PacketPlayOutScoreboardDisplayObjective updateDisplay = new PacketPlayOutScoreboardDisplayObjective(1, this.scoreboardObjective);
            this.sendPackets(player, updateDisplay);
        }

        this.oldSidebarMessages = this.sidebarMessages;
    }
}
