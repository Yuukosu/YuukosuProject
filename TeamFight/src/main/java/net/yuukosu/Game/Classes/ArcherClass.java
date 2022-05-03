package net.yuukosu.Game.Classes;

import net.yuukosu.System.ItemCreator;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

public class ArcherClass extends ItemClass {

    public ArcherClass() {
        super(
                "アーチャー",
                new String[]{
                        "§7説明"
                },
                new ItemStack(Material.BOW)
        );
        super.addItem(0, new ItemCreator(Material.BOW)
                        .addEnchantment(Enchantment.ARROW_DAMAGE, 1, true)
                        .addEnchantment(Enchantment.ARROW_INFINITE, 1, true)
                        .create())
                .addItem(1, new ItemCreator(Material.WOOD_SWORD).create())
                .addItem(2, new ItemCreator(Material.ARROW).setAmount(1).create());
    }
}
