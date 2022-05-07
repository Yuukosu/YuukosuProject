package net.yuukosu.Commands;

import net.yuukosu.Arena.ArenaManager;
import net.yuukosu.Arena.CustomItem.ChestTool;
import net.yuukosu.Game.EnumTeam;
import net.yuukosu.System.CustomItem.CustomItem;
import net.yuukosu.System.PlayerRank;
import net.yuukosu.TeamFight;
import net.yuukosu.YuukosuCore;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;

public class TeamFightCommand extends YuukosuCommand {

    public TeamFightCommand() {
        super("teamfight", PlayerRank.ADMIN);
        super.setCanConsoleExecute(false);
        super.setAliases(Collections.singletonList("tf"));
    }

    @Override
    public void run(CommandSender sender, String label, String[] args) {
        Player player = (Player) sender;
        Location location = player.getLocation();
        ArenaManager arenaManager = TeamFight.getArenaManager();

        if (args.length > 0) {
            if (args[0].equalsIgnoreCase("setname")) {
                if (args.length > 1) {
                    String name = args[1];
                    arenaManager.setName(name);
                    arenaManager.save();
                    sender.sendMessage("§aアリーナ名を " + name + " に設定しました。");
                    return;
                }

                sender.sendMessage("§cUsage: /teamfight setname (Name)");
                return;
            }

            if (args[0].equalsIgnoreCase("setmapname")) {
                if (args.length > 1) {
                    String name = args[1];
                    arenaManager.setMapName(name);
                    arenaManager.save();
                    sender.sendMessage("§aマップ名を " + name + " に設定しました。");
                    return;
                }

                sender.sendMessage("§cUsage: /teamfight setmapname (Name)");
                return;
            }

            if (args[0].equalsIgnoreCase("setlobby")) {
                arenaManager.setLobby(location);
                arenaManager.save();
                sender.sendMessage("§aロビーを設定しました。");
                return;
            }

            if (args[0].equalsIgnoreCase("chest")) {
                CustomItem item = new ChestTool(TeamFight.getArenaManager());
                player.getInventory().addItem(item.create());
                sender.sendMessage("§aGET!");
                return;
            }

            if (args[0].equalsIgnoreCase("clearChests")) {
                arenaManager.getChestLocations().clear();
                arenaManager.save();
                player.sendMessage("§aREMOVED!");
                return;
            }

            if (args[0].equalsIgnoreCase("registerteam")) {
                if (args.length > 1) {
                    EnumTeam enumTeam = this.getEnumTeam(args[1]);

                    if (enumTeam == null) {
                        sender.sendMessage("§c正しい引数を入力してください。");
                        return;
                    }

                    if (arenaManager.isRegistered(enumTeam)) {
                        sender.sendMessage("§c" + enumTeam.getName() + " チームはすでに登録されています。");
                        return;
                    }

                    arenaManager.registerTeam(enumTeam);
                    arenaManager.save();
                    sender.sendMessage("§a" + enumTeam.getName() + " チームを登録しました。");
                    return;
                }

                sender.sendMessage("§cUsage: /teamfight register (RED / BLUE / GREEN / YELLOW)");
                return;
            }

            if (args[0].equalsIgnoreCase("unregisterteam")) {
                if (args.length > 1) {
                    EnumTeam enumTeam = this.getEnumTeam(args[1]);

                    if (enumTeam == null) {
                        sender.sendMessage("§c正しい引数を入力してください。");
                        return;
                    }

                    if (!arenaManager.isRegistered(enumTeam)) {
                        sender.sendMessage("§c" + enumTeam.getName() + " チームはすでに登録されていません。");
                        return;
                    }

                    arenaManager.unregisterTeam(enumTeam);
                    arenaManager.save();
                    sender.sendMessage("§a" + enumTeam.getName() + " の登録を解除しました。");
                    return;
                }

                sender.sendMessage("§cUsage: /teamfight unregister (RED / BLUE / GREEN / YELLOW)");
                return;
            }

            if (args[0].equalsIgnoreCase("setcenter")) {
                if (args.length > 1) {
                    EnumTeam enumTeam = this.getEnumTeam(args[1]);

                    if (enumTeam == null) {
                        sender.sendMessage("§c正しい引数を入力してください。");
                        return;
                    }

                    if (!arenaManager.isRegistered(enumTeam)) {
                        sender.sendMessage("§c" + enumTeam.getName() + " チームは登録されていません。");
                        return;
                    }

                    arenaManager.getTeamArenaData(enumTeam).setCenter(location);
                    arenaManager.save();
                    sender.sendMessage("§a" + enumTeam.getName() + " チームのセンター地点を設定しました。");
                    return;
                }

                sender.sendMessage("§cUsage: /teamfight setcenter (RED / BLUE / GREEN / YELLOW)");
                return;
            }

            if (args[0].equalsIgnoreCase("addspawn")) {
                if (args.length > 1) {
                    EnumTeam enumTeam = this.getEnumTeam(args[1]);

                    if (enumTeam == null) {
                        sender.sendMessage("§c正しい引数を入力してください。");
                        return;
                    }

                    if (!arenaManager.isRegistered(enumTeam)) {
                        sender.sendMessage("§c" + enumTeam.getName() + " チームは登録されていません。");
                        return;
                    }

                    arenaManager.getTeamArenaData(enumTeam).addSpawn(location);
                    arenaManager.save();
                    sender.sendMessage("§a" + enumTeam.getName() + " チームのスポーン地点を追加しました。");
                    return;
                }

                sender.sendMessage("§cUsage: /teamfight addspawn (RED / BLUE / GREEN / YELLOW)");
                return;
            }

            if (args[0].equalsIgnoreCase("removespawn")) {
                if (args.length > 2) {
                    EnumTeam enumTeam = this.getEnumTeam(args[1]);
                    int index;

                    try {
                        index = Integer.parseInt(args[2]);
                    } catch (IllegalArgumentException e) {
                        sender.sendMessage("§c正しい引数を入力してください。");
                        return;
                    }

                    if (enumTeam == null) {
                        sender.sendMessage("§c正しい引数を入力してください。");
                        return;
                    }

                    if (!arenaManager.isRegistered(enumTeam)) {
                        sender.sendMessage("§c" + enumTeam.getName() + " チームは登録されていません。");
                        return;
                    }

                    if (index > arenaManager.getTeamArenaData(enumTeam).getSpawns().size() - 1) {
                        sender.sendMessage("§c" + enumTeam.getName() + " チームの " + index + " 番目のスポーン地点は存在しません。");
                        return;
                    }

                    arenaManager.getTeamArenaData(enumTeam).removeSpawn(index);
                    arenaManager.save();
                    sender.sendMessage("§a" + enumTeam.getName() + " チームの " + index + " 番目のスポーン地点を消去しました。");
                    return;
                }

                sender.sendMessage("§cUsage: /teamfight removespawn (RED / BLUE / GREEN / YELLOW) (Index)");
                return;
            }

            if (args[0].equalsIgnoreCase("info")) {
                if (args.length > 1) {
                    EnumTeam enumTeam = this.getEnumTeam(args[1]);

                    if (enumTeam == null) {
                        sender.sendMessage("§c正しい引数を入力してください。");
                        return;
                    }

                    if (!arenaManager.isRegistered(enumTeam)) {
                        sender.sendMessage("§c" + enumTeam.getName() + " チームは登録されていません。");
                        return;
                    }

                    Location center = arenaManager.getTeamArenaData(enumTeam).getCenter();
                    sender.sendMessage(enumTeam.getChatColor() + enumTeam.getName() + " §aチームの情報");
                    sender.sendMessage("   センター地点: §a" + (center != null ? this.convertLocationToString(center) : "§cNone"));
                    sender.sendMessage("   スポーン地点: " + (arenaManager.getTeamArenaData(enumTeam).getSpawns().isEmpty() ? "§cNone" : ""));

                    for (int i = 0; i < arenaManager.getTeamArenaData(enumTeam).getSpawns().size() - 1; i++) {
                        sender.sendMessage("      " + i + ". §a" + this.convertLocationToString(arenaManager.getTeamArenaData(enumTeam).getSpawns().get(i)));
                    }

                    return;
                }

                sender.sendMessage("アリーナ名: §a" + arenaManager.getName());
                sender.sendMessage("マップ名: §a" + arenaManager.getMapName());
                sender.sendMessage("ロビー地点: §a" + (arenaManager.getLobby() != null ? this.convertLocationToString(arenaManager.getLobby()) : "§cNone"));

                for (EnumTeam enumTeam : arenaManager.getArenaTeamData().keySet()) {
                    Location center = arenaManager.getTeamArenaData(enumTeam).getCenter();
                    sender.sendMessage(enumTeam.getChatColor() + enumTeam.getName() + " §aチームの情報");
                    sender.sendMessage("   センター地点: §a" + (center != null ? this.convertLocationToString(center) : "§cNone"));
                    sender.sendMessage("   スポーン地点: " + (arenaManager.getTeamArenaData(enumTeam).getSpawns().isEmpty() ? "§cNone" : ""));

                    for (int i = 0; i < arenaManager.getTeamArenaData(enumTeam).getSpawns().size() - 1; i++) {
                        sender.sendMessage("      " + i + ". §a" + this.convertLocationToString(arenaManager.getTeamArenaData(enumTeam).getSpawns().get(i)));
                    }
                }
                return;
            }
        }

        sender.sendMessage("§c---TeamFight Commands---");
        sender.sendMessage("§c - /teamfight setname (Name)");
        sender.sendMessage("§c - /teamfight setmapname (Name)");
        sender.sendMessage("§c - /teamfight setlobby");
        player.sendMessage("§c - /teamfight chest");
        sender.sendMessage("§c - /teamfight registerteam (RED / BLUE / GREEN / YELLOW)");
        sender.sendMessage("§c - /teamfight unregisterteam (RED / BLUE / GREEN / YELLOW)");
        sender.sendMessage("§c - /teamfight setcenter (RED / BLUE / GREEN / YELLOW)");
        sender.sendMessage("§c - /teamfight addspawn (RED / BLUE / GREEN / YELLOW)");
        sender.sendMessage("§c - /teamfight removespawn (RED / BLUE / GREEN / YELLOW) (Index)");
        sender.sendMessage("§c - /teamfight info (RED / BLUE / GREEN / YELLOW)");
        sender.sendMessage("§c------------------------");
    }

    protected String convertLocationToString(Location location) {
        return location.getWorld().getName() + ", " + Math.ceil(location.getX()) + ", " + Math.ceil(location.getY()) + ", " + Math.ceil(location.getZ()) + ", " + Math.ceil(location.getYaw()) + ", " + Math.ceil(location.getPitch());
    }

    protected EnumTeam getEnumTeam(String name) {
        EnumTeam enumTeam;

        try {
            enumTeam = EnumTeam.valueOf(name);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return null;
        }

        return enumTeam;
    }
}
