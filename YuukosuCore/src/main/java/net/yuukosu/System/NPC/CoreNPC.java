package net.yuukosu.System.NPC;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import lombok.Getter;
import net.minecraft.server.v1_8_R3.*;
import net.yuukosu.System.Hologram;
import net.yuukosu.YuukosuCore;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_8_R3.CraftServer;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_8_R3.scoreboard.CraftScoreboard;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class CoreNPC {

    @Getter
    private final EntityPlayer npc;
    @Getter
    private final Hologram hologramName;
    @Getter
    private final ScoreboardTeam scoreboardTeam;

    public CoreNPC(World world) {
        String name = String.valueOf(UUID.randomUUID().hashCode());
        MinecraftServer minecraftServer = ((CraftServer) Bukkit.getServer()).getServer();
        WorldServer worldServer = ((CraftWorld) world).getHandle();
        GameProfile gameProfile = new GameProfile(UUID.randomUUID(), name);
        PlayerInteractManager playerInteractManager = new PlayerInteractManager(worldServer);
        this.npc = new EntityPlayer(minecraftServer, worldServer, gameProfile, playerInteractManager);
        this.hologramName = new Hologram(world);
        this.scoreboardTeam = new ScoreboardTeam(((CraftScoreboard) YuukosuCore.getCoreManager().getCoreScoreboard()).getHandle(), name);
        this.scoreboardTeam.setNameTagVisibility(ScoreboardTeamBase.EnumNameTagVisibility.NEVER);
        this.scoreboardTeam.setPrefix("§8[NPC] ");
        this.scoreboardTeam.setAllowFriendlyFire(false);
        this.scoreboardTeam.setCanSeeFriendlyInvisibles(false);
    }

    public void spawnAll(Location location) {
        Bukkit.getOnlinePlayers().forEach(player -> this.spawn(player, location));
    }

    public void destroyAll() {
        Bukkit.getOnlinePlayers().forEach(this::destroy);
    }

    public void spawn(Player player, Location location) {
        this.destroy(player);

        this.npc.setPosition(location.getX(), location.getY(), location.getZ());
        this.npc.getDataWatcher().watch(10, (byte) 0xFF);

        PlayerConnection connection = ((CraftPlayer) player).getHandle().playerConnection;
        PacketPlayOutPlayerInfo packet1 = new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, this.npc);
        PacketPlayOutNamedEntitySpawn packet2 = new PacketPlayOutNamedEntitySpawn(this.npc);
        PacketPlayOutEntityMetadata packet3 = new PacketPlayOutEntityMetadata(this.npc.getId(), this.npc.getDataWatcher(), true);
        PacketPlayOutEntityHeadRotation packet4 = new PacketPlayOutEntityHeadRotation(this.npc, (byte) ((location.getYaw() * 256F) / 360F));
        PacketPlayOutScoreboardTeam packet5 = new PacketPlayOutScoreboardTeam(this.scoreboardTeam, 1);
        PacketPlayOutScoreboardTeam packet6 = new PacketPlayOutScoreboardTeam(this.scoreboardTeam, 0);
        PacketPlayOutScoreboardTeam packet7 = new PacketPlayOutScoreboardTeam(this.scoreboardTeam, Collections.singletonList(this.npc.getName()), 3);
        PacketPlayOutAnimation packet8 = new PacketPlayOutAnimation(this.npc, 0);
        PacketPlayOutPlayerInfo packet9 = new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER, this.npc);

        connection.sendPacket(packet1);
        connection.sendPacket(packet2);
        connection.sendPacket(packet3);
        connection.sendPacket(packet4);
        connection.sendPacket(packet5);
        connection.sendPacket(packet6);
        connection.sendPacket(packet7);
        connection.sendPacket(packet8);
        Bukkit.getScheduler().runTaskLater(YuukosuCore.getInstance(), () -> connection.sendPacket(packet9), 20L);
        this.hologramName.setLocation(location);
        this.hologramName.spawn(player);
    }

    public void destroy(Player player) {
        this.hologramName.destroy(player);

        PlayerConnection connection = ((CraftPlayer) player).getHandle().playerConnection;
        PacketPlayOutPlayerInfo packet1 = new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER, this.npc);
        PacketPlayOutEntityDestroy packet2 = new PacketPlayOutEntityDestroy(this.npc.getId());
        connection.sendPacket(packet1);
        connection.sendPacket(packet2);
    }

    public void setSkin(String texture, String signature) {
        GameProfile profile = this.npc.getProfile();
        profile.getProperties().clear();
        profile.getProperties().put("textures", new Property("textures", texture, signature));
    }

    public void setHologramName(List<String> names) {
        names.forEach(this.hologramName::addMessage);
    }
}
