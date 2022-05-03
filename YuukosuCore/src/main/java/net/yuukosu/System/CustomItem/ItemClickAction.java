package net.yuukosu.System.CustomItem;

import lombok.Getter;
import lombok.Setter;
import net.yuukosu.YuukosuCore;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.ArrayList;
import java.util.List;

public abstract class ItemClickAction {

    @Setter
    @Getter
    private long delay;
    protected final List<Player> delayingPlayers = new ArrayList<>();

    public ItemClickAction() {
        this.delay = 10;
    }

    public void startDelay(Player player) {
        if (!this.isDelaying(player)) {
            this.delayingPlayers.add(player);
            Bukkit.getScheduler().runTaskLater(YuukosuCore.getInstance(), () -> this.delayingPlayers.remove(player), this.delay);
        }
    }

    public boolean isDelaying(Player player) {
        return this.delayingPlayers.contains(player);
    }

    public abstract void click(PlayerInteractEvent e);
}
