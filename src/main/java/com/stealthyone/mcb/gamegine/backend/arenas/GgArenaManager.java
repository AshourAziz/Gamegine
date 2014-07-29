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