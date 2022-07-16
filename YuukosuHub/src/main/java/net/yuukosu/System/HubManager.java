package net.yuukosu.System;

import lombok.Getter;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class HubManager {

    @Getter
    private final HubData hubData;
    @Getter
    private final Map<UUID, HubPlayer> players = new HashMap<>();

    public HubManager() {
        this.hubData = new HubData();
    }

    public void load() {
        this.hubData.load();
    }

    public void registerPlayer(CorePlayer corePlayer) {
        this.players.put(corePlayer.getPlayer().getUniqueId(), new HubPlayer(corePlayer));
    }

    public void unregisterPlayer(UUID uuid) {
        this.players.remove(uuid);
    }

    public HubPlayer getHubPlayer(Player player) {
        return this.players.get(player.getUniqueId());
    }

    public boolean contains(Player player) {
        return this.players.containsKey(player.getUniqueId());
    }
}
