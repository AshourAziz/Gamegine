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
package com.stealthyone.mcb.gamegine.backend.signs;

import com.stealthyone.mcb.gamegine.GameginePlugin;
import com.stealthyone.mcb.gamegine.api.signs.ActiveGSign;
import com.stealthyone.mcb.gamegine.api.signs.variables.SignVariable;
import com.stealthyone.mcb.gamegine.lib.games.instances.GameInstance;
import com.stealthyone.mcb.gamegine.utils.BlockLocation;
import de.blablubbabc.insigns.SignSendEvent;
import lombok.RequiredArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class InSignsListener implements Listener {

    private final GameginePlugin plugin;

    @EventHandler
    public void onSignSend(SignSendEvent e) {
        BlockLocation loc = new BlockLocation(e.getLocation());
        ActiveGSign sign = plugin.getSignManager().activeSigns.get(loc);
        if (sign != null) {
            List<String> newLines = getLines(sign, e.getPlayer());

            for (int i = 0; i < 4; i++) {
                String line = newLines.get(i);
                if (line == null) continue;
                e.setLine(i + 1, ChatColor.translateAlternateColorCodes('&', newLines.get(i)));
            }
        }
    }

    private List<String> getLines(ActiveGSign sign, Player p) {
        List<String> newLines;

        GameInstance gameInstance;
        try {
            gameInstance = sign.getGame();
            newLines = new ArrayList<>(sign.getType().getLines(sign));
        } catch (IllegalStateException ex) {
            // Game not loaded.
            newLines = new ArrayList<>();

            String[] gameClassSplit = sign.getGameInstanceRef().split(":")[0].split("\\.");
            String gameName = gameClassSplit[gameClassSplit.length - 1];

            for (int i = 0; i < 4; i++) {
                newLines.set(i, ChatColor.translateAlternateColorCodes('&', plugin.getSignManager().gameNotFoundFormat.get(i).replace("{GAME}", gameName)));
            }
            return newLines;
        }

        if (gameInstance != null && sign.getType().useSignVariables()) {
            for (SignVariable var : plugin.getSignManager().registeredVariables.values()) {
                String varName = var.getClass().getCanonicalName();
                String replacement = var.getReplacement(gameInstance);

                for (int i = 0; i < 4; i++) {
                    String string = newLines.get(i);
                    if (string != null) {
                        newLines.set(i, string.replace(varName, replacement));
                    }
                }
            }
        }

        return newLines;
    }

}