package com.stealthyone.mcb.gamegine.players;

import com.stealthyone.mcb.gamegine.GameginePlugin;
import com.stealthyone.mcb.gamegine.api.Gamegine;
import com.stealthyone.mcb.gamegine.api.games.Game;
import com.stealthyone.mcb.gamegine.api.players.GamePlayer;
import com.stealthyone.mcb.gamegine.api.players.PlayerManager;
import com.stealthyone.mcb.gamegine.api.players.events.GgPlayerJoinGameEvent;
import com.stealthyone.mcb.gamegine.api.players.events.GgPlayerLeaveGameEvent;
import com.stealthyone.mcb.gamegine.backend.games.GgGameManager;
import com.stealthyone.mcb.gamegine.lib.games.InstanceGame;
import com.stealthyone.mcb.gamegine.lib.games.MultiInstanceGame;
import com.stealthyone.mcb.gamegine.lib.games.SingleInstanceGame;
import com.stealthyone.mcb.gamegine.lib.games.instances.GameInstance;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;

@RequiredArgsConstructor
public class GgPlayerManager implements PlayerManager {

    private final GameginePlugin plugin;

    private final Map<UUID, GgPlayer> loadedPlayers = new HashMap<>(); // Player UUID, GgPlayer instance

    private final Map<UUID, String> playerGames = new HashMap<>(); // Player UUID, game name
    private final Map<String, Set<UUID>> gamePlayers = new HashMap<>(); // Game name, set of player UUIDs

    private final Map<UUID, String> playerGameInstances = new HashMap<>(); //Player UUID, GameInstance reference
    private final Map<String, Set<UUID>> gameInstancePlayers = new HashMap<>(); // GameInstance reference, set of player UUIDs

    @Override
    public PlayerGameResponse setPlayerGame(@NonNull Player player, Class<? extends Game> gameClass) {
        if (gameClass == null) {
            return setPlayerGame(player, (Game) null);
        }

        Game game = plugin.getGameManager().getGameByClass(gameClass);
        if (game == null) {
            throw new IllegalArgumentException("Cannot set player's current game to '" + gameClass.getCanonicalName() + "' - game isn't registered!");
        }

        return setPlayerGame(player, game);
    }

    @Override
    public PlayerGameResponse setPlayerGame(@NonNull GamePlayer player, Class<? extends Game> gameClass) {
        return setPlayerGame(castGamePlayer(player), gameClass);
    }

    @Override
    public PlayerGameResponse setPlayerGame(@NonNull Player player, Game game) {
        if (game != null && !plugin.getGameManager().isGameRegistered(game)) {
            throw new IllegalArgumentException("Cannot set player's current game to '" + game.getClass().getCanonicalName() + "' - game isn't registered!");
        }

        UUID uuid = player.getUniqueId();

        if (game != null && isPlayerInGame(player)) return PlayerGameResponse.ALREADY_IN_GAME; // Player is already in a game.
        if (game == null && !isPlayerInGame(player)) return PlayerGameResponse.ALREADY_NOT_IN_GAME; // Player is already not in a game.

        if (game == null) {
            Game oldGame = plugin.getGameManager().getGameByClassName(playerGames.remove(uuid));
            Bukkit.getPluginManager().callEvent(new GgPlayerLeaveGameEvent(player, oldGame));

            playerGames.remove(uuid);
            getGamePlayersSet(oldGame).remove(uuid);
            return PlayerGameResponse.LEFT;
        } else {
            GgPlayerJoinGameEvent e = new GgPlayerJoinGameEvent(player, game);
            Bukkit.getPluginManager().callEvent(e);
            if (e.isCancelled()) {
                return PlayerGameResponse.JOIN_EVENT_CANCELLED;
            }

            playerGames.put(uuid, game.getClass().getCanonicalName());
            getGamePlayersSet(game).add(uuid);
            return PlayerGameResponse.JOINED;
        }
    }

    private Player castGamePlayer(GamePlayer player) {
        Player p = Bukkit.getPlayer(player.getUuid());
        if (p == null) {
            throw new IllegalArgumentException("Player cannot be offline.");
        }
        return p;
    }

    private Set<UUID> getGamePlayersSet(Game game) {
        String gameClass = game.getClass().getCanonicalName();
        Set<UUID> set = gamePlayers.get(gameClass);
        if (set == null) {
            set = new HashSet<>();
            gamePlayers.put(gameClass, set);
        }
        return set;
    }

    @Override
    public PlayerGameResponse setPlayerGame(@NonNull GamePlayer player, Game game) {
        return setPlayerGame(castGamePlayer(player), game);
    }

    @Override
    public PlayerGameResponse setPlayerGameInstance(@NonNull Player player, GameInstance gameInstance) {
        if (gameInstance != null) GgGameManager.validateGameInstance(gameInstance);

        PlayerGameResponse result = setPlayerGame(player, gameInstance == null ? null : gameInstance.getOwner());
        if (result == PlayerGameResponse.JOINED) {
            Game game = gameInstance.getOwner();
            String ref = game.getClass().getCanonicalName() + ":" + ((InstanceGame) game).getId(gameInstance);

            Set<UUID> uuids = gameInstancePlayers.get(ref);
            if (uuids == null) {
                uuids = new HashSet<>();
                gameInstancePlayers.put(ref, uuids);
            }
            uuids.add(player.getUniqueId());
            playerGameInstances.put(player.getUniqueId(), ref);
        } else if (result == PlayerGameResponse.LEFT) {
            String ref = playerGameInstances.remove(player.getUniqueId());
            if (ref != null) {
                Set<UUID> uuids = gameInstancePlayers.get(ref);
                if (uuids != null) {
                    uuids.remove(player.getUniqueId());
                }
            }
        }
        return result;
    }

    @Override
    public PlayerGameResponse setPlayerGameInstance(@NonNull GamePlayer player, GameInstance gameInstance) {
        return setPlayerGameInstance(castGamePlayer(player), gameInstance);
    }

    @Override
    public boolean isPlayerInGame(@NonNull Player player) {
        return playerGames.get(player.getUniqueId()) != null;
    }

    @Override
    public boolean isPlayerInGame(@NonNull GamePlayer player) {
        return getGame(player) != null;
    }

    @Override
    public Game getGame(@NonNull Player player) {
        return Gamegine.getInstance().getGameManager().getGameByClassName(playerGames.get(player.getUniqueId()));
    }

    @Override
    public Game getGame(@NonNull GamePlayer player) {
        return getGame(castGamePlayer(player));
    }

    @Override
    public GameInstance getGameInstance(@NonNull Player player) {
        String ref = playerGameInstances.get(player.getUniqueId());
        if (ref == null) return null;

        String[] split = ref.split(":");
        int id;
        try {
            id = Integer.parseInt(split[1]);
            return ((MultiInstanceGame) Gamegine.getInstance().getGameManager().getGameByClassName(split[0])).getGameInstance(id);
        } catch (Exception ex) {
            return ((SingleInstanceGame) Gamegine.getInstance().getGameManager().getGameByClassName(split[0])).getGameInstance();
        }
    }

    @Override
    public GameInstance getGameInstance(@NonNull GamePlayer player) {
        return getGameInstance(castGamePlayer(player));
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

    @Override
    public Collection<Player> getGamePlayers(@NonNull Game game) {
        Set<Player> players = new HashSet<>();
        String gameClass = game.getClass().getCanonicalName();
        if (gamePlayers.containsKey(gameClass)) {
            for (UUID uuid : gamePlayers.get(gameClass)) {
                players.add(Bukkit.getPlayer(uuid));
            }
        }
        return players;
    }

    @Override
    public Collection<Player> getGamePlayers(@NonNull GameInstance gameInstance) {
        GgGameManager.validateGameInstance(gameInstance);

        Set<Player> players = new HashSet<>();
        Game game = gameInstance.getOwner();
        String ref = game.getClass().getCanonicalName() + ":" + ((InstanceGame) game).getId(gameInstance);
        if (gameInstancePlayers.containsKey(ref)) {
            for (UUID uuid : gameInstancePlayers.get(ref)) {
                players.add(Bukkit.getPlayer(uuid));
            }
        }
        return players;
    }

    public void playerDisconnect(@NonNull Player player) {
        setPlayerGame(player, (Game) null);
        loadedPlayers.remove(player.getUniqueId());
    }

}