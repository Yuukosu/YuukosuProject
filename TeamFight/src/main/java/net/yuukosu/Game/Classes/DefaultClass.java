package net.yuukosu.Game.Classes;

import net.yuukosu.System.ItemCreator;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class DefaultClass extends ItemClass {

    public DefaultClass() {
        super(
                "デフォルト",
                new String[]{
                        "§7説明"
                },
                new ItemStack(Material.IRON_SWORD)
        );
        super.addItem(0, new ItemCreator(Material.STONE_SWORD).create())
                .addItem(1, new ItemCreator(Material.FISHING_ROD).create());
    }
}
