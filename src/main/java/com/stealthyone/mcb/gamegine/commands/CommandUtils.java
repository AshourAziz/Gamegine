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
import com.stealthyone.mcb.gamegine.api.games.Game;
import com.stealthyone.mcb.gamegine.api.selections.SelectionHandler;
import com.stealthyone.mcb.gamegine.permissions.PermissionNode;
import com.stealthyone.mcb.stbukkitlib.messages.Message;
import com.stealthyone.mcb.stbukkitlib.utils.QuickMap;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public final class CommandUtils {

    private CommandUtils() { }

    public static boolean performBasicChecks(GameginePlugin plugin, CommandSender sender, PermissionNode perm, boolean mustBePlayer) {
        if (mustBePlayer && !(sender instanceof Player)) {
            plugin.getMessageManager().getMessage("errors.must_be_player").sendTo(sender);
            return false;
        } else if (perm != null && !perm.isAllowedAlert(sender)) {
            return false;
        }
        return true;
    }

    public static boolean performArgsCheck(GameginePlugin plugin, CommandSender sender, String label, int argsLength, int reqArgsLength, Message usageMessage) {
        if (argsLength < reqArgsLength) {
            usageMessage.sendTo(sender, new QuickMap<>("{LABEL}", label).build());
            return false;
        }
        return true;
    }

    public static Game retrieveGame(GameginePlugin plugin, CommandSender sender, String name) {
        Game game = plugin.getGameManager().getGameByName(name);
        if (game == null) {
            plugin.getMessageManager().getMessage("errors.game_not_found").sendTo(sender, new QuickMap<>("{NAME}", name).build());
            return null;
        }
        return game;
    }

    public static int getPage(GameginePlugin plugin, CommandSender sender, String[] args, int index) {
        try {
            return Integer.parseInt(args[index]);
        } catch (IndexOutOfBoundsException ex) {
            return 1;
        } catch (NumberFormatException ex) {
            plugin.getMessageManager().getMessage("errors.page_must_be_int").sendTo(sender);
            return -1;
        }
    }

    public static SelectionHandler getSelectionHandler(GameginePlugin plugin, Player player) {
        SelectionHandler selectionHandler = plugin.getSelectionManager().getPlayerSelectionHandler(player);
        if (selectionHandler == null) {
            plugin.getMessageManager().getMessage("errors.selections_handler_not_set").sendTo(player);
            return null;
        }
        return selectionHandler;
    }

}