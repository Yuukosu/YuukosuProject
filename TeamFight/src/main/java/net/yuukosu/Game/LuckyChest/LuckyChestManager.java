package net.yuukosu.Game.LuckyChest;

import lombok.Getter;
import net.yuukosu.Arena.ArenaManager;
import net.yuukosu.Game.GameManager;

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

    public LuckyChestManager(ArenaManager arenaManager, GameManager gameManager) {
        this.arenaManager = arenaManager;
        this.gameManager = gameManager;
    }

    public void init() {
        this.arenaManager.getChestLocations().forEach(location -> this.luckyChests.add(new LuckyChest(this.gameManager, location)));
    }

    public void spawnAllChests() {
        this.luckyChests.forEach(LuckyChest::spawn);
    }

    public void destroyAllChests() {
        this.luckyChests.forEach(LuckyChest::destroy);
    }

    public void spawnRandomChests(int count) {
        int size = this.luckyChests.size();
        Random random = new Random();

        for (int i = 0; i < count; i++) {
            if (this.aliveChests.size() >= this.luckyChests.size()) {
                return;
            }

            int index = random.nextInt(size);

            while (index >= this.aliveChests.size()) {
                index = random.nextInt(size);
            }

            LuckyChest luckyChest = this.luckyChests.get(index);
            luckyChest.spawn();
            this.aliveChests.add(luckyChest);
        }
    }
}
