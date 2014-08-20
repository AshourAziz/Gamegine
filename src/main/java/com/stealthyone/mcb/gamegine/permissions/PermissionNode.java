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
package com.stealthyone.mcb.gamegine.permissions;

import com.stealthyone.mcb.gamegine.messages.Messages.ErrorMessages;
import org.bukkit.command.CommandSender;

public enum PermissionNode {

    GAMES_JOIN,
    GAMES_LEAVE,
    GAMES_LIST,
    SIGNS_CREATE,
    SIGNS_DELETE,
    SIGNS_INFO,
    SIGNS_LIST,
    SIGNS_TELEPORT,
    SIGNS_TYPES,
    RELOAD,
    SAVE;

    private String permission;

    private PermissionNode() {
        permission = "gamegine." + toString().toLowerCase().replace("_", ".");
    }

    public boolean isAllowed(CommandSender sender) {
        return sender.hasPermission(permission);
    }

    public boolean isAllowedAlert(CommandSender sender) {
        boolean result = isAllowed(sender);
        if (!result) {
            ErrorMessages.NO_PERMISSION.sendTo(sender);
        }
        return result;
    }

}