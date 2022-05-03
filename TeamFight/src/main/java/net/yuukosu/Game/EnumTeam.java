package net.yuukosu.Game;

import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.DyeColor;

import java.util.Arrays;

public enum EnumTeam {
    RED("赤", ChatColor.RED, DyeColor.RED.getColor(), 0),
    BLUE("青", ChatColor.BLUE, DyeColor.BLUE.getColor(), 1),
    GREEN("緑", ChatColor.GREEN, DyeColor.GREEN.getColor(), 2),
    YELLOW("黄", ChatColor.YELLOW, DyeColor.YELLOW.getColor(), 3);

    @Getter
    private final String name;
    @Getter
    private final ChatColor chatColor;
    @Getter
    private final Color color;
    @Getter
    private final int sortNumber;

    EnumTeam(String name, ChatColor chatColor, Color color, int sortNumber) {
        this.name = name;
        this.chatColor = chatColor;
        this.color = color;
        this.sortNumber = sortNumber;
    }

    public static EnumTeam getSortedTeam(int sortNumber) {
        return Arrays.stream(EnumTeam.values()).filter(enumTeam -> enumTeam.getSortNumber() == sortNumber).findFirst().orElse(null);
    }

    public String getColoredName() {
        return this.chatColor + this.name;
    }
}
