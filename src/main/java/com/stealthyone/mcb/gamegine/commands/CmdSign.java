package com.stealthyone.mcb.gamegine.commands;

import com.stealthyone.mcb.gamegine.GameginePlugin;
import lombok.RequiredArgsConstructor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

@RequiredArgsConstructor
public class CmdSign implements CommandExecutor {

    private final GameginePlugin plugin;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length > 0) {
            switch (args[0].toLowerCase()) {
                case "add":
                    cmdAdd(sender, label, args);
                    return true;

                case "info":
                    cmdInfo(sender, label, args);
                    return true;

                case "list":
                    cmdList(sender, label, args);
                    return true;

                case "tp":
                    cmdTp(sender, label, args);
                    return true;

                case "types":
                    cmdTypes(sender, label, args);
                    return true;
            }
        }
        return true;
    }

    /*
     * Add a sign.
     */
    private void cmdAdd(CommandSender sender, String label, String[] args) {

    }

    /*
     * View info about a sign.
     */
    private void cmdInfo(CommandSender sender, String label, String[] args) {

    }

    /*
     * List active signs.
     */
    private void cmdList(CommandSender sender, String label, String[] args) {

    }

    /*
     * Teleport to a sign.
     */
    private void cmdTp(CommandSender sender, String label, String[] args) {

    }

    /*
     * List registered sign types.
     */
    private void cmdTypes(CommandSender sender, String label, String[] args) {

    }

}