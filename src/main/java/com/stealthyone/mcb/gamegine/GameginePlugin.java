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
import com.stealthyone.mcb.gamegine.backend.selections.GgSelectionManager;
import com.stealthyone.mcb.gamegine.backend.signs.GgSignManager;
import com.stealthyone.mcb.gamegine.backend.signs.SignListener;
import com.stealthyone.mcb.gamegine.commands.CmdGamegine;
import com.stealthyone.mcb.gamegine.commands.CmdGames;
import com.stealthyone.mcb.gamegine.commands.CmdSelection;
import com.stealthyone.mcb.gamegine.commands.CmdSelectionCompleter;
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
    private GgSelectionManager selectionManager;
    private GgSignManager signManager;

    /* Commands */
    @Getter private CmdGames cmdGames = new CmdGames(this);

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

        selectionManager = new GgSelectionManager(this);

        signManager = new GgSignManager(this);
        signManager.load();

        GamegineLogger.debug("Loading defaults...");
        selectionManager.loadDefaults();
        signManager.loadDefaults();

        GamegineLogger.debug("Registering listeners...");
        Bukkit.getPluginManager().registerEvents(new PlayerListener(this), this);
        Bukkit.getPluginManager().registerEvents(new SignListener(this), this);

        GamegineLogger.debug("Registering commands...");
        getCommand("gamegine").setExecutor(new CmdGamegine(this));
        getCommand("gameginegames").setExecutor(cmdGames);
        getCommand("gamegineselection").setExecutor(new CmdSelection(this));
        getCommand("gamegineselection").setTabCompleter(new CmdSelectionCompleter(this));
        getCommand("gameginesigns").setExecutor(new CmdSign(this));

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
    public GgSelectionManager getSelectionManager() {
        return selectionManager;
    }

    @Override
    public GgSignManager getSignManager() {
        return signManager;
    }

}