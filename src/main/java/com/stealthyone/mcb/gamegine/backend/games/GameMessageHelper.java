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
package com.stealthyone.mcb.gamegine.backend.games;

import com.stealthyone.mcb.gamegine.GameginePlugin;
import com.stealthyone.mcb.gamegine.api.Gamegine;
import com.stealthyone.mcb.gamegine.api.games.Game;
import com.stealthyone.mcb.gamegine.api.games.modules.GameMessageModule;
import com.stealthyone.mcb.gamegine.api.games.modules.GameMessageModule.GameMessage;
import com.stealthyone.mcb.gamegine.api.games.modules.GameMessageModule.GameMessageManager;
import com.stealthyone.mcb.gamegine.lib.games.MultiInstanceGame;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public final class GameMessageHelper {

    private GameMessageHelper() { }

    public static String getMessage(Game game, GameMessage messageType, Player player) {
        String message = null;
        if (game instanceof GameMessageModule) {
            GameMessageManager messageManager = ((GameMessageModule) game).getMessageManager();

            message = messageManager.getMessage(messageType, player);

            if (message == null) {
                return null;
            }

            if (message.equals(GameMessageManager.DEFAULT_MESSAGE)) {
                message = null;
            }
        }

        if (message == null) {
            message = ((GameginePlugin) Gamegine.getInstance()).getMessageManager().getMessage("game_messages." + messageType.getName()).getMessages().get(0);
        }

        return ChatColor.translateAlternateColorCodes('&', message
            .replace("{GAME}", game.getName())
            .replace("{PLAYER}", player.getName())
        );
    }

    public static void handleGameMessage(Game game, GameMessage messageType, Player player) {
        String message = null;
        GameMessage broadcastType = null;
        String broadcastMessage = null;

        GameMessageManager messageManager = null;
        if (game instanceof GameMessageModule) {
            messageManager = ((GameMessageModule) game).getMessageManager();

            message = messageManager.getMessage(messageType, player);
        }

        if (!messageType.toString().endsWith("_BROADCAST")) {
            try {
                broadcastType = GameMessage.valueOf(messageType.toString() + "_BROADCAST");

                if (messageManager != null) {
                    broadcastMessage = messageManager.getMessage(broadcastType, player);
                }
            } catch (Exception ex) { }
        }

        if (message != null) {
            if (message.equals(GameMessageManager.DEFAULT_MESSAGE)) {
                message = ((GameginePlugin) Gamegine.getInstance()).getMessageManager().getMessage("game_messages." + messageType.getName()).getMessages().get(0);

                player.sendMessage(ChatColor.translateAlternateColorCodes('&', message
                    .replace("{GAME}", game.getName())
                    .replace("{PLAYER}", player.getName())
                ));
            }
        }

        if (broadcastMessage != null) {
            String finalMessage;
            if (broadcastMessage.equals(GameMessageManager.DEFAULT_MESSAGE)) {
                finalMessage = ChatColor.translateAlternateColorCodes('&', ((GameginePlugin) Gamegine.getInstance()).getMessageManager().getMessage("game_messages." + broadcastType.getName()).getMessages().get(0)
                    .replace("{GAME}", game.getName())
                    .replace("{PLAYER}", player.getName())
                );
            } else {
                finalMessage = ChatColor.translateAlternateColorCodes('&', broadcastMessage
                    .replace("{GAME}", game.getName())
                    .replace("{PLAYER}", player.getName())
                );
            }

            if (game instanceof MultiInstanceGame) {
                for (Player p : Gamegine.getInstance().getPlayerManager().getGamePlayers(((MultiInstanceGame) game).getGameInstance(player))) {
                    if (p.equals(player)) continue;
                    p.sendMessage(message);
                }
            } else {
                for (Player p : Gamegine.getInstance().getPlayerManager().getGamePlayers(game)) {
                    if (p.equals(player)) continue;
                    p.sendMessage(finalMessage);
                }
            }
        }
    }

}