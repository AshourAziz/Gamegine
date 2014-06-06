package com.stealthyone.mcb.gamegine.commands;

import com.stealthyone.mcb.gamegine.GameginePlugin;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class CmdGamegine implements CommandExecutor {

    private GameginePlugin plugin;

    public CmdGamegine(GameginePlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length > 0) {
            switch (args[0].toLowerCase()) {
                case "reload":

                    return true;

                case "version":
                    cmdVersion(sender);
                    return true;
            }
        }
        return true;
    }

    /*
     * Version command
     */
    private void cmdVersion(CommandSender sender) {
        sender.sendMessage("" + ChatColor.GREEN + ChatColor.BOLD + "Gamegine" + ChatColor.GOLD + ChatColor.BOLD + " v" + plugin.getVersion());
        sender.sendMessage("" + ChatColor.BLUE + ChatColor.ITALIC + "Created by Stealth2800");
        sender.sendMessage("" + ChatColor.AQUA + ChatColor.UNDERLINE + "http://stealthyone.com");
    }

}