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
package com.stealthyone.mcb.gamegine.backend.arenas;

import com.stealthyone.mcb.gamegine.GameginePlugin;
import com.stealthyone.mcb.gamegine.api.arenas.ArenaManager;
import com.stealthyone.mcb.gamegine.lib.arenas.Arena;
import com.stealthyone.mcb.gamegine.lib.arenas.components.RegenableArena;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang.Validate;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
public class GgArenaManager implements ArenaManager {

    private final GameginePlugin plugin;

    private Map<String, RegenableArena> registeredArenas = new HashMap<>();

    private void validateRegenableArena(RegenableArena arena) {
        Validate.notNull(arena, "Arena cannot be null.");
        if (!(arena instanceof Arena)) {
            throw new IllegalArgumentException("Cannot register regenable arena - RegenableArena implementation is not an Arena implementation.");
        }
        Validate.notNull(((Arena) arena).getOwner(), "Cannot register regenable arena - arena's owner cannot be null.");
    }

    @Override
    public boolean registerRegenableArena(RegenableArena arena) {
        validateRegenableArena(arena);

        Arena rawArena = (Arena) arena;
        String identifier = rawArena.getOwner() + "_" + rawArena.getId();
        if (registeredArenas.containsKey(identifier)) {
            return false;
        } else {
            registeredArenas.put(identifier, arena);
            return true;
        }
    }

    @Override
    public boolean unregisterRegenableArena(RegenableArena arena) {
        validateRegenableArena(arena);

        Arena rawArena = (Arena) arena;
        String identifier = rawArena.getOwner() + "_" + rawArena.getId();
        if (!registeredArenas.containsKey(identifier)) {
            return false;
        } else {
            registeredArenas.remove(identifier);
            return true;
        }
    }

}