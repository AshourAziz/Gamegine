package com.stealthyone.mcb.gamegine.players;

import com.stealthyone.mcb.gamegine.GameginePlugin;
import com.stealthyone.mcb.gamegine.api.games.Game;
import com.stealthyone.mcb.gamegine.api.players.GamePlayer;
import com.stealthyone.mcb.gamegine.api.players.PlayerManager;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class GgPlayerManager implements PlayerManager {

    private GameginePlugin plugin;

    private Map<Player, String> playerGames = new HashMap<>();

    public GgPlayerManager(GameginePlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean setPlayerGame(Player player, Game game) {
        if (game != null && !plugin.getGameManager().isGameRegistered(game)) {
            throw new IllegalArgumentException("Cannot set player's current game to '" + game.getClass().getCanonicalName() + "' - game isn't registered!");
        }

        if (isPlayerInGame(player)) {
            return false;
        } else {
            playerGames.put(player, game.getClass().getCanonicalName());
            return true;
        }
    }

    @Override
    public boolean isPlayerInGame(Player player) {
        return playerGames.get(player) != null;
    }

    @Override
    public GamePlayer castBukkitPlayer(Player player) {
        return null;
    }

}