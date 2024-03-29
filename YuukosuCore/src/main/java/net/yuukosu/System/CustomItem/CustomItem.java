package net.yuukosu.System.CustomItem;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class CustomItem {

    @Getter
    private final String name;
    @Getter
    private final ItemStack itemStack;
    @Getter
    private final ItemNBT itemNBT;
    @Setter
    @Getter
    private ItemAction itemAction;

    public CustomItem(String name, ItemStack itemStack) {
        this.name = name;
        this.itemStack = itemStack;
        this.itemNBT = new ItemNBT(this.itemStack);
    }

    public CustomItem(String name, Material material) {
        this.name = name;
        this.itemStack = new ItemStack(material);
        this.itemNBT = new ItemNBT(this.itemStack);
    }

    public ItemStack create() {
        return this.itemNBT.setString("CUSTOM_ITEM", this.name).create();
    }
}
