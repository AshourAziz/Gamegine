package com.stealthyone.mcb.gamegine.utils;

import org.bukkit.ChatColor;

public class GamegineUtils {

    public static String colorPercentage(double percentage) {
        ChatColor color = ChatColor.STRIKETHROUGH;
        if (percentage >= 100D) {
            color = ChatColor.DARK_GREEN;
        } else if (percentage >= 70D) {
            color = ChatColor.GREEN;
        } else if (percentage >= 50D) {
            color = ChatColor.YELLOW;
        } else if (percentage >= 30D) {
            color = ChatColor.GOLD;
        } else if (percentage >= 10D) {
            color = ChatColor.RED;
        } else if (percentage >= 0D) {
            color = ChatColor.DARK_RED;
        }
        return color + Double.toString(percentage) + "%";
    }

}