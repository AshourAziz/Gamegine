package com.stealthyone.mcb.gamegine.backend.games;

import com.stealthyone.mcb.gamegine.GameginePlugin;
import com.stealthyone.mcb.gamegine.api.games.Game;
import com.stealthyone.mcb.gamegine.api.games.GameManager;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
public class GgGameManager implements GameManager {

    private final GameginePlugin plugin;

    // Game class name, Game object
    private Map<String, Game> registeredGames = new HashMap<>();

    // Game name, Game class name
    private Map<String, String> gameNameToClassIndex = new HashMap<>();

    @Override
    public boolean registerGame(@NonNull Game game) {
        String uniqueName = game.getClass().getCanonicalName();
        if (registeredGames.containsKey(uniqueName)) {
            return false;
        } else {
            registeredGames.put(uniqueName, game);
            gameNameToClassIndex.put(game.getName().toLowerCase(), uniqueName);
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

}