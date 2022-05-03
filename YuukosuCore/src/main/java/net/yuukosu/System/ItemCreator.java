package net.yuukosu.System;

import lombok.Getter;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.material.MaterialData;

import java.util.Arrays;
import java.util.List;

public class ItemCreator {

    @Getter
    protected final ItemStack itemStack;
    @Getter
    protected final MaterialData materialData;
    @Getter
    protected final ItemMeta itemMeta;

    public ItemCreator(ItemStack itemStack) {
        this.itemStack = itemStack;
        this.materialData = this.itemStack.getData();
        this.itemMeta = this.itemStack.getItemMeta();
    }

    public ItemCreator(Material material) {
        this.itemStack = new ItemStack(material);
        this.materialData = this.itemStack.getData();
        this.itemMeta = this.itemStack.getItemMeta();
    }

    public ItemStack create() {
        this.itemStack.setData(this.materialData);
        this.itemStack.setItemMeta(this.itemMeta);

        return this.itemStack;
    }

    public ItemCreator setEquipmentMeta() {
        this.setUnbreakable(true);
        this.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);

        return this;
    }

    public ItemCreator setAmount(int amount) {
        this.itemStack.setAmount(amount);
        return this;
    }

    public ItemCreator setData(byte data) {
        this.materialData.setData(data);

        return this;
    }

    public ItemCreator setDisplayName(String displayName) {
        this.itemMeta.setDisplayName(displayName);
        return this;
    }

    public ItemCreator setLore(String[] lore) {
        List<String> list = Arrays.asList(lore);
        this.itemMeta.setLore(list);
        return this;
    }

    public ItemCreator addLore(String[] lore) {
        if (this.itemMeta.hasLore()) {
            List<String> list = this.itemMeta.getLore();
            list.addAll(Arrays.asList(lore));
            this.itemMeta.setLore(list);
        } else {
            this.setLore(lore);
        }

        return this;
    }

    public ItemCreator setUnbreakable(boolean unbreakable) {
        this.itemMeta.spigot().setUnbreakable(unbreakable);
        return this;
    }

    public ItemCreator setColor(Color color) {
        if (this.itemMeta instanceof LeatherArmorMeta) {
            ((LeatherArmorMeta) this.itemMeta).setColor(color);
        }

        return this;
    }

    public ItemCreator addEnchantment(Enchantment enchantment, int i, boolean b) {
        this.itemMeta.addEnchant(enchantment, i, b);
        return this;
    }

    public ItemCreator addItemFlags(ItemFlag... itemFlags) {
        this.itemMeta.addItemFlags(itemFlags);
        return this;
    }
}
