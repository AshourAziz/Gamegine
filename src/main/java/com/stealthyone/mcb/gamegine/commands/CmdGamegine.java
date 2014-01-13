package com.stealthyone.mcb.gamegine.commands;

import com.stealthyone.mcb.gamegine.Gamegine;
import com.stealthyone.mcb.gamegine.messages.ErrorMessage;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class CmdGamegine implements CommandExecutor {

    private Gamegine plugin;

    public CmdGamegine(Gamegine plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length > 0) {
            switch (args[0].toLowerCase()) {
                /* Version command */
                case "version":
                    break;

                default:
                    ErrorMessage.UNKNOWN_COMMAND.sendTo(sender);
                    return true;
            }
        }
        cmdVersion(sender, command, label, args);
        return true;
    }

    /*
     * Version command
     */
    private void cmdVersion(CommandSender sender, Command command, String label, String[] args) {
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&8-===== &a&lGamegine API&8=====-"));
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', String.format("&6&lPlugin Version %s", plugin.getDescription().getVersion())));
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&eGame API for Bukkit plugins"));
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&eCreated by Stealth2800"));
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&bhttp://stealthyone.com/bukkit"));
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&8-=======================-"));
    }

}