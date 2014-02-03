package com.stealthyone.mcb.gameginecore.api;

import com.stealthyone.mcb.gameginecore.Gamegine;
import com.stealthyone.mcb.gameginecore.backend.games.Game;
import org.apache.commons.lang.Validate;

public class GgGameAPI {

    /**
     * Retrieve a game by its unique game ID.
     * Game IDs are CaSe-SeNsItiVe.
     *
     * @param gameId ID of the desired game.
     *               Format: 'PluginName:GameName' (Ex. UltimateMinigames:Spleef).
     * @return Game object (if found).
     *         Null if no game matching ID found.
     */
    public static Game getGame(String gameId) {
        Validate.notNull(gameId, "GameID cannot be null.");
        return Gamegine.getInstance().getGameManager().getGame(gameId);
    }

    /**
     * Registers a game for Gamegine to track and handle.
     *
     * @param game Game object to register.
     * @return True if game was successfully registered.
     *         False if game is already registered.
     */
    public static boolean registerGame(Game game) {
        Validate.notNull(game, "Game cannot be null.");
        return Gamegine.getInstance().getGameManager().registerGame(game);
    }

}