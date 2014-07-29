package com.stealthyone.mcb.gamegine.commands;

import com.stealthyone.mcb.gamegine.GameginePlugin;
import com.stealthyone.mcb.gamegine.api.Gamegine;
import com.stealthyone.mcb.gamegine.api.logging.GamegineLogger;
import com.stealthyone.mcb.gamegine.messages.Messages.ErrorMessages;
import com.stealthyone.mcb.gamegine.messages.Messages.NoticeMessages;
import com.stealthyone.mcb.gamegine.messages.Messages.UsageMessages;
import com.stealthyone.mcb.gamegine.permissions.PermissionNode;
import com.stealthyone.mcb.stbukkitlib.utils.MessageUtils;
import com.stealthyone.mcb.stbukkitlib.utils.QuickMap;
import lombok.RequiredArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

@RequiredArgsConstructor
public class CmdGamegine implements CommandExecutor {

    private final GameginePlugin plugin;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length > 0) {
            switch (args[0].toLowerCase()) {
                /* Show help for 'gamegine' command */
                case "help":
                    plugin.getHelpManager().handleHelpCommand(null, sender, label, "help", args);
                    return true;

                /* Reload plugin configuration */
                case "reload":
                    cmdReload(sender);
                    return true;

                /* Save plugin configuration */
                case "save":
                    cmdSave(sender);
                    return true;

                /* Show plugin version */
                case "version":
                    cmdVersion(sender);
                    return true;

                default:
                    ErrorMessages.UNKNOWN_COMMAND.sendTo(sender);
                    break;
            }
        }
        UsageMessages.GAMEGINE_HELP.sendTo(sender, new QuickMap<>("{LABEL}", label).build());
        return true;
    }

    /*
     * Reload command
     */
    private void cmdReload(CommandSender sender) {
        if (!PermissionNode.RELOAD.isAllowedAlert(sender)) return;

        try {
            plugin.reloadConfig();
            plugin.getHelpManager().reload();
            plugin.getMessageManager().reloadMessages();
            NoticeMessages.PLUGIN_RELOADED.sendTo(sender);
        } catch (Exception ex) {
            ErrorMessages.RELOAD_ERROR.sendTo(sender, new QuickMap<>("{MESSAGE}", ex.getMessage()).build());
            GamegineLogger.severe(MessageUtils.stringArrayToString(ErrorMessages.RELOAD_ERROR.getFormattedMessages(new QuickMap<>("{MESSAGE}", ex.getMessage()).build())));
            ex.printStackTrace();
        }
    }

    /*
     * Save command
     */
    private void cmdSave(CommandSender sender) {
        if (!PermissionNode.SAVE.isAllowedAlert(sender)) return;

        try {
            plugin.saveConfig();
            NoticeMessages.PLUGIN_SAVED.sendTo(sender);
        } catch (Exception ex) {
            ErrorMessages.SAVE_ERROR.sendTo(sender, new QuickMap<>("{MESSAGE}", ex.getMessage()).build());
            GamegineLogger.severe(MessageUtils.stringArrayToString(ErrorMessages.SAVE_ERROR.getFormattedMessages(new QuickMap<>("{MESSAGE}", ex.getMessage()).build())));
            ex.printStackTrace();
        }
    }

    /*
     * Version command
     */
    private void cmdVersion(CommandSender sender) {
        sender.sendMessage("" + ChatColor.GREEN + ChatColor.BOLD + "Gamegine" + ChatColor.GREEN + " Game Library");
        sender.sendMessage("" + ChatColor.GOLD + ChatColor.ITALIC + "v" + plugin.getVersion() + " (API v" + Gamegine.getAPIVersion() + ")");
        sender.sendMessage(ChatColor.BLUE + "Created by Stealth2800");
        sender.sendMessage("" + ChatColor.AQUA + ChatColor.UNDERLINE + "http://stealthyone.com");
    }

}