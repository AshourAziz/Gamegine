package com.stealthyone.mcb.gameginecore.backend.games;

import com.stealthyone.mcb.stbukkitlib.lib.storage.YamlFileManager;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;

public class GgGameFile extends YamlFileManager {

    private Game owner;

    /* Configuration values */
    private boolean gameEnabled = true;

    public GgGameFile(Game game) {
        super(new File(game.getOwner().getDataFolder() + File.separator + "GameConfig_" + game.getName() + ".yml"));
        this.owner = game;
    }

    public Game getOwner() {
        return owner;
    }

    @Override
    public void reloadConfig() {
        super.reloadConfig();

        FileConfiguration config = getConfig();

        /* Load configuration values */
        gameEnabled = config.getBoolean("gameEnabled", gameEnabled);
        loadConfiguration(config);
    }

    @Override
    public void saveFile() {
        FileConfiguration config = getConfig();

        /* Save configuration values */
        config.set("gameEnabled", gameEnabled);
        saveConfiguration(config);

        super.saveFile();
    }

    public void saveConfiguration(FileConfiguration config) { };

    public void loadConfiguration(FileConfiguration config) { };

    public boolean isGameEnabled() {
        return gameEnabled;
    }

    public void setGameEnabled(boolean gameEnabled) {
        if (this.gameEnabled != gameEnabled) {
            this.gameEnabled = gameEnabled;
            if (gameEnabled) {
                //TODO: Enable arenas
            } else {
                //TODO: Disable all running games
            }
        }
    }

}