package com.stealthyone.mcb.gamegine;

import com.stealthyone.mcb.gamegine.api.Gamegine;
import com.stealthyone.mcb.gamegine.api.GamegineAPI;
import com.stealthyone.mcb.gamegine.api.hooks.plugins.defaults.HookInSigns;
import com.stealthyone.mcb.gamegine.api.hooks.plugins.defaults.HookWorldEdit;
import com.stealthyone.mcb.gamegine.api.hooks.plugins.defaults.HookWorldGuard;
import com.stealthyone.mcb.gamegine.api.logging.GamegineLogger;
import com.stealthyone.mcb.gamegine.backend.arenas.GgArenaManager;
import com.stealthyone.mcb.gamegine.backend.games.GgGameManager;
import com.stealthyone.mcb.gamegine.backend.hooks.GgHookManager;
import com.stealthyone.mcb.gamegine.backend.signs.GgSignManager;
import com.stealthyone.mcb.gamegine.backend.signs.variables.SignGameNameVar;
import com.stealthyone.mcb.gamegine.backend.signs.variables.SignPlayerCountVar;
import com.stealthyone.mcb.gamegine.commands.CmdGamegine;
import com.stealthyone.mcb.gamegine.commands.CmdGames;
import com.stealthyone.mcb.gamegine.commands.CmdSign;
import com.stealthyone.mcb.gamegine.listeners.PlayerListener;
import com.stealthyone.mcb.gamegine.players.GgPlayerManager;
import com.stealthyone.mcb.gamegine.utils.BlockLocation;
import com.stealthyone.mcb.stbukkitlib.help.HelpManager;
import com.stealthyone.mcb.stbukkitlib.messages.MessageManager;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.plugin.java.JavaPlugin;

public class GameginePlugin extends JavaPlugin implements GamegineAPI {

    static {
        ConfigurationSerialization.registerClass(BlockLocation.class);
    }

    /* Configuration */
    @Getter private boolean isDebug;
    private int autosaveInterval;

    /* Misc Plugin Managers */
    @Getter private HelpManager helpManager;
    @Getter private MessageManager messageManager;

    /* Gamegine Managers */
    private GgArenaManager arenaManager;
    private GgGameManager gameManager;
    private GgHookManager hookManager;
    private GgPlayerManager playerManager;
    private GgSignManager signManager;

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
        helpManager = new HelpManager(this);
        helpManager.reload();

        // Set up default hooks.
        hookManager = new GgHookManager(this);
        hookManager.registerHook(new HookWorldEdit());
        hookManager.registerHook(new HookWorldGuard());
        hookManager.registerHook(new HookInSigns());

        messageManager = new MessageManager(this);
        messageManager.reloadMessages();

        GamegineLogger.debug("Creating Gamegine managers...");
        arenaManager = new GgArenaManager(this);
        gameManager = new GgGameManager(this);
        playerManager = new GgPlayerManager(this);
        signManager = new GgSignManager(this);
        signManager.load();

        GamegineLogger.debug("Loading defaults...");
        signManager.registerVariable(new SignGameNameVar());
        signManager.registerVariable(new SignPlayerCountVar());

        GamegineLogger.debug("Registering listeners...");
        Bukkit.getPluginManager().registerEvents(new PlayerListener(this), this);
        Bukkit.getPluginManager().registerEvents(signManager, this);

        GamegineLogger.debug("Registering commands...");
        getCommand("gamegine").setExecutor(new CmdGamegine(this));
        getCommand("games").setExecutor(new CmdGames(this));
        getCommand("gameginesign").setExecutor(new CmdSign(this));

        GamegineLogger.info(String.format("Gamegine v%s by Stealth2800 successfully ENABLED.", getVersion()));
    }

    @Override
    public void onDisable() {
        GamegineLogger.info(String.format("Gamegine v%s by Stealth2800 successfully DISABLED.", getVersion()));
        Gamegine.setInstance(null);
    }

    @Override
    public void reloadConfig() {
        super.reloadConfig();

        FileConfiguration config = getConfig();

        this.autosaveInterval = config.getInt("Autosave interval", 10);
        this.isDebug = config.getBoolean("Debug", false);
    }

    public void reloadAll() {
        reloadConfig();

        helpManager.reload();
        messageManager.reloadMessages();
        signManager.reload();
    }

    public void saveAll() {
        saveConfig();
    }

    @Override
    public JavaPlugin getPlugin() {
        return this;
    }

    @Override
    public String getVersion() {
        return getDescription().getVersion();
    }

    @Override
    public GgArenaManager getArenaManager() {
        return arenaManager;
    }

    @Override
    public GgGameManager getGameManager() {
        return gameManager;
    }

    @Override
    public GgHookManager getHookManager() {
        return hookManager;
    }

    @Override
    public GgPlayerManager getPlayerManager() {
        return playerManager;
    }

    @Override
    public GgSignManager getSignManager() {
        return signManager;
    }

}