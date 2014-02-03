package com.stealthyone.mcb.gameginecore.permissions;

import com.stealthyone.mcb.gameginecore.messages.ErrorMessage;
import org.bukkit.command.CommandSender;

public enum PermissionNode {

    COOLDOWNS_LIST,
    COOLDOWNS_LIST_OTHERS,
    POINTS_CHECK,
    POINTS_CHECK_OTHERS,
    SELECTIONS_CREATE;

    private String permission;

    private PermissionNode() {
        permission = "gameginecore." + toString().toLowerCase().replace("_", ".");
    }

    public boolean isAllowed(CommandSender sender) {
        return sender.hasPermission(permission);
    }

    public boolean isAllowed(CommandSender sender, boolean alert) {
        boolean result = isAllowed(sender);
        if (!result && alert) {
            ErrorMessage.NO_PERMISSION.sendTo(sender);
        }
        return result;
    }

}