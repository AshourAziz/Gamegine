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
import com.stealthyone.mcb.gamegine.api.signs.handler.MultiSignHandler;
import com.stealthyone.mcb.gamegine.api.signs.handler.SignHandler;
import lombok.RequiredArgsConstructor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RequiredArgsConstructor
public class CmdSignCompleter implements TabCompleter {

    private final GameginePlugin plugin;

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (args.length > 1) {
            switch (args[0].toLowerCase()) {
                case "list":
                    return handlerNames();

                case "use":
                    return cmdUse(args);
            }
        } else {
            return Arrays.asList("create", "delete", "handlers", "info", "list", "tp", "types", "use");
        }
        return null;
    }

    private List<String> handlerNames() {
        return new ArrayList<>(plugin.getSignManager().getSignHandlerNames());
    }

    private List<String> cmdUse(String[] args) {
        int argsLength = args.length;
        if (argsLength == 2) {
            return new ArrayList<>(plugin.getSignManager().getSignHandlerNames());
        } else if (argsLength == 3) {
            String name = args[3].split(":")[0];
            SignHandler handler = plugin.getSignManager().getSignHandlerByName(name);
            if (handler != null) {
                if (handler instanceof MultiSignHandler) {
                    return new ArrayList<>(((MultiSignHandler) handler).getProviderNames());
                }
            }
        }
        return null;
    }

}