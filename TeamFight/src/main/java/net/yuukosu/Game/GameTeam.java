package net.yuukosu.Game;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

public class GameTeam {

    @Getter
    private final GameManager gameManager;
    @Getter
    private final List<GamePlayer> players = new ArrayList<>();
    @Setter
    @Getter
    private int kills;

    public GameTeam(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    public void sendMessage(String message) {
        this.players.forEach(gamePlayer -> gamePlayer.getPlayer().sendMessage(message));
    }

    public void sendTitle(String title, String subtitle, int fade, int stay, int out) {
        this.players.forEach(gamePlayer -> gamePlayer.getCorePlayer().sendTitle(title, subtitle, fade, stay, out));
    }

    public void playSound(String sound, float volume, float pitch) {
        this.players.forEach(gamePlayer -> gamePlayer.getPlayer().playSound(gamePlayer.getPlayer().getLocation(), sound, volume, pitch));
    }

    public void joinTeam(GamePlayer gamePlayer) {
        this.players.add(gamePlayer);
    }

    public void removeTeam(GamePlayer gamePlayer) {
        this.players.remove(gamePlayer);
    }

    public boolean contains(GamePlayer gamePlayer) {
        return this.players.contains(gamePlayer);
    }

    public int getSize() {
        return this.players.size();
    }
}
