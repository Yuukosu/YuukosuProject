package net.yuukosu.Game.CustomItem;

import lombok.Getter;
import net.yuukosu.Game.GameManager;
import net.yuukosu.Game.GamePlayer;
import net.yuukosu.Game.Gui.ClassSelectorGui;
import net.yuukosu.System.CorePlayer;
import net.yuukosu.System.CustomItem.CustomItem;
import net.yuukosu.System.CustomItem.ItemClickAction;
import net.yuukosu.System.ItemCreator;
import net.yuukosu.YuukosuCore;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class ClassSelectorItem extends CustomItem {

    public ClassSelectorItem(GameManager gameManager) {
        super("CLASS_SELECTOR", new ItemCreator(Material.NETHER_STAR)
                .setDisplayName("§aクラス選択 §7(右クリック)")
                .setLore(new String[]{
                        "§7クラス選択メニューを開きます。"
                }).create());
        super.setItemAction(new OpenClassSelector(gameManager));
    }

    private static class OpenClassSelector extends ItemClickAction {

        @Getter
        private final GameManager gameManager;

        public OpenClassSelector(GameManager gameManager) {
            this.gameManager = gameManager;
            super.setDelay(10);
        }

        @Override
        public void onClick(PlayerInteractEvent e) {
            e.setCancelled(true);
            Player player = e.getPlayer();

            if (YuukosuCore.getCoreManager().contains(player)) {
                if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
                    CorePlayer corePlayer = YuukosuCore.getCoreManager().getCorePlayer(player);
                    GamePlayer gamePlayer = this.gameManager.getGamePlayer(player);

                    if (gamePlayer != null) {
                        corePlayer.openGui(new ClassSelectorGui(this.gameManager, gamePlayer));
                    }
                }
            }
        }
    }
}
