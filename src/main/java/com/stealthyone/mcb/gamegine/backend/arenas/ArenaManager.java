package com.stealthyone.mcb.gamegine.backend.arenas;

import com.stealthyone.mcb.gamegine.Gamegine;
import com.stealthyone.mcb.gamegine.backend.games.Game;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Logger;

public class ArenaManager {

    private Gamegine plugin;

    private Map<String, Map<Integer, Arena>> gameArenas = new HashMap<>(); //Game ID, Arena ID, Arena

    public ArenaManager(Gamegine plugin) {
        Logger log = Bukkit.getLogger();

        this.plugin = plugin;

        /* Check config */
        //log.log(Level.INFO, "");
        //log.log(Level.INFO, "-----Gamegine Configuration: Arenas-----");
    }

    public void save() {
        for (Entry<String, Map<Integer, Arena>> entry : gameArenas.entrySet()) {
            for (Arena arena : entry.getValue().values()) {
                arena.getArenaConfig().saveFile();
            }
        }
    }

    public int reloadArenas(Game game) {
        String gameId = game.getUniqueId();

        if (!gameArenas.containsKey(gameId)) {
            gameArenas.put(gameId, new HashMap<Integer, Arena>());
        }
        Map<Integer, Arena> arenas = gameArenas.get(gameId);
        arenas.clear();

        File arenaDir = new File(game.getDataDir() + File.separator + "arenas");
        arenaDir.mkdir();

        for (File file : arenaDir.listFiles()) {
            if (file.getName().matches("..yml")) {
                game.loadArena(file);
            }
        }
        return arenas.size();
    }

    public Arena getArena(String arenaUniqueId) {
        Validate.notNull(arenaUniqueId);
        String[] split = arenaUniqueId.split("\\@");
        if (split.length > 1) {
            try {
                return gameArenas.get(split[0]).get(Integer.parseInt(split[1]));
            } catch (NullPointerException ex) {
                return null;
            } catch (NumberFormatException ex) {

            }
        }
        throw new IllegalArgumentException("Invalid arena ID: " + arenaUniqueId);
    }

    public Map<Integer, Arena> getAllArenas(Game game) {
        return gameArenas.get(game.getUniqueId());
    }

}