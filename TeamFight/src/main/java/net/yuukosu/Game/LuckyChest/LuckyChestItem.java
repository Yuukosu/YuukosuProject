package net.yuukosu.Game.LuckyChest;

import lombok.Getter;
import org.bukkit.inventory.ItemStack;

public class LuckyChestItem {

    @Getter
    private final String displayName;
    @Getter
    private final ItemStack displayItem;
    @Getter
    private final ItemStack[] items;

    public LuckyChestItem(String displayName, ItemStack displayItem, ItemStack... items) {
        this.displayName = displayName;
        this.displayItem = displayItem;
        this.items = items;
    }

    public String getName() {
        return this.displayName + (this.displayItem.getAmount() > 1 ? " §7x" + this.displayItem.getAmount() : "");
    }
}
