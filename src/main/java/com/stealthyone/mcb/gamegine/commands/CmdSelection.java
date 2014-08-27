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
import com.stealthyone.mcb.gamegine.api.selections.SelectionHandler;
import com.stealthyone.mcb.gamegine.api.selections.SelectionManager;
import com.stealthyone.mcb.gamegine.backend.selections.GgSelectionManager;
import com.stealthyone.mcb.gamegine.api.selections.Selection;
import com.stealthyone.mcb.gamegine.permissions.PermissionNode;
import com.stealthyone.mcb.stbukkitlib.messages.MessageManager;
import com.stealthyone.mcb.stbukkitlib.utils.QuickMap;
import lombok.RequiredArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RequiredArgsConstructor
public class CmdSelection implements CommandExecutor {

    private final GameginePlugin plugin;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length > 0) {
            switch (args[0].toLowerCase()) {
                case "info":
                    break;

                case "type":
                    cmdType(sender, label, args);
                    return true;
            }
        }

        /* Info command */
        if (!CommandUtils.performBasicChecks(plugin, sender, PermissionNode.SELECTIONS_INFO, true)) return true;

        MessageManager messageManager = plugin.getMessageManager();
        Player player = (Player) sender;

        SelectionManager selectionManager = plugin.getSelectionManager();
        SelectionHandler selectionHandler = selectionManager.getPlayerSelectionHandler(player);

        List<String> messages = new ArrayList<>();
        messages.addAll(Arrays.asList(messageManager.getMessage("plugin.cmd_selections_info_header").getFormattedMessages()));
        messages.addAll(Arrays.asList(messageManager.getMessage("plugin.cmd_selections_info_element").getFormattedMessages(new QuickMap<String, String>()
            .put("{NAME}", "Selection handler")
            .put("{VALUE}", selectionHandler == null ? ("" + ChatColor.RED + ChatColor.ITALIC + "<not set>") : selectionHandler.getName())
            .build()
        )));

        if (selectionHandler != null) {
            Selection selection = selectionManager.getPlayerSelection(player);

            String value;
            if (selection == null) {
                value = "" + ChatColor.RED + ChatColor.ITALIC + "<none>";
            } else if (selection.isValid()) {
                value = ChatColor.GREEN + Integer.toString(selection.size());
            } else {
                value = "" + ChatColor.RED + ChatColor.ITALIC + "invalid";
            }

            messages.addAll(Arrays.asList(messageManager.getMessage("plugin.cmd_selections_info_element").getFormattedMessages(new QuickMap<String, String>()
                .put("{NAME}", "Selection")
                .put("{VALUE}", value)
                .build()
            )));
        }

        sender.sendMessage(messages.toArray(new String[messages.size()]));
        return true;
    }

    /*
     * Sets the selection handler for a player.
     */
    public void cmdType(CommandSender sender, String label, String[] args) {
        if (!CommandUtils.performBasicChecks(plugin, sender, PermissionNode.SELECTIONS_TYPE, true)) return;
        if (!CommandUtils.performArgsCheck(plugin, sender, label, args.length, 2, plugin.getMessageManager().getMessage("usages.selections_type"))) return;

        GgSelectionManager selectionManager = plugin.getSelectionManager();
        SelectionHandler handler = null;
        if (!args[1].equalsIgnoreCase("none")) {
            handler = selectionManager.getSelectionHandler(args[1]);
            if (handler == null) {
                plugin.getMessageManager().getMessage("errors.selections_handler_not_found").sendTo(sender, new QuickMap<>("{NAME}", args[1]).build());
                return;
            }
        }

        String newName = handler == null ? ("" + ChatColor.RED + ChatColor.ITALIC + "<none>") : handler.getName();
        if (selectionManager.setPlayerSelectionHandler((Player) sender, handler)) {
            plugin.getMessageManager().getMessage("notices.selections_handler_set").sendTo(sender, new QuickMap<>("{NAME}", newName).build());
        } else {
            plugin.getMessageManager().getMessage("errors.selections_handler_already_set").sendTo(sender, new QuickMap<>("{NAME}", newName).build());
        }
    }

}