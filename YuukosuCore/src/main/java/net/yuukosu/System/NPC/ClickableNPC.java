package net.yuukosu.System.NPC;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.server.v1_8_R3.PacketPlayInUseEntity;
import net.yuukosu.System.CorePlayer;
import net.yuukosu.YuukosuCore;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class ClickableNPC extends CoreNPC {

    @Getter
    private static final Map<Integer, ClickableNPC> clickableNpcs = new HashMap<>();
    @Getter
    private final List<Player> delayingPlayers = new ArrayList<>();
    @Setter
    @Getter
    private long delay;

    public ClickableNPC(World world) {
        super(world);
        this.delay = 10L;
    }

    public void startDelay(Player player) {
        if (this.delayingPlayers.contains(player)) {
            return;
        }

        this.delayingPlayers.add(player);
        Bukkit.getScheduler().runTaskLater(YuukosuCore.getInstance(), () -> this.delayingPlayers.remove(player), this.delay);
    }

    public boolean isDelaying(Player player) {
        return this.delayingPlayers.contains(player);
    }

    @Override
    public void spawnAll(Location location) {
        super.spawnAll(location);
        ClickableNPC.getClickableNpcs().put(this.getNpc().getId(), this);
    }

    @Override
    public void spawn(Player player, Location location) {
        super.spawn(player, location);
        ClickableNPC.getClickableNpcs().put(this.getNpc().getId(), this);
    }

    @Override
    public void destroyAll() {
        super.destroyAll();
        ClickableNPC.getClickableNpcs().remove(this.getNpc().getId());
    }

    @Override
    public void destroy(Player player) {
        super.destroy(player);
        ClickableNPC.getClickableNpcs().remove(this.getNpc().getId());
    }

    public abstract void onClick(CorePlayer corePlayer, PacketPlayInUseEntity.EnumEntityUseAction action);
}
