package net.yuukosu.Game.Classes;

import lombok.Getter;
import lombok.Setter;
import net.yuukosu.Game.GamePlayer;
import net.yuukosu.System.ItemCreator;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.HashMap;
import java.util.Map;

public class ItemClass {

    @Getter
    private final ItemStack icon;
    @Getter
    private final Map<Integer, ItemStack> items = new HashMap<>();
    @Setter
    @Getter
    private ItemStack helmet;
    @Setter
    @Getter
    private ItemStack chestplate;
    @Setter
    @Getter
    private ItemStack leggings;
    @Setter
    @Getter
    private ItemStack boots;

    public ItemClass(String name, String[] description, ItemStack icon) {
        this.icon = new ItemCreator(icon)
                .setDisplayName("§a" + name)
                .setLore(description)
                .addItemFlags(ItemFlag.values())
                .setUnbreakable(true)
                .create();
    }

    public ItemClass addItem(int slot, ItemStack itemStack) {
        this.items.put(slot, new ItemCreator(itemStack).setEquipmentMeta().create());
        return this;
    }

    public void give(Player player) {
        this.giveItems(player);
        this.equipArmor(player);
    }

    public void giveItems(Player player) {
        this.items.forEach((key, value) -> player.getInventory().setItem(key, value));
    }

    public void equipArmor(Player player) {
        if (this.helmet != null) {
            player.getInventory().setHelmet(new ItemCreator(this.helmet).setEquipmentMeta().create());
        }

        if (this.chestplate != null) {
            player.getInventory().setChestplate(new ItemCreator(this.chestplate).setEquipmentMeta().create());
        }

        if (this.leggings != null) {
            player.getInventory().setLeggings(new ItemCreator(this.leggings).setEquipmentMeta().create());
        }

        if (this.boots != null) {
            player.getInventory().setBoots(new ItemCreator(this.boots).setEquipmentMeta().create());
        }
    }

    public void startBuff(GamePlayer gamePlayer) {
        PotionEffect speed = new PotionEffect(PotionEffectType.SPEED, 20 * 5, 1, false, false);
        gamePlayer.getPlayer().addPotionEffect(speed, true);
    }

    public void killBuff(GamePlayer gamePlayer) {
        PotionEffect speed = new PotionEffect(PotionEffectType.SPEED, 20 * 5, 1, false, false);
        PotionEffect regeneration = new PotionEffect(PotionEffectType.REGENERATION, 20 * 3, 2, false, false);
        gamePlayer.getPlayer().addPotionEffect(speed, true);
        gamePlayer.getPlayer().addPotionEffect(regeneration, true);
    }

    public void respawnBuff(GamePlayer gamePlayer) {
        PotionEffect speed = new PotionEffect(PotionEffectType.SPEED, 20 * 5, 1, false, false);
        gamePlayer.getPlayer().addPotionEffect(speed, true);
    }
}
