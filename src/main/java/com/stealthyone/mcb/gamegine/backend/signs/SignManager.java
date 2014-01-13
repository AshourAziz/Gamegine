package com.stealthyone.mcb.gamegine.backend.signs;

import com.stealthyone.mcb.gamegine.Gamegine;
import com.stealthyone.mcb.gamegine.backend.games.Game;
import com.stealthyone.mcb.gamegine.config.ConfigBoolean;
import com.stealthyone.mcb.stbukkitlib.lib.plugin.LogHelper;
import org.bukkit.Bukkit;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SignManager {

    private Gamegine plugin;

    private boolean enabled;

    private Map<String, GgSignFile> gameSignFiles = new HashMap<>(); //Game ID, sign file
    private Map<String, String> signIndex = new HashMap<>(); //location string, corresponding data location

    private Map<String, Class<? extends GgSign>> signTypes = new HashMap<>();

    public SignManager(Gamegine plugin) {
        Logger log = Bukkit.getLogger();

        this.plugin = plugin;

        /* Check config */
        log.log(Level.INFO, "");
        log.log(Level.INFO, "-----Gamegine Configuration: Signs-----");

        //Whether or not the sign part of the plugin is enabled
        enabled = ConfigBoolean.SIGNS_ENABLED.get();
        log.log(Level.INFO, "Game signs " + (enabled ? "enabled" : "DISABLED") + ".");

        if (enabled) {

        }
    }

    public boolean registerSignType(Class<? extends GgSign> clazz) {
        if (!enabled) {
            return false;
        }

        String name = clazz.getCanonicalName();
        if (!signTypes.containsKey(name)) {
            signTypes.put(name, clazz);
            LogHelper.DEBUG(plugin, "Registered sign type: " + name);
            return true;
        }
        return false;
    }

    public Class<? extends GgSign> getSignType(String name) {
        return signTypes.get(name);
    }

    public GgSignFile getSignFile(Game game) {
        GgSignFile file = gameSignFiles.get(game.getUniqueId());
        if (file == null) {
            file = new GgSignFile(game, game.getOwner().getDataFolder() + File.separator + "gamegineData" + File.separator + "GameSigns_" + game.getName() + ".yml");
            gameSignFiles.put(game.getUniqueId(), file);
        }
        return file;
    }

    public int reloadSigns(Game game) {
        if (!enabled) {
            return -1;
        }
        return getSignFile(game).reloadSigns();
    }

    public void reindexSigns() {
        signIndex.clear();
        for (GgSignFile file : gameSignFiles.values()) {

        }
    }

}