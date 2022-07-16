
package net.yuukosu.Game;

import lombok.Getter;
import lombok.Setter;
import net.yuukosu.Game.Classes.EnumClass;
import net.yuukosu.System.CorePlayer;
import net.yuukosu.TeamFight;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

public class GamePlayer {

    @Getter
    private final Player player;
    @Getter
    private final CorePlayer corePlayer;
    @Getter
    private final PlayerFrontend playerFrontend;
    @Setter
    @Getter
    private EnumClass enumClass;
    @Setter
    @Getter
    private GamePlayer lastDamager;
    @Setter
    @Getter
    private EntityDamageEvent.DamageCause lastDamageCause;
    @Setter
    @Getter
    private boolean noDamage;
    @Setter
    @Getter
    private boolean invisibleTick;
    @Setter
    @Getter
    private int kills;

    public GamePlayer(CorePlayer corePlayer) {
        this.player = corePlayer.getPlayer();
        this.corePlayer = corePlayer;
        this.playerFrontend = new PlayerFrontend(this, TeamFight.getGameManager());
        this.enumClass = EnumClass.DEFAULT;

        this.player.setMaximumNoDamageTicks(0);
    }

    public void startNoDamageTick(long tick) {
        if (!this.noDamage) {
            this.noDamage = true;
            Bukkit.getScheduler().runTaskLater(TeamFight.getInstance(), () -> this.noDamage = false, tick);
        }
    }

    public void startInvisibleTick(long tick) {
        if (!this.invisibleTick) {
            this.invisibleTick = true;
            Bukkit.getScheduler().runTaskLater(TeamFight.getInstance(), () -> this.invisibleTick = false, tick);
        }
    }

    public void resetHealth() {
        this.player.setMaxHealth(20);
        this.player.setHealth(this.player.getMaxHealth());
        this.player.setFoodLevel(20);
    }

    public void clearInventory() {
        for (int i = 0; i < this.player.getInventory().getSize(); i++) {
            this.player.getInventory().clear(i);
        }

        this.player.getInventory().setArmorContents(new ItemStack[]{
                null,
                null,
                null,
                null
        });
    }

    public void removePotionEffects() {
        for (PotionEffect potionEffect : this.player.getActivePotionEffects()) {
            this.player.removePotionEffect(potionEffect.getType());
        }
    }
}
