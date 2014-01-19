package com.stealthyone.mcb.gamegine;

import com.stealthyone.mcb.gamegine.backend.cooldowns.CooldownManager;
import com.stealthyone.mcb.gamegine.backend.games.GameManager;
import com.stealthyone.mcb.gamegine.backend.players.PlayerManager;
import com.stealthyone.mcb.gamegine.backend.selections.SelectionManager;
import com.stealthyone.mcb.gamegine.backend.signs.SignManager;
import com.stealthyone.mcb.gamegine.commands.CmdCooldown;
import com.stealthyone.mcb.gamegine.commands.CmdGamegine;
import com.stealthyone.mcb.gamegine.commands.CmdPoints;
import com.stealthyone.mcb.gamegine.listeners.PlayerListener;
import com.stealthyone.mcb.stbukkitlib.StBukkitLib;
import com.stealthyone.mcb.stbukkitlib.lib.messages.MessageManager;
import com.stealthyone.mcb.stbukkitlib.lib.plugin.LogHelper;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Gamegine extends JavaPlugin {

    private static Gamegine instance;

    public static Gamegine getInstance() {
        return instance;
    }

    private MessageManager messageManager;

    private CooldownManager cooldownManager;
    private GameManager gameManager;
    private PlayerManager playerManager;
    private SelectionManager selectionManager;
    private SignManager signManager;

    @Override
    public void onLoad() {
        instance = this;
        getDataFolder().mkdir();
    }

    @Override
    public void onEnable() {
        Logger log = Bukkit.getLogger();
        try {
            log.log(Level.INFO, String.format("==========Gamegine v%s by Stealth2800 LOADING==========", getDescription().getVersion()));
            log.log(Level.INFO, "This information should be sent to Stealth2800 if you encounter any bugs.");
            log.log(Level.INFO, "CraftBukkit v" + Bukkit.getVersion());
            log.log(Level.INFO, "Bukkit v" + Bukkit.getBukkitVersion());
            log.log(Level.INFO, "StBukkitLib v" + StBukkitLib.getInstance().getDescription().getVersion());
            log.log(Level.INFO, "Installed plugins: " + Arrays.toString(Bukkit.getPluginManager().getPlugins()));

            log.log(Level.INFO, "");
            log.log(Level.INFO, "Loading configuration...");
            saveDefaultConfig();
            getConfig().options().copyDefaults(false);
            saveConfig();

            log.log(Level.INFO, "");
            log.log(Level.INFO, "Instantiating main plugin components...");
            messageManager = new MessageManager(this);
            gameManager = new GameManager(this);
            playerManager = new PlayerManager(this);
            cooldownManager = new CooldownManager(this);
            selectionManager = new SelectionManager(this);
            signManager = new SignManager(this);

            //listeners
            Bukkit.getPluginManager().registerEvents(new PlayerListener(this), this);

            //commands
            getCommand("cooldown").setExecutor(new CmdCooldown(this));
            getCommand("gamegine").setExecutor(new CmdGamegine(this));
            getCommand("points").setExecutor(new CmdPoints(this));

            log.log(Level.INFO, "");
            log.log(Level.INFO, "==========Gamegine successfully ENABLED==========");
        } catch (Exception ex) {
            LogHelper.SEVERE(this, "Error encountered while enabling Gamegine:");
            ex.printStackTrace();
        }
    }

    @Override
    public void onDisable() {
        Logger log = Bukkit.getLogger();
        log.log(Level.INFO, "==========Disabling Gamegine==========");
        log.log(Level.INFO, "Saving data...");
        saveAll();
        log.log(Level.INFO, "Data saved.");
        instance = null;

        log.log(Level.INFO, "==========Gamegine successfully DISABLED==========");
    }

    public void saveAll() {
        cooldownManager.save();
        selectionManager.save();
        playerManager.save();
    }

    public void reloadAll() {
        reloadConfig();
        messageManager.reloadMessages();
        //gameManager
        //playerManager
        cooldownManager.reload();
        //selectionManager
        //signManager
    }

    public MessageManager getMessageManager() {
        return messageManager;
    }

    public CooldownManager getCooldownManager() {
        return cooldownManager;
    }

    public GameManager getGameManager() {
        return gameManager;
    }

    public PlayerManager getPlayerManager() {
        return playerManager;
    }

    public SelectionManager getSelectionManager() {
        return selectionManager;
    }

    public SignManager getSignManager() {
        return signManager;
    }

}