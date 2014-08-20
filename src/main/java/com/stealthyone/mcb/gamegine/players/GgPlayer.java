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
package com.stealthyone.mcb.gamegine.players;

import com.stealthyone.mcb.gamegine.api.Gamegine;
import com.stealthyone.mcb.gamegine.api.games.Game;
import com.stealthyone.mcb.gamegine.api.players.GamePlayer;
import com.stealthyone.mcb.gamegine.api.players.PlayerManager.PlayerGameResponse;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

@RequiredArgsConstructor
public class GgPlayer implements GamePlayer {

    private final UUID playerUuid;

    public Player getPlayer() {
        return Bukkit.getPlayer(playerUuid);
    }

    @Override
    public UUID getUuid() {
        return playerUuid;
    }

    @Override
    public boolean isInGame() {
        return Gamegine.getInstance().getPlayerManager().isPlayerInGame(getPlayer());
    }

    @Override
    public PlayerGameResponse setGame(Game game) {
        return Gamegine.getInstance().getPlayerManager().setPlayerGame(getPlayer(), game);
    }

}