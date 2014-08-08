package com.stealthyone.mcb.gamegine.backend.games;

import com.stealthyone.mcb.gamegine.GameginePlugin;
import com.stealthyone.mcb.gamegine.api.games.Game;
import com.stealthyone.mcb.gamegine.api.games.GameManager;
import com.stealthyone.mcb.gamegine.api.logging.GamegineLogger;
import com.stealthyone.mcb.gamegine.lib.games.InstanceGame;
import com.stealthyone.mcb.gamegine.lib.games.instances.GameInstance;
import com.stealthyone.mcb.gamegine.lib.games.instances.InvalidGameInstanceException;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
public class GgGameManager implements GameManager {

    private final GameginePlugin plugin;

    private final Map<String, Game> registeredGames = new HashMap<>(); // Game class name, Game object
    private final Map<String, String> gameNameToClassIndex = new HashMap<>(); // Game name, Game class name

    @Override
    public boolean registerGame(@NonNull Game game) {
        String uniqueName = game.getClass().getCanonicalName();
        if (registeredGames.containsKey(uniqueName) || gameNameToClassIndex.containsKey(game.getName().toLowerCase())) {
            GamegineLogger.warning("Unable to register game '" + game.getName() + "' (" + uniqueName + ") - a game with the same name was already registered!");
            return false;
        } else {
            registeredGames.put(uniqueName, game);
            gameNameToClassIndex.put(game.getName().toLowerCase(), uniqueName);
            GamegineLogger.info("Successfully registered game '" + game.getName() + "' v" + game.getVersion() + " by " + game.getAuthors().toString().replace("[", "").replace("]", "").replace(",", ""));
            return true;
        }
    }

    @Override
    public Collection<Game> getRegisteredGames() {
        return Collections.unmodifiableCollection(registeredGames.values());
    }

    @Override
    public Game getGameByClass(@NonNull Class<? extends Game> gameClass) {
        return registeredGames.get(gameClass.getCanonicalName());
    }

    @Override
    public Game getGameByClassName(@NonNull String className) {
        return registeredGames.get(className);
    }

    @Override
    public Game getGameByName(@NonNull String name) {
        String className = gameNameToClassIndex.get(name.toLowerCase());
        return className == null ? null : registeredGames.get(className);
    }

    @Override
    public boolean isGameRegistered(@NonNull Game game) {
        return registeredGames.get(game.getClass().getCanonicalName()) != null;
    }

    /**
     * Runs a check to see if a GameInstance is valid.
     *
     * @param gameInstance GameInstance to check.
     * @throws com.stealthyone.mcb.gamegine.lib.games.instances.InvalidGameInstanceException Thrown if the GameInstance is invalid.
     */
    public static void validateGameInstance(@NonNull GameInstance gameInstance) {
        // GameInstances can only exist for InstanceGames.
        if (!(gameInstance.getOwner() instanceof InstanceGame)) {
            throw new InvalidGameInstanceException(gameInstance, "The Game that this GameInstance belongs to does not implement InstanceGame.");
        }
    }

}