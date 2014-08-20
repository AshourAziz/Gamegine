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

import com.stealthyone.mcb.gamegine.api.Gamegine;
import com.stealthyone.mcb.gamegine.api.games.Game;
import com.stealthyone.mcb.gamegine.api.games.modules.GameJoinModule;
import com.stealthyone.mcb.gamegine.api.signs.variables.SignVariable;
import com.stealthyone.mcb.gamegine.lib.games.InstanceGame;
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
        Game owner = game.getOwner();
        int players = Gamegine.getInstance().getPlayerManager().getGamePlayers(game).size();
        if (!(game instanceof GameJoinModule)) {
            return Integer.toString(players);
        } else {
            int maxPlayers = ((GameJoinModule) game.getOwner()).getMaxPlayers(Integer.toString(((InstanceGame) owner).getId(game)));
            if (maxPlayers == -1) {
                return Integer.toString(players);
            } else {
                return players + "/" + maxPlayers;
            }
        }
    }

}