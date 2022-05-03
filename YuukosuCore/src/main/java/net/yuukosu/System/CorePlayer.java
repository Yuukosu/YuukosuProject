package net.yuukosu.System;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import io.netty.channel.Channel;
import io.netty.channel.ChannelDuplexHandler;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.server.v1_8_R3.*;
import net.yuukosu.System.GuiCreator.YuukosuGui;
import net.yuukosu.Utils.DatabaseUtils;
import net.yuukosu.YuukosuCore;
import org.bson.Document;
import org.bukkit.GameMode;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Arrays;

public class CorePlayer {

    @Getter
    private final Player player;
    @Setter
    @Getter
    private PlayerRank playerRank;
    @Setter
    @Getter
    private InviteCode inviteCode;
    @Setter
    @Getter
    private YuukosuGui currentGui;
    @Getter
    protected boolean inviteCodeRequesting;

    public CorePlayer(Player player) {
        this.player = player;
        this.playerRank = PlayerRank.DEFAULT;
    }

    public void cleanPlayerStatus(GameMode gameMode) {
        this.player.setGameMode(gameMode);
        this.player.setMaxHealth(20);
        this.player.setHealth(this.player.getMaxHealth());
        this.player.setFoodLevel(20);
        this.player.setExp(0);
        this.player.setTotalExperience(0);
        this.player.setLevel(0);
        this.player.setFireTicks(0);

        for (int i = 0; i < this.player.getInventory().getSize(); i++) {
            this.player.getInventory().clear(i);
        }

        for (PotionEffect potionEffect : this.player.getActivePotionEffects()) {
            this.player.removePotionEffect(potionEffect.getType());
        }
    }

    public void setCanMove(boolean b) {
        if (b) {
            this.player.setWalkSpeed(0.2F);
            this.player.setFlySpeed(0.1F);
            this.player.removePotionEffect(PotionEffectType.JUMP);
            this.player.removePotionEffect(PotionEffectType.BLINDNESS);
            return;
        }

        this.player.setWalkSpeed(0.0F);
        this.player.setFlySpeed(0.0F);
        this.player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 20 * 9999, -10, false, false));
        this.player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 20 * 9999, 255, false, false));
    }

    public void load() {
        MongoCollection<Document> collection = YuukosuCore.getPlayersCollection();
        Document playerDoc = collection.find(Filters.eq("UUID", this.player.getUniqueId().toString())).first();

        if (playerDoc != null) {
            if (playerDoc.containsKey("RANK")) {
                this.playerRank = PlayerRank.valueOf(playerDoc.getString("RANK"));
            }

            if (playerDoc.containsKey("INVITE_CODE")) {
                this.inviteCode = YuukosuCore.getCoreManager().getInviteCodeManager().getInviteCode(playerDoc.getString("INVITE_CODE"));
            }
        }
    }

    public void save() {
        MongoCollection<Document> collection = YuukosuCore.getPlayersCollection();
        Document document = new Document();
        String uniqueId = this.player.getUniqueId().toString();
        document.put("UUID", uniqueId);
        document.put("NAME", this.player.getName());

        if (this.playerRank != null) {
            document.put("RANK", this.playerRank.name());
        }

        if (this.inviteCode != null) {
            document.put("INVITE_CODE", this.inviteCode.getCode());
        }

        collection.updateOne(Filters.eq(uniqueId), new Document("$set", document), DatabaseUtils.getUpdateOptions());
    }

    public void sendPackets(Packet<?>... packets) {
        Arrays.asList(packets).forEach(packet -> ((CraftPlayer) this.player).getHandle().playerConnection.sendPacket(packet));
    }

    public void sendTitle(String title, String subtitle, int fade, int stay, int out) {
        this.sendPackets(
                new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.TITLE, new ChatComponentText(title)),
                new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.SUBTITLE, new ChatComponentText(subtitle)),
                new PacketPlayOutTitle(fade, stay, out)
        );
    }

    public void sendActionbar(String message) {
        this.sendPackets(new PacketPlayOutChat(new ChatComponentText(message), (byte) 2));
    }

    public void openGui(YuukosuGui yuukosuGui) {
        this.currentGui = yuukosuGui;
        this.player.openInventory(yuukosuGui.getInventory());
    }

    public void sendServer(String serverName) {
        @SuppressWarnings("UnstableApiUsage")
        ByteArrayDataOutput byteArray = ByteStreams.newDataOutput();
        byteArray.writeUTF("ConnectOther");
        byteArray.writeUTF(this.player.getName());
        byteArray.writeUTF(serverName);

        this.player.sendPluginMessage(YuukosuCore.getInstance(), "BungeeCord", byteArray.toByteArray());
    }

    public void registerPacketHandler(ChannelDuplexHandler channelDuplexHandler) {
        ((CraftPlayer) this.player).getHandle().playerConnection.networkManager.channel.pipeline().addBefore("packet_handler", this.player.getName(), channelDuplexHandler);
    }

    public void unregisterPacketHandler() {
        Channel channel = ((CraftPlayer) this.player).getHandle().playerConnection.networkManager.channel;
        channel.eventLoop().submit(() -> {
            channel.pipeline().remove(this.player.getName());
            return null;
        });
    }

    public void requestInviteCode() {
        this.inviteCodeRequesting = true;

        TileEntitySign entitySign = new TileEntitySign();
        PacketPlayOutOpenSignEditor packet = new PacketPlayOutOpenSignEditor(entitySign.getPosition());
        this.sendPackets(packet);
    }

    public boolean receiveInviteCode(String code) {
        if (YuukosuCore.getCoreManager().getInviteCodeManager().checkCode(code)) {
            this.acceptInviteCode(YuukosuCore.getCoreManager().getInviteCodeManager().getInviteCode(code));
            return true;
        }

        TileEntitySign entitySign = new TileEntitySign();
        PacketPlayOutOpenSignEditor packet = new PacketPlayOutOpenSignEditor(entitySign.getPosition());
        this.sendPackets(packet);
        return false;
    }

    public void acceptInviteCode(InviteCode invitationInfo) {
        this.inviteCodeRequesting = false;
        this.inviteCode = invitationInfo;
        this.save();
    }

    public String getRankName() {
        return this.playerRank.getColoredPrefix() + (this.playerRank == PlayerRank.DEFAULT ? "" : " ") + this.player.getName();
    }

    public String getColoredName() {
        return this.playerRank.getColor() + this.player.getName();
    }

    public boolean hasActiveInviteCode() {
        return this.inviteCode != null && YuukosuCore.getCoreManager().getInviteCodeManager().checkCode(this.inviteCode.getCode());
    }

    public boolean hasData() {
        return YuukosuCore.getPlayersCollection().find(Filters.eq(this.player.getUniqueId().toString())).first() != null;
    }
}
