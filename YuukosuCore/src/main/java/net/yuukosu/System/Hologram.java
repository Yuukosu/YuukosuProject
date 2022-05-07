package net.yuukosu.System;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class Hologram {

    @Setter
    @Getter
    private Location location;
    @Getter
    private final double space;
    @Getter
    private final List<EntityArmorStand> armorStands = new ArrayList<>();

    public Hologram(Location location) {
        this.location = location;
        this.space = 0.5F;
    }

    public Hologram(Location location, float space) {
        this.location = location;
        this.space = space;
    }

    public void spawnAll() {
        Bukkit.getOnlinePlayers().forEach(this::spawn);
    }

    public void updateAll() {
        Bukkit.getOnlinePlayers().forEach(this::update);
    }

    public void destroyAll() {
        Bukkit.getOnlinePlayers().forEach(this::destroy);
    }

    public void spawn(Player player) {
        for (int i = 0; i < this.armorStands.size(); i++) {
            EntityArmorStand armorStand = this.armorStands.get(i);
            armorStand.setPosition(this.location.getX(), this.location.getY() + (this.space * i), this.location.getZ());
            ((CraftPlayer) player).getHandle().playerConnection.sendPacket(new PacketPlayOutSpawnEntityLiving(armorStand));
        }
    }

    public void destroy(Player player) {
        this.armorStands.forEach(armorStands -> ((CraftPlayer) player).getHandle().playerConnection.sendPacket(new PacketPlayOutEntityDestroy(armorStands.getId())));
    }

    public void update(Player player) {
        this.armorStands.forEach(armorStand -> {
            if (armorStand.locX != this.location.getX() || armorStand.locY != this.location.getY() || armorStand.locZ != this.location.getZ()) {
                ((CraftPlayer) player).getHandle().playerConnection.sendPacket(new PacketPlayOutEntityTeleport(armorStand.getId(), (int) (this.location.getX() * 32.0D), (int) (this.location.getY() * 32.0D), (int) (this.location.getZ() * 32.0D), (byte) 0, (byte) 0, false));
            }

            ((CraftPlayer) player).getHandle().playerConnection.sendPacket(new PacketPlayOutUpdateEntityNBT(armorStand.getId(), armorStand.getNBTTag()));
        });
    }

    public void addMessage(String message) {
        EntityArmorStand armorStand = new EntityArmorStand(((CraftWorld) this.location.getWorld()).getHandle());
        armorStand.setCustomName(message != null ? message : "");
        armorStand.setCustomNameVisible(message != null);
        armorStand.setGravity(false);
        armorStand.setInvisible(true);

        this.armorStands.add(armorStand);
    }

    public void setMessage(int index, String message) {
        if (index >= this.armorStands.size()) {
            this.addMessage(message);
            return;
        }

        EntityArmorStand armorStand = new EntityArmorStand(((CraftWorld) this.location.getWorld()).getHandle());
        armorStand.setCustomName(message != null ? message : "");
        armorStand.setCustomNameVisible(message != null);
        armorStand.setGravity(false);
        armorStand.setInvisible(true);

        this.armorStands.set(index, armorStand);
    }

    public void removeMessage(int index) {
        this.armorStands.remove(index);
    }
}
