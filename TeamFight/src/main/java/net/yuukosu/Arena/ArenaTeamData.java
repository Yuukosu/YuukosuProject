package net.yuukosu.Arena;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.List;

public class ArenaTeamData {

    @Setter
    @Getter
    private Location center;
    @Getter
    private final List<Location> spawns = new ArrayList<>();

    public ArenaTeamData() {
    }

    public void addSpawn(Location location) {
        this.spawns.add(location);
    }

    public void removeSpawn(int index) {
        this.spawns.remove(index);
    }

    public boolean complete() {
        return this.spawns.size() > 0;
    }
}
