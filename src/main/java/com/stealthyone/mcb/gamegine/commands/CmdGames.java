package com.stealthyone.mcb.gamegine.commands;

import com.stealthyone.mcb.gamegine.GameginePlugin;
import com.stealthyone.mcb.gamegine.api.games.Game;
import com.stealthyone.mcb.gamegine.api.games.GamePlayerAddException;
import com.stealthyone.mcb.gamegine.api.games.modules.GameJoinModule;
import com.stealthyone.mcb.gamegine.api.games.modules.GameLeaveModule;
import com.stealthyone.mcb.gamegine.api.games.modules.GameMessageModule.GameMessage;
import com.stealthyone.mcb.gamegine.backend.games.GameMessageHelper;
import com.stealthyone.mcb.gamegine.messages.Messages.ErrorMessages;
import com.stealthyone.mcb.gamegine.messages.Messages.PluginMessages;
import com.stealthyone.mcb.gamegine.messages.Messages.UsageMessages;
import com.stealthyone.mcb.gamegine.permissions.PermissionNode;
import com.stealthyone.mcb.stbukkitlib.utils.MessageUtils;
import com.stealthyone.mcb.stbukkitlib.utils.MiscUtils;
import com.stealthyone.mcb.stbukkitlib.utils.QuickMap;
import com.stealthyone.mcb.stbukkitlib.utils.StringUtils;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.*;

@RequiredArgsConstructor
public class CmdGames implements CommandExecutor {

    private final GameginePlugin plugin;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length > 0) {
            switch (args[0].toLowerCase()) {
                /* Show help for 'games' command */
                case "help":
                    plugin.getHelpManager().handleHelpCommand("games", sender, label, "help", args);
                    return true;

                /* Join a registered game. */
                case "join":
                    cmdJoin(sender, label, args);
                    return true;

                /* Leave joined game. */
                case "leave":
                    cmdLeave(sender, label, args);
                    return true;

                /* List registered games */
                case "list":
                    cmdList(sender, label, args);
                    return true;

                default:
                    ErrorMessages.UNKNOWN_COMMAND.sendTo(sender);
                    break;
            }
        }
        UsageMessages.GAMES_HELP.sendTo(sender, new QuickMap<>("{LABEL}", label).build());
        return true;
    }

    /*
     * Join a registered game.
     */
    private void cmdJoin(CommandSender sender, String label, String[] args) {
        if (!CommandUtils.performBasicChecks(plugin, sender, PermissionNode.GAMES_JOIN, true)
            || !CommandUtils.performArgsCheck(plugin, sender, label, args.length, 2, plugin.getMessageManager().getMessage("usages.games_join"))) return;

        Game game = CommandUtils.retrieveGame(plugin, sender, args[1]);
        if (game == null) return;

        if (!(game instanceof GameJoinModule)) {
            plugin.getMessageManager().getMessage("errors.game_cannot_join").sendTo(sender, new QuickMap<>("{REASON}", "Game is not joinable.").build());
            return;
        }

        try {
            GameJoinModule joinableGame = (GameJoinModule) game;

            if (args.length > 2) {
                List<String> gameArgs = Arrays.asList(args).subList(1, args.length);
                joinableGame.addPlayer((Player) sender, gameArgs.toArray(new String[gameArgs.size()]));
            } else {
                joinableGame.addPlayer((Player) sender);
            }

            GameMessageHelper.handleGameMessage(game, GameMessage.JOIN, (Player) sender);
        } catch (GamePlayerAddException ex) {
            plugin.getMessageManager().getMessage("errors.game_cannot_join").sendTo(sender, new QuickMap<>("{REASON}", ex.getMessage()).build());

            String logWarning = ex.getLogWarning();
            if (logWarning != null) {
                Bukkit.getLogger().warning("Player '" + sender.getName() + "' encountered a problem trying to join game '" + game.getName() + "': "
                        + logWarning);
            }
        }
    }

    /*
     * Leave a joined game.
     */
    private void cmdLeave(CommandSender sender, String label, String[] args) {
        if (!CommandUtils.performBasicChecks(plugin, sender, PermissionNode.GAMES_LEAVE, true)) return;

        Game game = plugin.getPlayerManager().getGame((Player) sender);
        if (game == null) {
            plugin.getMessageManager().getMessage("errors.not_in_game").sendTo(sender);
            return;
        }

        if (!(game instanceof GameLeaveModule)) {
            plugin.getMessageManager().getMessage("errors.game_cannot_leave");
            return;
        }

        ((GameLeaveModule) game).removePlayer((Player) sender);
        GameMessageHelper.handleGameMessage(game, GameMessage.LEAVE, (Player) sender);
    }

    /*
     * List all registered games.
     */
    private void cmdList(CommandSender sender, String label, String[] args) {
        if (!PermissionNode.GAMES_LIST.isAllowedAlert(sender)) return;

        int page;
        try {
            page = Integer.parseInt(args[1]);
        } catch (IndexOutOfBoundsException ex) {
            page = 1;
        } catch (NumberFormatException ex) {
            return;
        }

        List<Game> games = new ArrayList<>(plugin.getGameManager().getRegisteredGames());
        Collections.sort(games, new Comparator<Game>() {
            @Override
            public int compare(Game o1, Game o2) {
                return o1.getName().compareTo(o2.getName());
            }
        });

        List<String> messages = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            int index = i + ((page - 1) * 8);
            Game game;
            try {
                game = games.get(index);
            } catch (IndexOutOfBoundsException ex) {
                break;
            }

            messages.addAll(Arrays.asList(PluginMessages.CMD_GAMES_LIST_ITEM.getFormattedMessages(new QuickMap<String, String>()
                .put("{NUM}", Integer.toString(index + 1))
                .put("{NAME}", game.getName())
                .put("{AUTHORS}", StringUtils.stringListToString(game.getAuthors()))
                .put("{VERSION}", game.getVersion())
                .build()
            )));
        }

        int maxPages = MiscUtils.getPageCount(games.size(), 8);
        String[] pageNotice = null;
        if (page < maxPages) {
            pageNotice = PluginMessages.PAGE_NOTICE.getFormattedMessages(new QuickMap<>("{LABEL}", label)
                .put("{COMMAND}", " list ")
                .put("{NEXTPAGE}", Integer.toString(page + 1))
                .build()
            );
        }
        PluginMessages.CMD_GAMES_LIST.sendTo(sender, new QuickMap<>("{LABEL}", label)
            .put("{GAMELIST}", messages.size() == 0 ? MessageUtils.stringArrayToString(PluginMessages.CMD_GAMES_LIST_NONE.getFormattedMessages()) : MessageUtils.stringListToString(messages))
            .put("{PAGE}", Integer.toString(page))
            .put("{MAXPAGES}", Integer.toString(maxPages))
            .put("{PAGENOTICE}", pageNotice == null ? "" : MessageUtils.stringArrayToString(pageNotice))
            .build()
        );
    }

}