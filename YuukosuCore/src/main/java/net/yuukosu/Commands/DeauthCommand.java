package net.yuukosu.Commands;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import net.yuukosu.System.CorePlayer;
import net.yuukosu.System.PlayerRank;
import net.yuukosu.Utils.DatabaseUtils;
import net.yuukosu.YuukosuCore;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class DeauthCommand extends YuukosuCommand {

    public DeauthCommand() {
        super("deauth", PlayerRank.ADMIN);
        super.setCanConsoleExecute(true);
    }

    @Override
    public void run(CommandSender sender, String label, String[] args) {
        if (args.length > 0) {
            String name = args[0];
            Player player = Bukkit.getPlayerExact(args[0]);

            if (YuukosuCore.getCoreManager().contains(player)) {
                CorePlayer corePlayer = YuukosuCore.getCoreManager().getCorePlayer(player);
                corePlayer.setInviteCode(null);
                corePlayer.save();
                player.kickPlayer("§c管理者によって、招待コードを削除されました。");
                sender.sendMessage("§a" + player.getName() + " の招待コードを削除しました。");
            } else {
                MongoCollection<Document> col = YuukosuCore.getPlayersCollection();
                Document doc1 = col.find(Filters.eq("NAME", name)).first();
                Document doc2 = col.find(Filters.eq("UUID", name)).first();

                if (doc1 != null) {
                    doc1.remove("INVITE_CODE");
                    col.updateOne(Filters.eq("NAME", name), new Document("$set", doc1), DatabaseUtils.getUpdateOptions());
                } else if (doc2 != null) {
                    doc2.remove("INVITE_CODE");
                    col.updateOne(Filters.eq("NAME", name), new Document("$set", doc2), DatabaseUtils.getUpdateOptions());
                } else {
                    sender.sendMessage("§c" + name + " のデータは見つかりませんでした。");
                    return;
                }

                sender.sendMessage("§a" + name + " の招待コードを削除しました。");
            }
        }
    }
}
