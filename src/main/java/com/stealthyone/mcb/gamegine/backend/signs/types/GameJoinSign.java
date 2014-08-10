package com.stealthyone.mcb.gamegine.backend.signs.types;

import com.stealthyone.mcb.gamegine.GameginePlugin;
import com.stealthyone.mcb.gamegine.api.Gamegine;
import com.stealthyone.mcb.gamegine.api.games.modules.GameJoinModule;
import com.stealthyone.mcb.gamegine.api.games.modules.GameJoinModule.GameJoinStatus;
import com.stealthyone.mcb.gamegine.api.signs.ActiveGSign;
import com.stealthyone.mcb.gamegine.api.signs.modules.GSignInteractModule;
import com.stealthyone.mcb.gamegine.lib.games.instances.GameInstance;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.*;

/**
 * A game sign that allows players to join a game.
 */
public class GameJoinSign extends GgYamlGSign implements GSignInteractModule {

    private Map<String, String> gameStatuses = new HashMap<>();

    public GameJoinSign(ConfigurationSection config) {
        super("join", true, config);
    }

    @Override
    public void playerInteract(PlayerInteractEvent e, ActiveGSign sign) {
        GameInstance game = sign.getGame();
        if (!(game.getOwner() instanceof GameJoinModule)) {
            ((GameginePlugin) Gamegine.getInstance()).getMessageManager().getMessage("errors.game_not_joinable").sendTo(e.getPlayer());
        } else {
            ((GameginePlugin) Gamegine.getInstance()).getCmdGames().onCommand(e.getPlayer(), null, "join", createArgs(sign));
        }
    }

    private String[] createArgs(ActiveGSign sign) {
        List<String> args = new ArrayList<>();
        args.add(sign.getGameInstanceRef());
        Object obj = sign.getExtraData().get("args");
        if (obj != null) {
            if (obj instanceof String) {
                args.addAll(Arrays.asList(((String) obj).split(" ")));
            } else if (obj instanceof List) {
                for (Object listObj : (List) obj) {
                    Arrays.asList(listObj.toString().split(" "));
                }
            }
        }
        return args.toArray(new String[args.size()]);
    }

    @Override
    public List<String> getLines(ActiveGSign sign) {
        List<String> list = new ArrayList<>(super.getLines(sign));

        GameInstance game = sign.getGame();
        if (!(game instanceof GameJoinModule)) {
            list.add("" + ChatColor.DARK_RED + ChatColor.BOLD + "NOT JOINABLE");
        } else {
            GameJoinStatus status = ((GameJoinModule) game).getStatus();
            if (status != null) {
                String line = gameStatuses.get(status.getName());
                if (line == null) {
                    line = status.getSignLine();
                    if (line == null) {
                        line = status.getName();
                    }
                }
                list.add(ChatColor.translateAlternateColorCodes('&', line));
            }
        }
        return list;
    }

    @Override
    public void loadFrom(ConfigurationSection config) {
        super.loadFrom(config);

        ConfigurationSection sec = config.getConfigurationSection("statuses");
        if (sec != null) {
            for (String key : sec.getKeys(false)) {
                gameStatuses.put(key, sec.getString(key));
            }
        }

        if (lines.size() != 4) {
            lines.clear();
            lines.add("" + ChatColor.GREEN + ChatColor.BOLD + "{GAME}");
            lines.add(ChatColor.GOLD + "{PLAYERCOUNT}");
            lines.add("");
        }
    }

}