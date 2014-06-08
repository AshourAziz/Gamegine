package com.stealthyone.mcb.gamegine.permissions;

import com.stealthyone.mcb.gamegine.messages.Messages.ErrorMessages;
import org.bukkit.command.CommandSender;

public enum PermissionNode {

    GAMES_LIST,
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