package com.stealthyone.mcb.gamegine.backend.games;

import com.stealthyone.mcb.gamegine.Gamegine;
import com.stealthyone.mcb.gamegine.backend.signs.GgSign;
import com.stealthyone.mcb.stbukkitlib.lib.plugin.LogHelper;
import com.stealthyone.mcb.stbukkitlib.lib.storage.YamlFileManager;
import org.bukkit.Bukkit;

import java.io.File;
import java.util.*;

public class GameManager {

    private Gamegine plugin;

    private Map<String, List<String>> pluginToGames = new HashMap<>(); //Plugin name, games registered under plugin
    private Map<String, String> gamesToPlugins = new HashMap<>(); //Game, plugin game is registered under

    private Map<String, Game> registeredGames = new HashMap<>(); //Game unique ID, game
    private Map<String, YamlFileManager> gameFiles = new HashMap<>(); //Game unique ID, game file

    public GameManager(Gamegine plugin) {
        this.plugin = plugin;
    }

    public boolean registerGame(Game game) {
        String id = game.getUniqueId();
        if (!registeredGames.containsKey(id)) {
            registeredGames.put(id, game);
            try {
                pluginToGames.get(game.getOwner().getName()).add(game.getName());
            } catch (NullPointerException ex) {
                pluginToGames.put(game.getOwner().getName(), new ArrayList<String>(Arrays.asList(game.getName())));
            }
            gamesToPlugins.put(game.getName(), game.getOwner().getName());
            LogHelper.INFO(plugin, "Registered game: '" + id + "'");

            for (Class<? extends GgSign> signType : game.getSignTypes()) {
                plugin.getSignManager().registerSignType(signType);
            }
            int value = plugin.getSignManager().reloadSigns(game);
            if (value != -1) {
                LogHelper.INFO(plugin, "Loaded " + value + " signs for game: '" + id + "'");
            }
            return true;
        }
        LogHelper.WARNING(plugin, "Unable to register game: '" + id + "' -> game already registered.");
        return false;
    }

    public YamlFileManager getFile(String gameId) {
        if (!gameFiles.containsKey(gameId)) {
            YamlFileManager newFile = new YamlFileManager(Bukkit.getPluginManager().getPlugin(gameId.split(":")[0]).getDataFolder() + File.separator + "GameConfig_" + gameId.split(":")[1] + ".yml");
            gameFiles.put(gameId, newFile);
        }
        return gameFiles.get(gameId);
    }

    public Game getGame(String gameId) {
        return registeredGames.get(gameId);
    }

    public GameInstance getGameInstance(String gameInstanceId) {
        return getGame(gameInstanceId.split(":")[0]).getGameInstance(gameInstanceId.split(":")[1]);
    }

}