package com.stealthyone.mcb.gamegine.players;

import com.stealthyone.mcb.gamegine.api.Gamegine;
import com.stealthyone.mcb.gamegine.api.games.Game;
import com.stealthyone.mcb.gamegine.api.players.GamePlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

public class GgPlayer implements GamePlayer {

    private UUID playerUuid;

    public GgPlayer(UUID uuid) {
        this.playerUuid = uuid;
    }

    public Player getPlayer() {
        return Bukkit.getPlayer(playerUuid);
    }

    @Override
    public boolean isInGame() {
        return Gamegine.getInstance().getPlayerManager().isPlayerInGame(getPlayer());
    }

    public boolean setGame(Game game) {
        return Gamegine.getInstance().getPlayerManager().setPlayerGame(getPlayer(), game);
    }

}