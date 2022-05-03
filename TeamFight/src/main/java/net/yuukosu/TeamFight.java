package net.yuukosu;

import com.mongodb.client.MongoCollection;
import lombok.Getter;
import net.yuukosu.Arena.ArenaManager;
import net.yuukosu.Commands.GameDebugCommand;
import net.yuukosu.Commands.StartCommand;
import net.yuukosu.Commands.TeamFightCommand;
import net.yuukosu.Commands.VictoryCommand;
import net.yuukosu.Game.CustomItem.ClassSelectorItem;
import net.yuukosu.Game.GameManager;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class TeamFight extends JavaPlugin {

    // TODO ラッキーチェストシステムの作成

    @Getter
    private static TeamFight instance;
    @Getter
    private static ArenaManager arenaManager;
    @Getter
    private static GameManager gameManager;

    @Override
    public void onEnable() {
        TeamFight.instance = this;

        this.saveDefaultConfig();
        String arenaName = this.getConfig().getString("ARENA_NAME", "Untitled");

        TeamFight.arenaManager = new ArenaManager(arenaName);
        TeamFight.arenaManager.load();

        TeamFight.gameManager = new GameManager(TeamFight.arenaManager);

        this.init();
    }

    @Override
    public void onDisable() {
        TeamFight.getGameManager().repairLobby();
    }

    private void init() {
        YuukosuCore.getCoreManager().registerCustomItem(new ClassSelectorItem(TeamFight.getGameManager()));

        Bukkit.getPluginManager().registerEvents(new GameEvent(TeamFight.gameManager), this);
        YuukosuCore.registerCommands(
                new TeamFightCommand(),
                new StartCommand(),
                new VictoryCommand(),
                new GameDebugCommand()
        );
        Bukkit.getWorlds().forEach(world -> world.setGameRuleValue("doDaylightCycle", "false"));
        Bukkit.getWorlds().forEach(world -> world.setTime(5000));
    }

    public static MongoCollection<Document> getTeamFightCollection() {
        return YuukosuCore.getGameCollection("TEAMFIGHT");
    }

    public static MongoCollection<Document> getTeamFightArenaCollection() {
        return YuukosuCore.getGameCollection("TEAMFIGHT.ARENA");
    }
}
