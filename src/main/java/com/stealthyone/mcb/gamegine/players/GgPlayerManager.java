package com.stealthyone.mcb.gamegine.players;

import com.stealthyone.mcb.gamegine.GameginePlugin;
import com.stealthyone.mcb.gamegine.api.games.Game;
import com.stealthyone.mcb.gamegine.api.players.GamePlayer;
import com.stealthyone.mcb.gamegine.api.players.PlayerManager;
import com.stealthyone.mcb.gamegine.api.players.events.GgPlayerJoinGameEvent;
import com.stealthyone.mcb.gamegine.api.players.events.GgPlayerLeaveGameEvent;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RequiredArgsConstructor
public class GgPlayerManager implements PlayerManager {

    private final GameginePlugin plugin;

    private Map<UUID, GgPlayer> loadedPlayers = new HashMap<>();
    private Map<UUID, String> playerGames = new HashMap<>();

    @Override
    public PlayerGameResponse setPlayerGame(@NonNull Player player, Game game) {
        if (game != null && !plugin.getGameManager().isGameRegistered(game)) {
            throw new IllegalArgumentException("Cannot set player's current game to '" + game.getClass().getCanonicalName() + "' - game isn't registered!");
        }

        UUID uuid = player.getUniqueId();

        if (game != null && isPlayerInGame(player)) return PlayerGameResponse.ALREADY_IN_GAME; // Player is already in a game.
        if (game == null && !isPlayerInGame(player)) return PlayerGameResponse.ALREADY_NOT_IN_GAME; // Player is already not in a game.

        if (game == null) {
            Bukkit.getPluginManager().callEvent(new GgPlayerLeaveGameEvent(player, plugin.getGameManager().getGameByClassName(playerGames.remove(uuid))));
            return PlayerGameResponse.LEFT;
        } else {
            GgPlayerJoinGameEvent e = new GgPlayerJoinGameEvent(player, game);
            Bukkit.getPluginManager().callEvent(e);
            if (e.isCancelled()) {
                return PlayerGameResponse.JOIN_EVENT_CANCELLED;
            }

            playerGames.put(uuid, game.getClass().getCanonicalName());
            return PlayerGameResponse.JOINED;
        }
    }

    @Override
    public boolean isPlayerInGame(@NonNull Player player) {
        return playerGames.get(player.getUniqueId()) != null;
    }

    @Override
    public GamePlayer castBukkitPlayer(@NonNull Player player) {
        GgPlayer p = loadedPlayers.get(player.getUniqueId());
        if (p == null) {
            p = new GgPlayer(player.getUniqueId());
            loadedPlayers.put(player.getUniqueId(), p);
        }
        return p;
    }

    public void playerDisconnect(@NonNull Player player) {
        setPlayerGame(player, null);
        loadedPlayers.remove(player.getUniqueId());
    }

}