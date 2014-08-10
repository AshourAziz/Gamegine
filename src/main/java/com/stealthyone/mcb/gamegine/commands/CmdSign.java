package com.stealthyone.mcb.gamegine.commands;

import com.stealthyone.mcb.gamegine.GameginePlugin;
import com.stealthyone.mcb.gamegine.api.games.Game;
import com.stealthyone.mcb.gamegine.api.signs.ActiveGSign;
import com.stealthyone.mcb.gamegine.api.signs.GSignType;
import com.stealthyone.mcb.gamegine.backend.signs.GgSignManager;
import com.stealthyone.mcb.gamegine.lib.games.InstanceGame;
import com.stealthyone.mcb.gamegine.lib.games.MultiInstanceGame;
import com.stealthyone.mcb.gamegine.lib.games.SingleInstanceGame;
import com.stealthyone.mcb.gamegine.lib.games.instances.GameInstance;
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

import java.util.*;
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
    private List<ActiveGSign> getActiveSigns(CommandSender sender, Game game) {
        List<ActiveGSign> signs;
        try {
            signs = new ArrayList<>(plugin.getSignManager().getActiveSigns(game));
        } catch (IllegalArgumentException ex) {
            plugin.getMessageManager().getMessage("errors.signs_invalid_game").sendTo(sender);
            return null;
        }

        Collections.sort(signs, new Comparator<ActiveGSign>() {
            @Override
            public int compare(ActiveGSign o1, ActiveGSign o2) {
                return o1.getGameInstanceRef().compareTo(o2.getGameInstanceRef());
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

        String gameInstanceRef = args[2];
        String[] split = gameInstanceRef.split(":");
        Game game = plugin.getGameManager().getGameByName(split[0]);
        if (game == null) {
            plugin.getMessageManager().getMessage("errors.game_not_found").sendTo(sender, new QuickMap<>("{NAME}", split[0]).build());
            return;
        }

        if (!(game instanceof InstanceGame)) {
            plugin.getMessageManager().getMessage("errors.signs_invalid_game").sendTo(sender);
            return;
        }

        GameInstance instance;

        if (split.length == 1) {
            if (game instanceof SingleInstanceGame) {
                instance = ((SingleInstanceGame) game).getGameInstance();
            } else {
                plugin.getMessageManager().getMessage("errors.signs_create_ref_req").sendTo(sender);
                return;
            }
        } else {
            String rawId = split[1];
            if (rawId.equalsIgnoreCase("MAIN")) {
                if (game instanceof SingleInstanceGame) {
                    instance = ((SingleInstanceGame) game).getGameInstance();
                } else {
                    plugin.getMessageManager().getMessage("errors.sign_create_ref_invalid").sendTo(sender);
                    return;
                }
            } else {
                if (game instanceof SingleInstanceGame) {
                    plugin.getMessageManager().getMessage("errors.sign_create_ref_invalid").sendTo(sender);
                    return;
                } else {
                    int id;
                    try {
                        id = Integer.parseInt(rawId);
                    } catch (NumberFormatException ex) {
                        plugin.getMessageManager().getMessage("errors.sign_create_ref_invalid").sendTo(sender);
                        return;
                    }
                    instance = ((MultiInstanceGame) game).getGameInstance(id);
                }
            }
        }

        List<String> signArgs = new ArrayList<>();
        if (args.length > 3) {
            signArgs.addAll(Arrays.asList(args).subList(3, args.length));
        }

        if (plugin.getSignManager().createSign(block, type, gameInstanceRef, instance, new QuickMap<String, Object>("args", signArgs.toArray(new String[signArgs.size()])).build())) {
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
        messages.add(String.format(LIST_ELEMENT, "Game", sign.getGameInstanceRef()));
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
     * /{LABEL} list <game>
     */
    private void cmdList(CommandSender sender, String label, String[] args) {
        if (!CommandUtils.performBasicChecks(plugin, sender, PermissionNode.SIGNS_LIST, false)) return;
        if (!CommandUtils.performArgsCheck(plugin, sender, label, args.length, 2, plugin.getMessageManager().getMessage("usages.signs_list"))) return;

        Game game = CommandUtils.retrieveGame(plugin, sender, args[1]);
        if (game == null) return;

        int page = CommandUtils.getPage(plugin, sender, args, 2);
        if (page == -1) return;

        List<ActiveGSign> signs = getActiveSigns(sender, game);
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
        mm.getMessage("plugin.cmd_signs_list_header").sendTo(sender, new QuickMap<>("{GAME}", game.getName()).put("{PAGE}", Integer.toString(page)).put("{MAXPAGES}", Integer.toString(pageCount)).build());
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

        Game game = CommandUtils.retrieveGame(plugin, sender, args[1]);
        if (game == null) return;

        List<ActiveGSign> signs = getActiveSigns(sender, game);
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
        plugin.getMessageManager().getMessage("notices.signs_teleported").sendTo(sender, new QuickMap<>("{SIGN}", Integer.toString(number)).put("{GAME}", game.getName()).build());
    }

    /*
     * List registered sign types.
     */
    private void cmdTypes(CommandSender sender, String label, String[] args) {
        if (!CommandUtils.performBasicChecks(plugin, sender, PermissionNode.SIGNS_TYPES, false)) return;

        int page = CommandUtils.getPage(plugin, sender, args, 1);
        if (page == -1) return;

        GgSignManager signManager = plugin.getSignManager();

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

}