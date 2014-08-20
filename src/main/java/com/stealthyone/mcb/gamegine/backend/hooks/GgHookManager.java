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
package com.stealthyone.mcb.gamegine.backend.hooks;

import com.stealthyone.mcb.gamegine.GameginePlugin;
import com.stealthyone.mcb.gamegine.api.hooks.Hook;
import com.stealthyone.mcb.gamegine.api.hooks.HookManager;
import com.stealthyone.mcb.gamegine.api.logging.GamegineLogger;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@RequiredArgsConstructor
public class GgHookManager implements HookManager {

    private final GameginePlugin plugin;

    /* All loaded hooks. */
    private Map<Class<? extends Hook>, Hook> hooks = new HashMap<>();

    /* Index of hook names to their respective classes. */
    private Map<String, Class<? extends Hook>> hookNameIndex = new HashMap<>();

    /* Set of hooks that failed to load properly. */
    private Set<Class<? extends Hook>> disabledHooks = new HashSet<>();

    @Override
    public <T extends Hook> T getHook(@NonNull Class<T> hookClass) {
        return (T) hooks.get(hookClass);
    }

    @Override
    public boolean isEnabled(@NonNull Hook hook) {
        return isEnabled(hook.getClass());
    }

    @Override
    public boolean isEnabled(@NonNull Class<? extends Hook> hookClass) {
        return hooks.containsKey(hookClass) && !disabledHooks.contains(hookClass);
    }

    @Override
    public boolean isEnabled(@NonNull String hookName) {
        Class<? extends Hook> clazz = hookNameIndex.get(hookName.toLowerCase());
        return clazz != null && isEnabled(clazz);
    }

    @Override
    public void registerHook(@NonNull Hook hook) {
        hooks.put(hook.getClass(), hook);
        hookNameIndex.put(hook.getName(), hook.getClass());
        try {
            hook.load();
            GamegineLogger.info("Successfully loaded hook '" + hook.toString() + "'");
        } catch (Exception ex) {
            GamegineLogger.warning("Failed to load hook '" + hook.toString() + "': " + ex.getMessage());
            disabledHooks.add(hook.getClass());
        }
    }

}