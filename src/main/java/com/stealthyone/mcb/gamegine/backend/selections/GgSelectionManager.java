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
package com.stealthyone.mcb.gamegine.backend.selections;

import com.stealthyone.mcb.gamegine.GameginePlugin;
import com.stealthyone.mcb.gamegine.api.logging.GamegineLogger;
import com.stealthyone.mcb.gamegine.api.selections.Selection;
import com.stealthyone.mcb.gamegine.api.selections.SelectionHandler;
import com.stealthyone.mcb.gamegine.api.selections.SelectionManager;
import com.stealthyone.mcb.gamegine.api.selections.wands.SelectionWand;
import com.stealthyone.mcb.gamegine.api.selections.wands.SelectionWandBuilder;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RequiredArgsConstructor
public class GgSelectionManager implements SelectionManager {

    private final GameginePlugin plugin;

    /**
     * The global {@link com.stealthyone.mcb.gamegine.api.selections.wands.SelectionWand} instance.
     */
    private SelectionWand globalWand;

    /**
     * Stores the currently active {@link com.stealthyone.mcb.gamegine.api.selections.SelectionHandler}s for players.<br />
     * When a player uses the global selection wand, this will be used to determine what type of selection they are creating.
     */
    private Map<UUID, String> playerSelectionHandlers = new HashMap<>();

    /**
     * Stores the current {@link com.stealthyone.mcb.gamegine.api.selections.Selection}s for Players.
     */
    private Map<UUID, Selection> playerSelections = new HashMap<>();

    /**
     * Stores registered {@link com.stealthyone.mcb.gamegine.api.selections.SelectionHandler} data.
     */
    private Map<String, SelectionHandler> selectionHandlers = new HashMap<>();

    public void reload() {
        Object obj = plugin.getConfig().get("Selections.Global wand");

        if (obj == null) {
            globalWand = null;
        } else if (obj instanceof ItemStack) {
            globalWand = new SelectionWandBuilder((ItemStack) obj);
        } else if (obj instanceof String) {
            try {
                globalWand = new SelectionWandBuilder((String) obj);
            } catch (Exception ex) {
                GamegineLogger.warning("[Selections] Unable to load global wand from configuration file: " + ex.getMessage());
                globalWand = null;
            }
        } else {
            GamegineLogger.warning("[Selections] Unable to load global wand from configuration file: Defined wand is invalid.");
            globalWand = null;
        }
    }

    @Override
    public boolean registerSelectionHandler(@NonNull SelectionHandler handler) {
        String identifier = handler.getName();
        if (identifier == null || identifier.contains(" ")) {
            throw new IllegalArgumentException("SelectionHandler '" + handler.getClass().getCanonicalName() + "' has an illegal identifier: " + identifier);
        }

        identifier = identifier.toLowerCase();
        if (selectionHandlers.containsKey(identifier)) {
            return false;
        } else {
            selectionHandlers.put(identifier, handler);
            return true;
        }
    }

    @Override
    public SelectionHandler getSelectionHandler(@NonNull String name) {
        return selectionHandlers.get(name.toLowerCase());
    }

    @Override
    public SelectionWand getGlobalWand() {
        return globalWand;
    }

    @Override
    public Selection getPlayerSelection(@NonNull Player player) {
        return playerSelections.get(player.getUniqueId());
    }

    @Override
    public SelectionHandler getPlayerSelectionHandler(@NonNull Player player) {
        String name = playerSelectionHandlers.get(player.getUniqueId());
        return name == null ? null : selectionHandlers.get(name);
    }

    @Override
    public boolean setPlayerSelectionHandler(@NonNull Player player, SelectionHandler selectionHandler) {
        String current = playerSelectionHandlers.get(player.getUniqueId());
        if (selectionHandler == null && current == null) {
            return false;
        }

        String newHandler = selectionHandler == null ? null : selectionHandler.getName().toLowerCase();
        if (newHandler != null && newHandler.equals(current)) {
            return false;
        }

        if (newHandler == null) {
            playerSelectionHandlers.remove(player.getUniqueId());
        } else {
            playerSelectionHandlers.put(player.getUniqueId(), newHandler);
        }
        return true;
    }

    /**
     * Returns a collection of all selection handlers.
     *
     * @return Collection of all selection handlers.
     */
    public Collection<SelectionHandler> getSelectionHandlers() {
        return Collections.unmodifiableCollection(selectionHandlers.values());
    }

}