package net.yuukosu.Game.LuckyChest;

import lombok.Getter;
import net.yuukosu.Arena.ArenaManager;
import net.yuukosu.Game.GameManager;
import net.yuukosu.System.ItemCreator;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class LuckyChestManager {

    @Getter
    private final ArenaManager arenaManager;
    @Getter
    private final GameManager gameManager;
    @Getter
    private final List<LuckyChest> luckyChests = new ArrayList<>();
    @Getter
    private final List<LuckyChest> aliveChests = new ArrayList<>();
    @Getter
    private final List<LuckyChestItem> luckyChestItems = new ArrayList<>();

    public LuckyChestManager(ArenaManager arenaManager, GameManager gameManager) {
        this.arenaManager = arenaManager;
        this.gameManager = gameManager;
    }

    public void registerLuckyChestItem(LuckyChestItem luckyChestItem) {
        this.luckyChestItems.add(luckyChestItem);
    }

    public void init() {
        this.arenaManager.getChestLocations().forEach(location -> this.luckyChests.add(new LuckyChest(this, this.gameManager, location)));
        this.luckyChests.forEach(LuckyChest::destroy);

        this.registerLuckyChestItem(new LuckyChestItem("§6金の林檎", new ItemCreator(Material.GOLDEN_APPLE).setAmount(1).create(), new ItemCreator(Material.GOLDEN_APPLE).setAmount(1).create()));
        this.registerLuckyChestItem(new LuckyChestItem("§6金の林檎", new ItemCreator(Material.GOLDEN_APPLE).setAmount(2).create(), new ItemCreator(Material.GOLDEN_APPLE).setAmount(2).create()));
        this.registerLuckyChestItem(new LuckyChestItem("§6金の林檎", new ItemCreator(Material.GOLDEN_APPLE).setAmount(3).create(), new ItemCreator(Material.GOLDEN_APPLE).setAmount(3).create()));
    }

    public void spawnAllChests() {
        this.aliveChests.addAll(this.luckyChests);
        this.luckyChests.forEach(LuckyChest::spawn);
    }

    public void destroyAllChests() {
        this.aliveChests.clear();
        this.luckyChests.forEach(LuckyChest::destroy);
    }

    public void spawnRandomChests(int count) {
        for (int i = 0; i <= count; i++) {
            if (this.aliveChests.size() >= this.luckyChests.size()) {
                return;
            }

            LuckyChest luckyChest = this.luckyChests.get(new Random().nextInt(this.luckyChests.size()));
            luckyChest.spawn();
            this.aliveChests.add(luckyChest);
        }
    }

    public void spawnRandomChests() {
        this.destroyAllChests();

        if (this.luckyChests.size() < 2) {
            this.spawnRandomChests(1);
        } else {
            this.spawnRandomChests(this.luckyChests.size() / 2);
        }
    }
}
