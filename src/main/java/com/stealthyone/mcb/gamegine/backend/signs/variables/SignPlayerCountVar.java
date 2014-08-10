package com.stealthyone.mcb.gamegine.backend.signs.variables;

import com.stealthyone.mcb.gamegine.api.signs.variables.SignVariable;
import com.stealthyone.mcb.gamegine.lib.games.instances.GameInstance;

/**
 * Can return a few different things:<br />
 * <ul>
 *     <li>{@link com.stealthyone.mcb.gamegine.api.games.Game} doesn't implement {@link com.stealthyone.mcb.gamegine.api.games.modules.GameJoinModule}: <code>X</code></li>
 *     <li>{@link com.stealthyone.mcb.gamegine.api.games.Game} implements {@link com.stealthyone.mcb.gamegine.api.games.modules.GameJoinModule}: <code>X/X</code></li>
 *     <li>{@link com.stealthyone.mcb.gamegine.api.games.Game} implements {@link com.stealthyone.mcb.gamegine.api.games.modules.GameJoinModule} but returns Integer.MAX_VALUE: X</li>
 * </ul>
 */
public class SignPlayerCountVar extends SignVariable {

    public SignPlayerCountVar() {
        super("{PLAYERCOUNT}");
    }

    @Override
    public String getReplacement(GameInstance game) {
        return null;
    }

}