package com.stealthyone.mcb.gamegine.backend.signs.variables;

import com.stealthyone.mcb.gamegine.api.signs.variables.SignVariable;
import com.stealthyone.mcb.gamegine.lib.games.instances.GameInstance;

/**
 * Gets the name of a game.
 */
public class SignGameNameVar extends SignVariable {

    public SignGameNameVar() {
        super("{GAME}");
    }

    @Override
    public String getReplacement(GameInstance game) {
        return game.getOwner().getName();
    }

}