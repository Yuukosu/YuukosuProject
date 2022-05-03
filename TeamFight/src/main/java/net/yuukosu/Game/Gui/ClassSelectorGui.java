package net.yuukosu.Game.Gui;

import lombok.Getter;
import net.yuukosu.Game.Classes.EnumClass;
import net.yuukosu.Game.GameManager;
import net.yuukosu.Game.GamePhase;
import net.yuukosu.Game.GamePlayer;
import net.yuukosu.System.GuiCreator.GuiButton;
import net.yuukosu.System.GuiCreator.YuukosuGui;
import net.yuukosu.System.ItemCreator;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemStack;

public class ClassSelectorGui extends YuukosuGui {

    @Getter
    private final GameManager gameManager;
    @Getter
    private final GamePlayer gamePlayer;

    public ClassSelectorGui(GameManager gameManager, GamePlayer gamePlayer) {
        super(27, "§8クラス選択");
        this.gameManager = gameManager;
        this.gamePlayer = gamePlayer;
    }

    @Override
    public void open(InventoryOpenEvent e) {
        this.update();
    }

    @Override
    public void update() {
        this.clearInventory();
        this.clearButtons();

        for (int i = 0; i < EnumClass.values().length; i++) {
            EnumClass enumClass = EnumClass.values()[i];
            ItemStack icon = enumClass.getItemClass().getIcon().clone();

            if (enumClass == this.gamePlayer.getEnumClass()) {
                icon = new ItemCreator(icon)
                        .addLore(new String[]{
                        "",
                        "§a選択中！"})
                        .addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1, true)
                        .create();
            }

            this.setItem(icon, i);
            this.createButton(i, new ClassSelectButton(this.gamePlayer, enumClass));
        }
    }

    @Override
    public void close(InventoryCloseEvent e) {
    }

    private class ClassSelectButton extends GuiButton {

        @Getter
        private final GamePlayer gamePlayer;
        @Getter
        private final EnumClass enumClass;

        public ClassSelectButton(GamePlayer gamePlayer, EnumClass enumClass) {
            this.gamePlayer = gamePlayer;
            this.enumClass = enumClass;
        }

        @Override
        public void click(InventoryClickEvent e) {
            if (this.gamePlayer.getEnumClass() != this.enumClass) {
                this.gamePlayer.setEnumClass(this.enumClass);
                this.gamePlayer.getPlayer().sendMessage("§e" + this.enumClass.getName() + " §aクラスを選択しました！");

                if (ClassSelectorGui.this.gameManager.getGamePhase() == GamePhase.STARTED) {
                    this.gamePlayer.getPlayer().sendMessage("§e次のリスポーン時に適用されます。");
                }

                this.gamePlayer.getPlayer().playSound(this.gamePlayer.getPlayer().getLocation(), "note.pling", 3F, 2F);
                ClassSelectorGui.this.update();
                return;
            }

            this.gamePlayer.getPlayer().sendMessage("§cこのクラスはすでに選択しています。");
            this.gamePlayer.getPlayer().playSound(this.gamePlayer.getPlayer().getLocation(), "mob.endermen.portal", 3F, 0F);
        }
    }
}
