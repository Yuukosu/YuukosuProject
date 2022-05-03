package net.yuukosu.Commands;

import lombok.Getter;
import lombok.Setter;
import net.yuukosu.System.CorePlayer;
import net.yuukosu.System.PlayerRank;
import net.yuukosu.YuukosuCore;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

public abstract class YuukosuCommand extends Command {

    @Setter
    @Getter
    private PlayerRank requireRank;
    @Setter
    @Getter
    private boolean canConsoleExecute;
    @Setter
    @Getter
    private String cantConsoleExecuteMessage;

    public YuukosuCommand(String name, PlayerRank requireRank) {
        super(name);
        this.requireRank = requireRank;
        this.canConsoleExecute = true;
        this.cantConsoleExecuteMessage = "§cプレイヤーから実行してください。";
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            if (YuukosuCore.getCoreManager().contains(player)) {
                CorePlayer corePlayer = YuukosuCore.getCoreManager().getCorePlayer(player);

                if (corePlayer.getPlayerRank().hasPriority(this.requireRank)) {
                    this.run(sender, label, args);
                    return true;
                }

                sender.sendMessage("§cこのコマンドを実行するには、" + this.requireRank.getName() + " ランク以上が必要です。");
                return false;
            }
        }

        if (sender instanceof ConsoleCommandSender) {
            if (this.canConsoleExecute) {
                this.run(sender, label, args);
                return true;
            }

            sender.sendMessage(this.cantConsoleExecuteMessage);
        }

        return false;
    }

    public abstract void run(CommandSender sender, String label, String[] args);
}
