package com.stealthyone.mcb.gamegine.backend.signs.variables;

import com.stealthyone.mcb.gamegine.api.Gamegine;
import com.stealthyone.mcb.gamegine.api.games.Game;
import com.stealthyone.mcb.gamegine.api.signs.variables.SignVariable;

/**
 * Gets the current player count of a game.
 */
public class SignPlayerCountVar extends SignVariable {

    public SignPlayerCountVar() {
        super("{PLAYERS}");
    }

    @Override
    public String getReplacement(Game game) {
        return Integer.toString(Gamegine.getInstance().getPlayerManager().getGamePlayers(game).size());
    }

}