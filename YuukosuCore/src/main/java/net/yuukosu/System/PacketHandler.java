package net.yuukosu.System;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import lombok.Getter;
import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import net.minecraft.server.v1_8_R3.PacketPlayInUpdateSign;
import net.minecraft.server.v1_8_R3.PacketPlayInUseEntity;
import net.yuukosu.System.NPC.ClickableNPC;
import net.yuukosu.YuukosuCore;
import org.bukkit.Bukkit;

import java.lang.reflect.Field;
import java.util.Arrays;

public class PacketHandler extends ChannelDuplexHandler {

    @Getter
    private final CorePlayer corePlayer;

    public PacketHandler(CorePlayer corePlayer) {
        this.corePlayer = corePlayer;
    }

    @Override
    public void channelRead(ChannelHandlerContext channelHandlerContext, Object packet) throws Exception {
        if (packet instanceof PacketPlayInUpdateSign) {
            if (this.corePlayer.isInviteCodeRequesting()) {
                PacketPlayInUpdateSign updateSign = (PacketPlayInUpdateSign) packet;
                IChatBaseComponent baseComponent = Arrays.stream(updateSign.b()).filter(iChatBaseComponent -> !iChatBaseComponent.getText().isEmpty()).findFirst().orElse(null);
                String code = baseComponent == null ? "" : baseComponent.getText();

                if (code.equalsIgnoreCase("exit") || code.equalsIgnoreCase("quit")) {
                    Bukkit.getScheduler().runTaskLater(YuukosuCore.getInstance(), () -> this.corePlayer.getPlayer().kickPlayer("§cサーバーから退出しました。"), 20L);
                    return;
                }

                if (this.corePlayer.receiveInviteCode(code)) {
                    Bukkit.getScheduler().runTaskLater(YuukosuCore.getInstance(), () -> this.corePlayer.setCanMove(true), 10L);

                    this.corePlayer.sendTitle("§e§lWELCOME!", "§7招待コードが認証されました！", 0, 20 * 3, 20);
                    this.corePlayer.getPlayer().playSound(this.corePlayer.getPlayer().getLocation(), "note.pling", 3F, 2F);
                } else {
                    this.corePlayer.getPlayer().sendMessage(baseComponent == null ? "§c招待コードを入力してください。" : "§cこの招待コードは無効です。");
                    this.corePlayer.getPlayer().playSound(this.corePlayer.getPlayer().getLocation(), "mob.endermen.portal", 3F, 0F);
                }

                return;
            }
        }

        if (packet instanceof PacketPlayInUseEntity) {
            PacketPlayInUseEntity useEntity = (PacketPlayInUseEntity) packet;
            Field field = PacketPlayInUseEntity.class.getDeclaredField("a");
            field.setAccessible(true);
            int id = field.getInt(useEntity);

            if (ClickableNPC.getClickableNpcs().containsKey(id)) {
                ClickableNPC clickableNPC = ClickableNPC.getClickableNpcs().get(id);

                if (clickableNPC.isDelaying(this.getCorePlayer().getPlayer())) {
                    return;
                }

                clickableNPC.startDelay(this.getCorePlayer().getPlayer());
                clickableNPC.onClick(this.corePlayer, useEntity.a());
            }
        }

        super.channelRead(channelHandlerContext, packet);
    }

    @Override
    public void write(ChannelHandlerContext channelHandlerContext, Object packet, ChannelPromise channelPromise) throws Exception {
        super.write(channelHandlerContext, packet, channelPromise);
    }
}
