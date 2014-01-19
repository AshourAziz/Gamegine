package com.stealthyone.mcb.gamegine.commands;

import com.stealthyone.mcb.gamegine.Gamegine;
import com.stealthyone.mcb.gamegine.backend.cooldowns.Cooldown;
import com.stealthyone.mcb.gamegine.messages.ErrorMessage;
import com.stealthyone.mcb.gamegine.messages.NoticeMessage;
import com.stealthyone.mcb.gamegine.messages.UsageMessage;
import com.stealthyone.mcb.gamegine.permissions.PermissionNode;
import com.stealthyone.mcb.gamegine.utils.GamegineUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class CmdCooldown implements CommandExecutor {

    private Gamegine plugin;

    public CmdCooldown(Gamegine plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!plugin.getCooldownManager().isEnabled()) {
            ErrorMessage.COOLDOWNS_SYSTEM_DISABLED.sendTo(sender);
            return true;
        }

        if (args.length > 0) {
            switch (args[0].toLowerCase()) {
                /* List cooldowns */
                case "list":
                    cmdList(sender, command, label, args);
                    return true;
            }
        }
        return true;
    }

    /*
     * List cooldowns command
     */
    private void cmdList(CommandSender sender, Command command, String label, String[] args) {
        if (!PermissionNode.POINTS_CHECK.isAllowed(sender, true)) return;

        String name = sender.getName();
        if (args.length == 1) {
            if (!(sender instanceof Player)) {
                UsageMessage.COOLDOWNS_LIST_OTHERS.sendTo(sender, label);
                return;
            } else {
                name = args[1];
            }
        }

        if (!name.equalsIgnoreCase(sender.getName()) && PermissionNode.POINTS_CHECK_OTHERS.isAllowed(sender, true)) {
            List<String> messages = new ArrayList<>();
            Map<String, Cooldown> cooldowns = plugin.getCooldownManager().getCooldowns(name);
            if (cooldowns != null) {
                int page = 1;
                if (args.length > 1) {
                    try {
                        page = Integer.parseInt(args[args.length - 1]);
                    } catch (NumberFormatException ex) {}
                }

                List<Entry<String, Cooldown>> cooldownList = new ArrayList<>(cooldowns.entrySet());
                messages.add(ChatColor.DARK_GRAY + "=====" + ChatColor.GREEN + "Cooldowns" + ChatColor.DARK_GRAY + "=====");
                for (int i = 0; i < 8; i++) {
                    int index = ((page - 1) * 8) + i;

                    Entry<String, Cooldown> current;
                    try {
                        current = cooldownList.get(i);
                    } catch (IndexOutOfBoundsException ex) {
                        continue;
                    }

                    Cooldown curCd = current.getValue();
                    long curTicks = curCd.getCurrentTicks();
                    long maxTicks = curCd.getCooldownTicks();
                    messages.add(ChatColor.DARK_GRAY + Integer.toString(index) + ") " + ChatColor.YELLOW + "'" + current.getKey() + "' " + ChatColor.RED + curTicks + "/" + maxTicks + " (" + GamegineUtils.colorPercentage((curTicks / maxTicks) * 100D) + ")");
                }

            }

            if (messages.size() == 0) {
                messages.add(NoticeMessage.COOLDOWNS_PLAYER_HAS_NONE.getMessage(name));
            }
            sender.sendMessage(messages.toArray(new String[messages.size()]));
        }
    }

}