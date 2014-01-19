package com.stealthyone.mcb.gamegine.commands;

import com.stealthyone.mcb.gamegine.Gamegine;
import com.stealthyone.mcb.gamegine.backend.players.GgPlayerFile;
import com.stealthyone.mcb.gamegine.messages.ErrorMessage;
import com.stealthyone.mcb.gamegine.permissions.PermissionNode;
import com.stealthyone.mcb.stbukkitlib.api.Stbl;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

public class CmdPoints implements CommandExecutor {

    private Gamegine plugin;

    public CmdPoints(Gamegine plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length > 0) {
            switch (args[0].toLowerCase()) {
                /* Check points */
                case "check":
                    cmdCheck(sender, command, label, args, 1);
                    return true;

                /* View points leaderboard(s) */
                case "top":
                    cmdTop(sender, command, label, args);
                    return true;
            }
        }
        cmdCheck(sender, command, label, args, 0);
        return true;
    }

    /*
     * Check points command
     */
    private void cmdCheck(CommandSender sender, Command command, String label, String[] args, int startIndex) {
        if (!PermissionNode.POINTS_CHECK.isAllowed(sender)) {
            ErrorMessage.NO_PERMISSION.sendTo(sender);
            return;
        }

        String target = sender.getName();
        if (args.length > startIndex) {
            target = args[startIndex];
        }

        if (target.equals(sender.getName())) {
            if (!(sender instanceof Player)) {
                ErrorMessage.MUST_BE_PLAYER.sendTo(sender);
                return;
            }
        } else if (!PermissionNode.POINTS_CHECK_OTHERS.isAllowed(sender)) {
            ErrorMessage.NO_PERMISSION.sendTo(sender);
            return;
        }

        String targetId = Stbl.getUuidManager().getUuid(target);
        if (targetId == null) {
            ErrorMessage.UNKNOWN_PLAYER.sendTo(sender, target);
        } else {
            target = Stbl.getUuidManager().getName(targetId);
            GgPlayerFile file = plugin.getPlayerManager().getFile(targetId, false);
            if (file == null) {
                ErrorMessage.UNKNOWN_PLAYER.sendTo(sender, target);
            } else {
                List<String> messages = new ArrayList<>();
                messages.add(ChatColor.DARK_GRAY + "=====" + ChatColor.GREEN + "Player Points: " + ChatColor.GOLD + target + ChatColor.DARK_GRAY + "=====");
                messages.add(ChatColor.RED + "Available points: " + ChatColor.YELLOW + file.getAvailablePoints());
                messages.add(ChatColor.RED + "Total points: " + ChatColor.YELLOW + file.getTotalPoints());
                for (Entry<String, Double> earnedPointEntry : file.getEarnedPoints().entrySet()) {
                    String gameName;
                    try {
                        gameName = plugin.getGameManager().getGame(earnedPointEntry.getKey()).getName();
                    } catch (NullPointerException ex) {
                        gameName = earnedPointEntry.getKey();
                    }
                    messages.add(ChatColor.GREEN + " " + gameName + ": " + ChatColor.YELLOW + earnedPointEntry.getValue());
                }
                sender.sendMessage(messages.toArray(new String[messages.size()]));
            }
        }
    }

    /*
     * Points leaderboard(s) command
     */
    private void cmdTop(CommandSender sender, Command command, String label, String[] args) {
        sender.sendMessage(ChatColor.RED + "Not yet implemented.");
    }

}