package net.yuukosu.Commands;

import net.yuukosu.System.InviteCodeManager;
import net.yuukosu.System.InviteCode;
import net.yuukosu.System.PlayerRank;
import net.yuukosu.YuukosuCore;
import org.bukkit.command.CommandSender;

import java.text.SimpleDateFormat;

public class InviteCodeCommand extends YuukosuCommand {

    public InviteCodeCommand() {
        super("invitecode", PlayerRank.ADMIN);
        super.setCanConsoleExecute(true);
    }

    @Override
    public void run(CommandSender sender, String label, String[] args) {
        InviteCodeManager manager = YuukosuCore.getCoreManager().getInviteCodeManager();

        if (args.length > 0) {
            if (args[0].equalsIgnoreCase("generate")) {
                InviteCode inviteCode = manager.generateCode();

                sender.sendMessage("---------------");
                sender.sendMessage("§a新しい招待コードを生成しました。");
                sender.sendMessage("コード: §a" + inviteCode.getCode());
                sender.sendMessage("---------------");
                return;
            } else if (args[0].equalsIgnoreCase("remove")) {
                if (args.length > 1) {
                    int index;

                    try {
                        index = Integer.parseInt(args[1]);
                    } catch (IllegalArgumentException e) {
                        sender.sendMessage("§c正しい引数を入力してください。");
                        return;
                    }

                    if (index < manager.getInviteCodes().size()) {
                        manager.getInviteCodes().remove(index);
                        manager.save();
                        sender.sendMessage("§a消去しました。");
                        return;
                    }

                    sender.sendMessage("§c" + index + " 番目のコードは存在しません。");
                    return;
                }

                sender.sendMessage("§cUsage: /invitecode remove (Number)");
                return;
            } else if (args[0].equalsIgnoreCase("list")) {
                if (manager.getInviteCodes().isEmpty()) {
                    sender.sendMessage("§c招待コードはまだ生成されていません。");
                    return;
                }

                for (int i = 0; i < manager.getInviteCodes().size(); i++) {
                    InviteCode inviteCode = (InviteCode) manager.getInviteCodes().toArray()[i];

                    sender.sendMessage("番号: §a" + i);
                    sender.sendMessage("   招待コード: §a" + inviteCode.getCode());
                    sender.sendMessage("   生成日: §a" + new SimpleDateFormat("yyyy/MM/dd").format(inviteCode.getTime()));
                }

                return;
            }
        }

        sender.sendMessage("§c---InviteCode---");
        sender.sendMessage("§c/invitecode generate");
        sender.sendMessage("§c/invitecode remove (Index)");
        sender.sendMessage("§c/invitecode list");
        sender.sendMessage("§c----------------");
    }
}
