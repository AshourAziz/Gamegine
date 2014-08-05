package com.stealthyone.mcb.gamegine.backend.signs.variables;

import com.stealthyone.mcb.gamegine.api.games.Game;
import com.stealthyone.mcb.gamegine.api.signs.variables.SignVariable;
import com.stealthyone.mcb.gamegine.lib.games.InstanceGame;
import com.stealthyone.mcb.gamegine.lib.games.instances.GameInstance;

public class SignGameIDVar extends SignVariable {

    public SignGameIDVar() {
        super("{ID}");
    }

    @Override
    public String getReplacement(GameInstance game) {
        Game owner = game.getOwner();
        if (!(owner instanceof InstanceGame)) {
            return null;
        }

        int id = ((InstanceGame) owner).getId(game);
        return id == InstanceGame.MAIN_INSTANCE ? "MAIN" : Integer.toString(id);
    }

}