package net.yuukosu.System.NPC;

import net.minecraft.server.v1_8_R3.PacketPlayInUseEntity;
import net.yuukosu.System.CorePlayer;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.Arrays;

public class MinikloonNPC extends ClickableNPC {

    public MinikloonNPC(World world) {
        super(world);
        super.setDelay(20L);
        super.setSkin(
                "ewogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJpZCIgOiAiMGZlMDJkYzNlYjMyNDFmZmE3MTcxMDJiMzBlZGE0ZWUiLAogICAgICAidHlwZSIgOiAiU0tJTiIsCiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYzJiZjk1ZDIyZTE1MmQ4YTNhMDQ3NjQ0NGM0MTE0MDQ0OTAzYjkzN2E2NzlhMDBiMTJiMmYxNzRhMTA3NTQ5OSIsCiAgICAgICJwcm9maWxlSWQiIDogIjIwOTM0ZWY5NDg4YzQ2NTE4MGE3OGY4NjE1ODZiNGNmIiwKICAgICAgInRleHR1cmVJZCIgOiAiYzJiZjk1ZDIyZTE1MmQ4YTNhMDQ3NjQ0NGM0MTE0MDQ0OTAzYjkzN2E2NzlhMDBiMTJiMmYxNzRhMTA3NTQ5OSIKICAgIH0KICB9LAogICJjYXBlIiA6IG51bGwsCiAgInNraW4iIDogewogICAgImlkIiA6ICIwZmUwMmRjM2ViMzI0MWZmYTcxNzEwMmIzMGVkYTRlZSIsCiAgICAidHlwZSIgOiAiU0tJTiIsCiAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlL2MyYmY5NWQyMmUxNTJkOGEzYTA0NzY0NDRjNDExNDA0NDkwM2I5MzdhNjc5YTAwYjEyYjJmMTc0YTEwNzU0OTkiLAogICAgInByb2ZpbGVJZCIgOiAiMjA5MzRlZjk0ODhjNDY1MTgwYTc4Zjg2MTU4NmI0Y2YiLAogICAgInRleHR1cmVJZCIgOiAiYzJiZjk1ZDIyZTE1MmQ4YTNhMDQ3NjQ0NGM0MTE0MDQ0OTAzYjkzN2E2NzlhMDBiMTJiMmYxNzRhMTA3NTQ5OSIKICB9Cn0=",
                "KSh/P54S5HSEwvWp6F0UiMzzdRJFZKjoNSG/Y/OoUAPJJyqnivJSJ0xpxbN5rLtYiG0kLGhm/18DFFBNNx0BR0TeEYSaPmeneGpFs7ty3PAdtkKu3ZMELruixxfsRY/MeEC4aWaqM0SfWlS/0rCHDapdgIRBTBzpDaoFa00bJSCEf9v28cbsELjjrZtj1nawEeXt9QAq6brPiJukXEJOUpR+kpgYnlIMXnckk4OIqFN7+Vh125KvAc+QKHupWc7Q5cziO1Njk31YTOsrtjDPPIQZntKpt9jsBXcjlk/P14T18kFS64Ow1sCiG4PrK3nlvkpe2qsCJIGFvVLbEfz79uwhN+0Tpfqsi1zfQ9AURKGjlBxejOP1uXe3IqGUWrpELOV8LRHWCBxtd7OLXJVhP+xxeJX/lrhPpSvaKxSdHSeV6t7BB4nLv+skyz4YzAiMpCzGApFL1p7SycTe39k71XQsRcfyIKYMpSWt1D2089HoiPCTUyXGf6leQNpG76ha9ULNGEFAgEGmvJwhN1k1hH5exLTXd23xAno99PSM26Q+jkxL3MQNN+sz/TelUrmzz/492WLNayu8u5DpbPn/tPz3E9J9ztqJWwHNCi5jg6HNiDhln5cbSoJx03xIGQirIHaan+04v5vJHs6CjQJsGN7PCdQaDTRi/or7nM/cT8Q="
        );
        super.setHologramName(Arrays.asList(
                "§e§lCLICK TO PLAY!",
                "§bTEAM FIGHT"
        ));
    }

    @Override
    public void onClick(CorePlayer corePlayer, PacketPlayInUseEntity.EnumEntityUseAction action) {
        Player player = corePlayer.getPlayer();
        player.sendMessage("§e[NPC] Minikloon: §rHello! §e" + player.getName());
        player.playSound(corePlayer.getPlayer().getLocation(), Sound.VILLAGER_IDLE, 3, 1);
    }
}
