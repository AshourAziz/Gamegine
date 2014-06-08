package com.stealthyone.mcb.gamegine;

import com.stealthyone.mcb.gamegine.api.Gamegine;
import com.stealthyone.mcb.gamegine.api.GamegineAPI;
import com.stealthyone.mcb.gamegine.api.logging.GamegineLogger;
import com.stealthyone.mcb.gamegine.commands.CmdGamegine;
import com.stealthyone.mcb.gamegine.commands.CmdGames;
import com.stealthyone.mcb.gamegine.games.GgGameManager;
import com.stealthyone.mcb.gamegine.players.GgPlayerManager;
import com.stealthyone.mcb.stbukkitlib.messages.MessageManager;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class GameginePlugin extends JavaPlugin implements GamegineAPI {

    /* Configuration */
    private boolean isDebug;
    private int autosaveInterval;

    /* Misc Plugin Managers */
    private MessageManager messageManager;

    /* Gamegine Managers */
    private GgGameManager gameManager;
    private GgPlayerManager playerManager;

    @Override
    public void onLoad() {
        Gamegine.setInstance(this);
        getDataFolder().mkdir();
    }

    @Override
    public void onEnable() {
        GamegineLogger.debug("Checking default config.yml...");
        saveDefaultConfig();
        getConfig().options().copyDefaults(false);
        saveConfig();

        GamegineLogger.debug("Setting up plugin managers...");
        messageManager = new MessageManager(this);
        messageManager.reloadMessages();

        GamegineLogger.debug("Creating Gamegine managers...");
        gameManager = new GgGameManager(this);
        playerManager = new GgPlayerManager(this);

        GamegineLogger.debug("Registering commands...");
        getCommand("gamegine").setExecutor(new CmdGamegine(this));
        getCommand("games").setExecutor(new CmdGames(this));

        GamegineLogger.info(String.format("Gamegine v%s by Stealth2800 successfully ENABLED.", getVersion()));
    }

    @Override
    public void onDisable() {
        GamegineLogger.info(String.format("Gamegine v%s by Stealth2800 successfully DISABLED.", getVersion()));
    }

    @Override
    public void reloadConfig() {
        super.reloadConfig();

        FileConfiguration config = getConfig();

        this.autosaveInterval = config.getInt("Autosave interval", 10);
        this.isDebug = config.getBoolean("Debug", false);
    }

    @Override
    public String getVersion() {
        return getDescription().getVersion();
    }

    @Override
    public boolean isDebug() {
        return isDebug;
    }

    @Override
    public GgGameManager getGameManager() {
        return gameManager;
    }

    public MessageManager getMessageManager() {
        return messageManager;
    }

    @Override
    public GgPlayerManager getPlayerManager() {
        return playerManager;
    }

}