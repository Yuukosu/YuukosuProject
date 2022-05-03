package net.yuukosu.System;

import lombok.Getter;
import net.yuukosu.YuukosuHub;
import org.bukkit.Location;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class HubPlayer {

    @Getter
    private final CorePlayer corePlayer;
    @Getter
    private final FrontEnd frontEnd;

    public HubPlayer(CorePlayer corePlayer) {
        this.corePlayer = corePlayer;
        this.frontEnd = new FrontEnd(this.corePlayer);
    }

    public void init() {
        YuukosuHub.getHubManager().getPlayers().values().forEach(hubPlayer -> hubPlayer.getFrontEnd().updateDisplayName(true));

        this.frontEnd.startTask(10);

        if (this.corePlayer.getPlayerRank().hasPriority(PlayerRank.VIP)) {
            this.corePlayer.getPlayer().setAllowFlight(true);
            this.corePlayer.getPlayer().setFlying(true);
        }

        Location spawn = YuukosuHub.getHubManager().getHubData().getSpawn();
        if (spawn != null) {
            int radius = 2;
            List<Location> locations = new LinkedList<>();

            for (double x = spawn.getX() - radius; x < spawn.getX() + radius; x += 0.5D) {
                for (double z = spawn.getZ() - radius; z < spawn.getZ() + radius; z += 0.5D) {
                    locations.add(new Location(spawn.getWorld(), x, spawn.getY(), z, spawn.getYaw(), spawn.getPitch()));
                }
            }

            int index = new Random().nextInt(locations.size());
            this.corePlayer.getPlayer().teleport(locations.get(index));
        }
    }
}
