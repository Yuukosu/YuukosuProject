package net.yuukosu.System;

import lombok.Getter;
import net.yuukosu.System.CustomItem.CustomItem;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;

import java.util.*;

public class CoreManager {

    @Getter
    private final Map<UUID, CorePlayer> players = new HashMap<>();
    @Getter
    private final List<CustomItem> customItems = new ArrayList<>();
    @Getter
    private final InviteCodeManager inviteCodeManager;
    @Getter
    private final Scoreboard coreScoreboard;

    public CoreManager() {
        this.inviteCodeManager = new InviteCodeManager();
        this.coreScoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
    }

    public void init() {
        this.inviteCodeManager.load();
    }

    public void registerCustomItem(CustomItem customItem) {
        this.customItems.add(customItem);
    }

    public CustomItem getCustomItem(String name) {
        return this.customItems.stream().filter(customItem -> customItem.getName().equals(name)).findFirst().orElse(null);
    }

    public void registerPlayer(Player player) {
        if (!this.contains(player)) {
            this.players.put(player.getUniqueId(), new CorePlayer(player));
        }
    }

    public void unregisterPlayer(UUID uuid) {
        this.players.remove(uuid);
    }

    public CorePlayer getCorePlayer(Player player) {
        return this.players.get(player.getUniqueId());
    }

    public boolean contains(Player player) {
        return this.players.containsKey(player.getUniqueId());
    }
}
