package com.stealthyone.mcb.gamegine.commands;

import com.stealthyone.mcb.gamegine.GameginePlugin;
import com.stealthyone.mcb.gamegine.api.games.Game;
import com.stealthyone.mcb.gamegine.messages.Messages.ErrorMessages;
import com.stealthyone.mcb.gamegine.messages.Messages.PluginMessages;
import com.stealthyone.mcb.gamegine.messages.Messages.UsageMessages;
import com.stealthyone.mcb.gamegine.permissions.PermissionNode;
import com.stealthyone.mcb.stbukkitlib.utils.MessageUtils;
import com.stealthyone.mcb.stbukkitlib.utils.MiscUtils;
import com.stealthyone.mcb.stbukkitlib.utils.QuickMap;
import com.stealthyone.mcb.stbukkitlib.utils.StringUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.*;

public class CmdGames implements CommandExecutor {

    private GameginePlugin plugin;

    public CmdGames(GameginePlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length > 0) {
            switch (args[0].toLowerCase()) {
                /* Show help for 'games' command */
                case "help":
                    plugin.getHelpManager().handleHelpCommand("games", sender, label, "help", args);
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
                .put("{NUM}", Integer.toString(index))
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