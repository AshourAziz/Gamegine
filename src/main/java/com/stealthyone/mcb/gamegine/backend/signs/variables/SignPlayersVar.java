package com.stealthyone.mcb.gamegine.backend.signs.variables;

import com.stealthyone.mcb.gamegine.api.Gamegine;
import com.stealthyone.mcb.gamegine.api.signs.variables.SignVariable;
import com.stealthyone.mcb.gamegine.lib.games.instances.GameInstance;

/**
 * Gets the current player count of a game.
 */
public class SignPlayersVar extends SignVariable {

    public SignPlayersVar() {
        super("{PLAYERS}");
    }

    @Override
    public String getReplacement(GameInstance game) {
        return Integer.toString(Gamegine.getInstance().getPlayerManager().getGamePlayers(game).size());
    }

}