package net.yuukosu;

import com.mongodb.client.MongoCollection;
import lombok.Getter;
import net.yuukosu.Commands.FlyCommand;
import net.yuukosu.Commands.SetSpawnCommand;
import net.yuukosu.Commands.StuckCommand;
import net.yuukosu.System.HubManager;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class YuukosuHub extends JavaPlugin {

    @Getter
    private static YuukosuHub instance;
    @Getter
    private static HubManager hubManager;

    @Override
    public void onEnable() {
        if (!check()) {
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        this.init();
    }

    private boolean check() {
        return Bukkit.getPluginManager().isPluginEnabled("YuukosuCore");
    }

    private void init() {
        YuukosuHub.instance = this;
        YuukosuHub.hubManager = new HubManager();
        YuukosuHub.hubManager.load();

        Bukkit.getPluginManager().registerEvents(new HubEvent(), this);
        YuukosuCore.registerCommands(
                new SetSpawnCommand(),
                new StuckCommand(),
                new FlyCommand()
        );
    }

    public static MongoCollection<Document> getHubDataCollection() {
        return YuukosuCore.getGameDatabase().getCollection("HUB.DATA");
    }
}
