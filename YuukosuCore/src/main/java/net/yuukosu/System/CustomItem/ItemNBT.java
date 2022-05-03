package net.yuukosu.System.CustomItem;

import net.minecraft.server.v1_8_R3.ItemStack;
import net.minecraft.server.v1_8_R3.NBTTagCompound;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;

public class ItemNBT {

    protected final ItemStack itemStack;
    protected final NBTTagCompound nbtTagCompound;

    public ItemNBT(org.bukkit.inventory.ItemStack itemStack) {
        this.itemStack = CraftItemStack.asNMSCopy(itemStack);
        this.nbtTagCompound = this.itemStack.hasTag() ? this.itemStack.getTag() : new NBTTagCompound();
    }

    public org.bukkit.inventory.ItemStack create() {
        this.itemStack.setTag(this.nbtTagCompound);

        return CraftItemStack.asBukkitCopy(this.itemStack);
    }

    public ItemNBT setString(String key, String value) {
        this.nbtTagCompound.setString(key, value);

        return this;
    }

    public ItemNBT setInt(String key, int value) {
        this.nbtTagCompound.setInt(key, value);

        return this;
    }

    public String getString(String key) {
        return this.nbtTagCompound.getString(key);
    }

    public int getInt(String key) {
        return this.nbtTagCompound.getInt(key);
    }

    public boolean hasTag(String key) {
        return this.nbtTagCompound.hasKey(key);
    }
}
