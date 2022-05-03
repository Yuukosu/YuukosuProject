package net.yuukosu.System;

import lombok.Getter;
import org.bukkit.ChatColor;

public enum PlayerRank {
    DEFAULT("デフォルト", "", ChatColor.GRAY, false, 4, 8),
    VIP("VIP", "[VIP]", ChatColor.GREEN, false, 3, 7),
    SPECIAL("SPECIAL", "[SPECIAL]", ChatColor.LIGHT_PURPLE, false, 2, 6),
    KING("KING", "§c[§rKING§c]", ChatColor.RED, false, 2, 5),
    QUEEN("QUEEN", "§c[§rQUEEN§c]", ChatColor.RED, false, 2, 4),
    BUILDER("ビルダー", "[ビルダー]", ChatColor.DARK_AQUA, true, 1, 3),
    MODERATOR("モデレーター", "[モデレーター]", ChatColor.DARK_GREEN, true, 1, 2),
    ADMIN("管理者", "[管理者]", ChatColor.RED, true, 0, 1),
    ASO("ASO", "§a[§rASO§a]", ChatColor.GREEN, true, 0, 0);

    @Getter
    private final String name;
    @Getter
    private final String prefix;
    @Getter
    private final ChatColor color;
    @Getter
    private final boolean staff;
    @Getter
    private final int priority;
    @Getter
    private final int sortNumber;

    PlayerRank(String name, String prefix, ChatColor color, boolean staff, int priority, int sortNumber) {
        this.name = name;
        this.prefix = prefix;
        this.color = color;
        this.staff = staff;
        this.priority = priority;
        this.sortNumber = sortNumber;
    }

    public String getColoredName() {
        return this.color + this.name;
    }

    public String getColoredPrefix() {
        return this.color + this.prefix;
    }

    public boolean hasPriority(PlayerRank playerRank) {
        return this.priority <= playerRank.priority;
    }
}
