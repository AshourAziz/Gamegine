package com.stealthyone.mcb.gamegine.games;

import com.stealthyone.mcb.gamegine.GameginePlugin;
import com.stealthyone.mcb.gamegine.api.games.Game;
import com.stealthyone.mcb.gamegine.api.games.GameManager;
import org.apache.commons.lang.Validate;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class GgGameManager implements GameManager {

    private GameginePlugin plugin;

    // Game class name, Game object
    private Map<String, Game> registeredGames = new HashMap<>();

    // Game name, Game class name
    private Map<String, String> gameToClassMapping = new HashMap<>();

    public GgGameManager(GameginePlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean registerGame(Game game) {
        Validate.notNull(game, "Game cannot be null.");

        String uniqueName = game.getClass().getCanonicalName();
        if (registeredGames.containsKey(uniqueName)) {
            return false;
        } else {
            registeredGames.put(uniqueName, game);
            gameToClassMapping.put(game.getName().toLowerCase(), uniqueName);
            return true;
        }
    }

    @Override
    public Collection<Game> getRegisteredGames() {
        return registeredGames.values();
    }

}