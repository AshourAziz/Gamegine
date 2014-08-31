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
import com.stealthyone.mcb.gamegine.api.signs.ActiveGSign;
import com.stealthyone.mcb.gamegine.api.signs.GSignType;
import com.stealthyone.mcb.gamegine.api.signs.handler.GSignProvider;
import com.stealthyone.mcb.gamegine.api.signs.handler.MultiSignHandler;
import com.stealthyone.mcb.gamegine.api.signs.handler.SignHandler;
import com.stealthyone.mcb.gamegine.api.signs.handler.SignProviderReference;
import com.stealthyone.mcb.gamegine.api.signs.handler.SingleSignHandler;
import com.stealthyone.mcb.gamegine.permissions.PermissionNode;
import com.stealthyone.mcb.stbukkitlib.messages.Message;
import com.stealthyone.mcb.stbukkitlib.messages.MessageManager;
import com.stealthyone.mcb.stbukkitlib.utils.MiscUtils;
import com.stealthyone.mcb.stbukkitlib.utils.QuickMap;
import lombok.RequiredArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.BlockIterator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map.Entry;

@RequiredArgsConstructor
public class CmdSign implements CommandExecutor {

    private final GameginePlugin plugin;

    //TODO: Make these non-hardcoded.
    private final static String LIST_ELEMENT = ChatColor.YELLOW + "%s: " + ChatColor.BLUE + "%s";
    private final static String TYPE_ELEMENT = ChatColor.YELLOW + "{NUM}) " + "{NAME}";

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length > 0) {
            switch (args[0].toLowerCase()) {
                case "create":
                    cmdCreate(sender, label, args);
                    return true;

                case "delete":
                    cmdDelete(sender, label, args);
                    return true;

                /* Lists all registered sign handlers. */
                case "handlers":
                    cmdHandlers(sender, args);
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

                /* Lists all registered sign types. */
                case "types":
                    cmdTypes(sender, label, args);
                    return true;

                /* Selects a sign provider. */
                case "use":
                    cmdUse(sender, label, args);
                    return true;
            }
        }
        return true;
    }

    // Utility method.
    private Block getTargetBlock(Player player) {
        BlockIterator iterator = new BlockIterator(player.getEyeLocation(), 0D, 10);
        Block block = null;
        while (iterator.hasNext()) {
            Block b = iterator.next();
            if (b != null && b.getType() != Material.AIR) {
                block = b;
                break;
            }
        }

        if (block == null) {
            plugin.getMessageManager().getMessage("errors.must_look_at_sign").sendTo(player);
            return null;
        }

        if (!(block.getType() == Material.SIGN_POST || block.getType() == Material.WALL_SIGN)) {
            plugin.getMessageManager().getMessage("errors.block_not_sign").sendTo(player);
            return null;
        }
        return block;
    }

    // Utility method.
    private List<GSignType> getSignTypes() {
        List<GSignType> types = new ArrayList<>(plugin.getSignManager().getRegisteredTypes());
        Collections.sort(types, new Comparator<GSignType>() {
            @Override
            public int compare(GSignType o1, GSignType o2) {
                return o1.getClass().getCanonicalName().compareTo(o2.getClass().getCanonicalName());
            }
        });
        return types;
    }

    // Utility method.
    private SignHandler getSignHandler(CommandSender sender, String name) {
        SignHandler handler = plugin.getSignManager().getSignHandlerByName(name);
        if (handler == null) {
            plugin.getMessageManager().getMessage("errors.signs_handler_not_found").sendTo(sender, new QuickMap<>("{NAME}", name).build());
        }
        return handler;
    }

    // Utility method.
    private GSignProvider getSignProvider(CommandSender sender, String raw) {
        SignProviderReference ref = new SignProviderReference(raw);
        GSignProvider provider;
        try {
            provider = plugin.getSignManager().getSignProvider(ref);
        } catch (Exception ex) {
            plugin.getMessageManager().getMessage("errors.signs_provider_invalid").sendTo(sender, new QuickMap<>("{REASON}", ex.getMessage()).build());
            return null;
        }

        if (provider == null) {
            plugin.getMessageManager().getMessage("errors.signs_provider_not_found").sendTo(sender, new QuickMap<>("{NAME}", ref.getHandlerIdentifier()).build());
        }
        return null;
    }

    // Utility method.
    private List<ActiveGSign> getActiveSigns(SignHandler handler) {
        List<ActiveGSign> signs = new ArrayList<>(plugin.getSignManager().getActiveSigns(handler));

        Collections.sort(signs, new Comparator<ActiveGSign>() {
            @Override
            public int compare(ActiveGSign o1, ActiveGSign o2) {
                return o1.getProviderReference().compareTo(o2.getProviderReference());
            }
        });
        return signs;
    }

    /*
     * Create a sign.
     */
    private void cmdCreate(CommandSender sender, String label, String[] args) {
        if (!CommandUtils.performBasicChecks(plugin, sender, PermissionNode.SIGNS_CREATE, true)) return;
        if (!CommandUtils.performArgsCheck(plugin, sender, label, args.length, 3, plugin.getMessageManager().getMessage("usages.signs_create"))) return;

        Block block = getTargetBlock((Player) sender);
        if (block == null) return;

        GSignType type;
        try {
            int id = Integer.parseInt(args[1]);
            type = getSignTypes().get(id);
        } catch (NumberFormatException ex) {
            type = plugin.getSignManager().getSignType(args[1]);
        }

        if (type == null) {
            plugin.getMessageManager().getMessage("errors.signs_type_not_found").sendTo(sender, new QuickMap<>("{TYPE}", args[1]).build());
            return;
        }

        GSignProvider provider = getSignProvider(sender, args[2]);
        if (provider == null) return;

        List<String> signArgs = new ArrayList<>();
        if (args.length > 3) {
            signArgs.addAll(Arrays.asList(args).subList(3, args.length));
        }

        if (plugin.getSignManager().createSign(block, type, provider, new QuickMap<String, Object>("args", signArgs.toArray(new String[signArgs.size()])).build())) {
            plugin.getMessageManager().getMessage("notices.signs_created").sendTo(sender);
        } else {
            plugin.getMessageManager().getMessage("errors.signs_unable_to_create").sendTo(sender);
        }
    }

    /*
     * Delete a sign.
     */
    private void cmdDelete(CommandSender sender, String label, String[] args) {
        //TODO: Add command to delete signs.  It'll also be possible to remove signs by shift+destroying them.
    }

    /*
     * List all registered sign handlers.
     */
    private void cmdHandlers(CommandSender sender, String[] args) {
        if (!CommandUtils.performBasicChecks(plugin, sender, PermissionNode.SIGNS_HANDLERS, false)) return;

        int page = CommandUtils.getPage(plugin, sender, args, 1);
        if (page == -1) return;

        List<SignHandler> signHandlers = new ArrayList<>(plugin.getSignManager().getSignHandlers());
        Collections.sort(signHandlers, new Comparator<SignHandler>() {
            @Override
            public int compare(SignHandler o1, SignHandler o2) {
                return o1.getName().compareTo(o2.getName());
            }
        });

        List<String> messages = new ArrayList<>();
        Message elementMsg = plugin.getMessageManager().getMessage("plugin.cmd_signs_handlers_element");
        for (int i = 0; i < 8; i++) {
            int index = i + ((page - 1) * 8);

            SignHandler cur;
            try {
                cur = signHandlers.get(index);
            } catch (Exception ex) {
                break;
            }

            messages.addAll(Arrays.asList(elementMsg.getFormattedMessages(new QuickMap<String, String>()
                .put("{NUM}", Integer.toString(index + 1))
                .put("{PROVIDERS}", Integer.toString(cur instanceof SingleSignHandler ? 1 : ((MultiSignHandler) cur).getProviders().size()))
                .build()
            )));
        }

        plugin.getMessageManager().getMessage("plugin.cmd_signs_handlers_header").sendTo(sender, new QuickMap<String, String>()
            .put("{PAGE}", Integer.toString(page))
            .put("{MAXPAGES}", Integer.toString(MiscUtils.getPageCount(signHandlers.size(), 8)))
            .build()
        );

        if (messages.isEmpty()) {
            plugin.getMessageManager().getMessage("plugin.cmd_signs_handlers_none").sendTo(sender);
        } else {
            sender.sendMessage(messages.toArray(new String[messages.size()]));
        }
    }

    /*
     * View info about a sign.
     */
    private void cmdInfo(CommandSender sender, String label, String[] args) {
        if (!CommandUtils.performBasicChecks(plugin, sender, PermissionNode.SIGNS_INFO, true)) return;

        Block block = getTargetBlock((Player) sender);
        if (block == null) return;

        ActiveGSign sign = plugin.getSignManager().getSign(block.getLocation());
        if (sign == null) {
            plugin.getMessageManager().getMessage("errors.not_valid_sign").sendTo(sender);
            return;
        }

        List<String> messages = new ArrayList<>();
        messages.add(String.format(LIST_ELEMENT, "Type", sign.getType().getClass().getCanonicalName()));
        messages.add(String.format(LIST_ELEMENT, "Provider", sign.getProviderReference().toString()));
        messages.add(String.format(LIST_ELEMENT, "File", sign.getFile().getFile().getPath()));
        StringBuilder extraData = new StringBuilder();
        for (Entry<String, Object> data : sign.getExtraData().entrySet()) {
            if (extraData.length() > 0) {
                extraData.append("\n");
            }
            extraData.append(ChatColor.BLUE).append(data.getKey()).append(": ").append(ChatColor.RED).append(data.getValue().toString());
        }
        if (extraData.length() > 0) messages.add(String.format(LIST_ELEMENT, "Extra data", extraData.toString()));

        plugin.getMessageManager().getMessage("plugin.cmd_signs_info_header").sendTo(sender, new QuickMap<>("{LOCATION}", sign.getLocation().toString()).build());
        sender.sendMessage(messages.toArray(new String[messages.size()]));
    }

    /*
     * List active signs.
     * /{LABEL} list <handler>
     */
    private void cmdList(CommandSender sender, String label, String[] args) {
        if (!CommandUtils.performBasicChecks(plugin, sender, PermissionNode.SIGNS_LIST, false)) return;
        if (!CommandUtils.performArgsCheck(plugin, sender, label, args.length, 2, plugin.getMessageManager().getMessage("usages.signs_list"))) return;

        SignHandler handler = getSignHandler(sender, args[1]);
        if (handler == null) return;

        int page = CommandUtils.getPage(plugin, sender, args, 2);
        if (page == -1) return;

        List<ActiveGSign> signs = getActiveSigns(handler);
        if (signs == null) return;

        List<String> messages = new ArrayList<>();

        MessageManager mm = plugin.getMessageManager();
        Message element = mm.getMessage("plugin.cmd_signs_list_element");

        for (int i = 0; i < 8; i++) {
            int index = i + ((page - 1) * 8);
            ActiveGSign cur;
            try {
                cur = signs.get(index);
            } catch (Exception ex) {
                break;
            }

            messages.addAll(Arrays.asList(element.getFormattedMessages(new QuickMap<String, String>()
                .put("{NUM}", Integer.toString(index + 1))
                .put("{TYPE}", cur.getType().getClass().getCanonicalName())
                .put("{LOCATION}", cur.getLocation().toString())
                .build()
            )));
        }

        int pageCount = MiscUtils.getPageCount(signs.size(), 8);
        mm.getMessage("plugin.cmd_signs_list_header").sendTo(sender, new QuickMap<>("{HANDLER}", handler.getName()).put("{PAGE}", Integer.toString(page)).put("{MAXPAGES}", Integer.toString(pageCount)).build());
        if (messages.isEmpty()) {
            mm.getMessage("plugin.cmd_signs_list_none").sendTo(sender);
        } else {
            sender.sendMessage(messages.toArray(new String[messages.size()]));
        }
    }

    /*
     * Teleport to an active sign.
     */
    private void cmdTp(CommandSender sender, String label, String[] args) {
        if (!CommandUtils.performBasicChecks(plugin, sender, PermissionNode.SIGNS_TELEPORT, true)) return;
        if (!CommandUtils.performArgsCheck(plugin, sender, label, args.length, 3, plugin.getMessageManager().getMessage("usages.signs_tp"))) return;

        SignHandler handler = getSignHandler(sender, args[1]);
        if (handler == null) return;

        List<ActiveGSign> signs = getActiveSigns(handler);
        if (signs == null) return;

        int number;
        try {
            number = Integer.parseInt(args[2]);
        } catch (Exception ex) {
            plugin.getMessageManager().getMessage("errors.item_must_be_int").sendTo(sender, new QuickMap<>("{ITEM}", "Sign number").build());
            return;
        }

        ActiveGSign sign;
        try {
            sign = signs.get(number);
        } catch (IndexOutOfBoundsException ex) {
            plugin.getMessageManager().getMessage("errors.signs_invalid_number").sendTo(sender);
            return;
        }

        ((Player) sender).teleport(sign.getLocation().getBlock().getLocation());
        plugin.getMessageManager().getMessage("notices.signs_teleported").sendTo(sender, new QuickMap<>("{SIGN}", Integer.toString(number)).put("{HANDLER}", handler.getName()).build());
    }

    /*
     * List registered sign types.
     */
    private void cmdTypes(CommandSender sender, String label, String[] args) {
        if (!CommandUtils.performBasicChecks(plugin, sender, PermissionNode.SIGNS_TYPES, false)) return;

        int page = CommandUtils.getPage(plugin, sender, args, 1);
        if (page == -1) return;

        List<String> messages = new ArrayList<>();
        List<GSignType> types = getSignTypes();

        for (int i = 0; i < 8; i++) {
            int index = i + ((page - 1) * 8);
            GSignType type;
            try {
                type = types.get(index);
            } catch (Exception ex) {
                break;
            }

            String shortName = type.getShortName();
            String rawName = ChatColor.RED + type.getClass().getCanonicalName();
            messages.add(TYPE_ELEMENT
                .replace("{NUM}", Integer.toString(index + 1))
                .replace("{NAME}", shortName != null ? (ChatColor.BLUE + shortName + " (" + rawName + ")") : (rawName))
            );
        }

        int maxPages = MiscUtils.getPageCount(types.size(), 8);
        plugin.getMessageManager().getMessage("plugin.cmd_signs_types_header").sendTo(sender, new QuickMap<>("{PAGE}", Integer.toString(page)).put("{MAXPAGES}", Integer.toString(maxPages)).build());
        if (messages.isEmpty()) {
            plugin.getMessageManager().getMessage("plugin.cmd_signs_types_none").sendTo(sender);
        } else {
            sender.sendMessage(messages.toArray(new String[messages.size()]));
        }
    }

    /*
     * Selects a sign provider.
     */
    private void cmdUse(CommandSender sender, String label, String[] args) {
        if (!CommandUtils.performBasicChecks(plugin, sender, PermissionNode.SIGNS_CREATE, true)) return;
        if (!CommandUtils.performArgsCheck(plugin, sender, label, args.length, 2, plugin.getMessageManager().getMessage("usages.signs_use"))) return;

        String name = args[1];
        SignHandler signHandler = plugin.getSignManager().getSignHandlerByName(name);
        if (signHandler == null) {
            if (!name.equals("none")) {
                plugin.getMessageManager().getMessage("errors.signs_handler_not_found").sendTo(sender, new QuickMap<>("{NAME}", name).build());
                return;
            } else {
                name = null;
            }
        }

        GSignProvider provider = null;
        if (signHandler != null) {
            if (signHandler instanceof SingleSignHandler) {
                provider = ((SingleSignHandler) signHandler).getProvider();
            } else if (signHandler instanceof MultiSignHandler) {
                if (args.length < 3 || args[2].equals("")) {
                    plugin.getMessageManager().getMessage("usages.sign_use").sendTo(sender, new QuickMap<>("{LABEL}", label).build());
                    return;
                }

                provider = ((MultiSignHandler) signHandler).getProvider(args[2]);
            }
        }

        if (provider == null && name != null) {
            plugin.getMessageManager().getMessage("errors.signs_provider_not_found").sendTo(sender, new QuickMap<String, String>()
                .put("{NAME}", name)
                .build()
            );
            return;
        }

        if (!plugin.getSignManager().setPlayerProvider(((Player) sender).getUniqueId(), provider)) {
            plugin.getMessageManager().getMessage("errors.signs_provider_already_set").sendTo(sender);
        } else {
            plugin.getMessageManager().getMessage("notices.signs_provider_set").sendTo(sender);
        }
    }

}