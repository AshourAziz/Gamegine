package com.stealthyone.mcb.gameginecore.backend.games;

import com.stealthyone.mcb.gameginecore.Gamegine;
import org.bukkit.ChatColor;

public enum GameState {

    DISABLED(ChatColor.DARK_RED + "DISABLED"),
    WAITING(ChatColor.GREEN + "WAITING"),
    STARTING(ChatColor.YELLOW + "STARTING"),
    IN_PROGRESS(ChatColor.RED + "IN PROGRESS"),
    ENDING(ChatColor.YELLOW + "ENDING"),
    RESETTING(ChatColor.DARK_RED + "RESETTING");

    private String message;

    private GameState(String defMessage) {
        message = ChatColor.translateAlternateColorCodes('&', Gamegine.getInstance().getMessageManager().getMessage("game", defMessage));
    }

    @Override
    public String toString() {
        return message;
    }

}