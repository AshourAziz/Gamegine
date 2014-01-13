package com.stealthyone.mcb.gamegine.permissions;

import org.bukkit.command.CommandSender;

public enum PermissionNode {

    POINTS_CHECK,
    POINTS_CHECK_OTHER;

    private String permission;

    private PermissionNode() {
        permission = "gamegine." + toString().toLowerCase().replace("_", ".");
    }

    public boolean isAllowed(CommandSender sender) {
        return sender.hasPermission(permission);
    }

}