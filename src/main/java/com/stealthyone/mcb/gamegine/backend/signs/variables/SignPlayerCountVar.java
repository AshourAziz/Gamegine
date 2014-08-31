/*
 * Gamegine - Game compatibility API and creation library for Bukkit
 * Copyright (C) 2013-2014 Stealth2800 <stealth2800@stealthyone.com>
 * Website: <http://stealthyone.com/>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.stealthyone.mcb.gamegine.backend.signs.variables;

import com.stealthyone.mcb.gamegine.api.signs.handler.GSignProvider;
import com.stealthyone.mcb.gamegine.api.signs.variables.SignVariable;
import lombok.NonNull;

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
        super(DefaultVariable.PLAYER_COUNT_FANCY);
    }

    @Override
    public String getReplacement(@NonNull GSignProvider provider) {
        String players = provider.getValue(DefaultVariable.PLAYER_COUNT);
        String maxPlayers = provider.getValue(DefaultVariable.PLAYER_COUNT_MAX);

        return maxPlayers != null ? (players + "/" + maxPlayers) : players;
    }

}