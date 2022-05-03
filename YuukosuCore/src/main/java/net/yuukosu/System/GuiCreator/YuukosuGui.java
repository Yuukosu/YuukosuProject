package net.yuukosu.System.GuiCreator;

import lombok.Getter;

import lombok.Setter;
import net.yuukosu.YuukosuCore;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public abstract class YuukosuGui {

    @Getter
    private final Inventory inventory;
    @Getter
    private final Map<Integer, List<GuiButton>> buttons = new HashMap<>();
    @Getter
    private final Set<Player> delayingPlayers = new HashSet<>();
    @Setter
    @Getter
    private long clickDelay;

    public YuukosuGui(int size, String title) {
        this.inventory = Bukkit.createInventory(null, size, title);
        this.clickDelay = 10;
    }

    public void setItem(ItemStack item, int slot) {
        this.inventory.setItem(slot, item);
    }

    public void createButton(int slot, GuiButton guiButton) {
        if (this.buttons.containsKey(slot)) {
            this.buttons.get(slot).add(guiButton);
            return;
        }

        this.buttons.put(slot, new ArrayList<GuiButton>(){{this.add(guiButton);}});
    }

    public void clearInventory() {
        for (int i = 0; i < this.inventory.getSize(); i++) {
            this.inventory.clear(i);
        }
    }

    public void clearButtons() {
        this.buttons.clear();
    }

    public void startDelay(Player player) {
        if (!this.isDelaying(player)) {
            this.delayingPlayers.add(player);
            Bukkit.getScheduler().runTaskLater(YuukosuCore.getInstance(), () -> this.delayingPlayers.remove(player), this.clickDelay);
        }
    }

    public boolean isDelaying(Player player) {
        return this.delayingPlayers.contains(player);
    }

    public abstract void open(InventoryOpenEvent e);
    public abstract void update();
    public abstract void close(InventoryCloseEvent e);
}
