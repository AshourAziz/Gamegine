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
package com.stealthyone.mcb.gamegine.backend.signs.types;

import com.stealthyone.mcb.gamegine.GameginePlugin;
import com.stealthyone.mcb.gamegine.api.Gamegine;
import com.stealthyone.mcb.gamegine.api.games.Game;
import com.stealthyone.mcb.gamegine.api.games.modules.GameJoinModule;
import com.stealthyone.mcb.gamegine.api.games.modules.GameJoinModule.GameJoinStatus;
import com.stealthyone.mcb.gamegine.api.signs.ActiveGSign;
import com.stealthyone.mcb.gamegine.api.signs.modules.GSignInteractModule;
import com.stealthyone.mcb.gamegine.api.signs.variables.SignVariable.DefaultVariable;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        Game game = sign.getProvider().getHandler().getGame();
        if (!(game instanceof GameJoinModule)) {
            ((GameginePlugin) Gamegine.getInstance()).getMessageManager().getMessage("errors.game_not_joinable").sendTo(e.getPlayer());
            return;
        }

        if (e.getPlayer().isSneaking()) {
            ((GameginePlugin) Gamegine.getInstance()).getCmdGame().onCommand(e.getPlayer(), null, "games", new String[]{ "leave" });
        } else {
            ((GameginePlugin) Gamegine.getInstance()).getCmdGame().onCommand(e.getPlayer(), null, "games", createArgs(sign, "join"));
        }
    }

    private String[] createArgs(ActiveGSign sign, String firstArg) {
        List<String> args = new ArrayList<>();
        args.add(firstArg);
        args.add(sign.getProvider().getValue(DefaultVariable.GAME_REFERENCE));
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

        Game game = sign.getProvider().getHandler().getGame();
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