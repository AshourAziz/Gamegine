package com.stealthyone.mcb.gamegine.backend.signs.variables;

import com.stealthyone.mcb.gamegine.api.games.Game;
import com.stealthyone.mcb.gamegine.api.signs.variables.SignVariable;

/**
 * Gets the name of a game.
 */
public class SignGameNameVar extends SignVariable {

    public SignGameNameVar() {
        super("{GAME}");
    }

    @Override
    public String getReplacement(Game game) {
        return game.getName();
    }

}