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
package com.stealthyone.mcb.gamegine.commands;

import com.stealthyone.mcb.gamegine.GameginePlugin;
import com.stealthyone.mcb.gamegine.api.selections.SelectionHandler;
import lombok.RequiredArgsConstructor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RequiredArgsConstructor
public class CmdSelectionCompleter implements TabCompleter {

    private final GameginePlugin plugin;

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (args.length > 1) {
            switch (args[0].toLowerCase()) {
                case "type":
                    return cmdType();
            }
        } else {
            return Arrays.asList("create", "info", "type");
        }
        return null;
    }

    private List<String> cmdType() {
        List<String> newList = new ArrayList<>();
        for (SelectionHandler handler : plugin.getSelectionManager().getSelectionHandlers()) {
            newList.add(handler.getName());
        }
        return newList;
    }

}