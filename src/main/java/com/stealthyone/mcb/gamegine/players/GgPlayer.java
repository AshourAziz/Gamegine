package com.stealthyone.mcb.gamegine.players;

import com.stealthyone.mcb.gamegine.api.Gamegine;
import com.stealthyone.mcb.gamegine.api.games.Game;
import com.stealthyone.mcb.gamegine.api.players.GamePlayer;
import com.stealthyone.mcb.gamegine.api.players.PlayerManager.PlayerGameResponse;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

@RequiredArgsConstructor
public class GgPlayer implements GamePlayer {

    private final UUID playerUuid;

    public Player getPlayer() {
        return Bukkit.getPlayer(playerUuid);
    }

    @Override
    public UUID getUuid() {
        return playerUuid;
    }

    @Override
    public boolean isInGame() {
        return Gamegine.getInstance().getPlayerManager().isPlayerInGame(getPlayer());
    }

    @Override
    public PlayerGameResponse setGame(Game game) {
        return Gamegine.getInstance().getPlayerManager().setPlayerGame(getPlayer(), game);
    }

}