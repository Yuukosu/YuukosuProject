package net.yuukosu;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import lombok.Getter;
import net.yuukosu.Commands.*;
import net.yuukosu.System.CoreManager;
import net.yuukosu.System.CoreDatabase;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.craftbukkit.v1_8_R3.CraftServer;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;

public class YuukosuCore extends JavaPlugin {

    @Getter
    private static YuukosuCore instance;
    @Getter
    private static CoreDatabase coreDatabase;
    @Getter
    private static CoreManager coreManager;

    @Override
    public void onEnable() {
        YuukosuCore.instance = this;

        this.saveDefaultConfig();

        FileConfiguration config = this.getConfig();
        String host = config.getString("DATABASE.HOST", "0.0.0.0");
        int port = config.getInt("DATABASE.PORT", 27017);
        String database = config.getString("DATABASE.DATABASE", "database");
        String user = config.getString("DATABASE.USER", "user");
        String password = config.getString("DATABASE.PASSWORD", "password");

        YuukosuCore.coreDatabase = new CoreDatabase(host, port, database, user, password.toCharArray());
        YuukosuCore.coreManager = new CoreManager();
        YuukosuCore.coreManager.init();

        this.init();
    }

    @Override
    public void onDisable() {
        Bukkit.getWorlds().forEach(world -> world.getEntities().forEach(Entity::remove));
        Bukkit.getOnlinePlayers().forEach(player -> player.kickPlayer("§cStopped Core."));
        Bukkit.getMessenger().unregisterOutgoingPluginChannel(this, "BungeeCord");
    }

    private void init() {
        Bukkit.getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
        Bukkit.getPluginManager().registerEvents(new EventListener(), this);

        YuukosuCore.registerCommands(
                new LobbyCommand(),
                new RankCommand(),
                new InviteCodeCommand(),
                new LaunchCommand(),
                new KaboomCommand(),
                new StatusCommand(),
                new DamageCommand(),
                new ExampleCommand(),
                new StrikeCommand(),
                new DeauthCommand(),
                new MinikloonCommand()
        );
    }

    public static void registerCommands(Command... commands) {
        ((CraftServer) Bukkit.getServer()).getCommandMap().registerAll("YuukosuCommand", Arrays.asList(commands));
    }

    public static MongoDatabase getGameDatabase() {
        return YuukosuCore.getCoreDatabase().getDatabase("game");
    }

    public static MongoCollection<Document> getGameCollection(String gameName) {
        return YuukosuCore.getGameDatabase().getCollection("GAMES." + gameName);
    }

    public static MongoCollection<Document> getCoreDataCollection() {
        return YuukosuCore.getGameDatabase().getCollection("CORE.DATA");
    }

    public static MongoCollection<Document> getPlayersCollection() {
        return YuukosuCore.getGameDatabase().getCollection("CORE.PLAYERS");
    }
}
