package com.stealthyone.mcb.gameginecore.backend.games;

import com.stealthyone.mcb.gameginecore.Gamegine;
import com.stealthyone.mcb.stbukkitlib.lib.plugin.LogHelper;
import org.bukkit.Bukkit;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GameManager {

    private Gamegine plugin;

    private Map<String, List<String>> pluginToGames = new HashMap<>(); //Plugin name, games registered under plugin
    private Map<String, String> gamesToPlugins = new HashMap<>(); //Game, plugin game is registered under

    private Map<String, Game> registeredGames = new HashMap<>(); //Game unique ID, game

    public GameManager(Gamegine plugin) {
        this.plugin = plugin;
    }

    public void save() {
        for (Game game : registeredGames.values()) {
            game.getGameConfig().saveFile();
        }
    }

    public boolean registerGame(Game game) {
        String id = game.getUniqueId();
        if (!registeredGames.containsKey(id)) {
            Logger log = Bukkit.getLogger();

            log.log(Level.INFO, "");
            log.log(Level.INFO, "-----Gamegine: Registering game '" + id + "'-----");

            // Step 1: Register game
            registeredGames.put(id, game);
            try {
                pluginToGames.get(game.getOwner().getName()).add(game.getName());
            } catch (NullPointerException ex) {
                pluginToGames.put(game.getOwner().getName(), new ArrayList<String>(Arrays.asList(game.getName())));
            }
            gamesToPlugins.put(game.getName(), game.getOwner().getName());
            log.log(Level.INFO, "Registered game successfully.");

            // Step 2: Load signs
            log.log(Level.INFO, "Loading signs...");
            int signCount = plugin.getSignManager().reloadSigns(game);
            log.log(Level.INFO, "Loaded " + (signCount == -1 ? 0 : signCount) + " signs.");

            // Step 3: Load arenas
            int arenaCount = plugin.getArenaManager().reloadArenas(game);
            log.log(Level.INFO, "Loaded " + arenaCount + " arenas.");

            log.log(Level.INFO, "");
            return true;
        }
        LogHelper.WARNING(plugin, "Unable to register game: '" + id + "' -> game already registered.");
        return false;
    }

    public Game getGame(String gameId) {
        return registeredGames.get(gameId);
    }

    public GameInstance getGameInstance()

}