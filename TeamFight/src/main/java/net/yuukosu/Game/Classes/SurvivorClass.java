package net.yuukosu.Game.Classes;

import net.yuukosu.System.ItemCreator;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class SurvivorClass extends ItemClass {

    public SurvivorClass() {
        super("サバイバー", new String[]{
                "§7説明",
        }, new ItemStack(Material.IRON_AXE));
        super.addItem(0, new ItemCreator(Material.IRON_AXE).create())
                .addItem(1, new ItemCreator(Material.GOLDEN_APPLE).setAmount(3).create());
    }
}
