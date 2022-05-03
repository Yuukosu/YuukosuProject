package net.yuukosu.System.GuiCreator;

import net.yuukosu.System.ItemCreator;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemFlag;

public class ExampleGui extends YuukosuGui {

    public ExampleGui() {
        super(54, "Example");
        super.setAutoUpdate(true);
    }

    @Override
    public void open(InventoryOpenEvent e) {
        this.update();
    }

    @Override
    public void update() {
        this.setItem(
                new ItemCreator(Material.GOLDEN_APPLE)
                        .setDisplayName("§dボタン")
                        .setLore(new String[]{
                                "§7Lore1",
                                "§7Lore2",
                                "§7Lore3"
                        })
                        .addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1, true)
                        .addItemFlags(ItemFlag.values())
                        .create()
                , 22
        );
        this.createButton(22, new ExampleButton());
    }

    @Override
    public void close(InventoryCloseEvent e) {
    }

    private static final class ExampleButton extends GuiButton {

        private int phase = 0;

        @Override
        public void click(InventoryClickEvent e) {
            Player player = (Player) e.getWhoClicked();

            switch (this.phase) {
                case 0:
                    player.sendMessage("§aポン！");
                    player.playSound(player.getLocation(), "fireworks.blast", 3, 1);
                    this.phase++;
                    break;
                case 1:
                case 2:
                    player.sendMessage("§cクラッシュ！");
                    player.playSound(player.getLocation(), "mob.zombie.wood", 3, 1);
                    this.phase++;
                    break;
                case 3:
                case 4:
                    player.sendMessage("§eパッ！");
                    player.playSound(player.getLocation(), "mob.chicken.plop", 3, 1);
                    this.phase++;
                    break;
                case 5:
                    player.sendMessage("§eパァ！");
                    player.playSound(player.getLocation(), "mob.chicken.plop", 3, 1);
                    this.phase++;
                    break;
                case 6:
                    player.sendMessage("§dグルメスパイザー！");
                    player.playSound(player.getLocation(), "random.explode", 3, 1);
                    this.phase = 0;
                    break;
            }
        }
    }
}
