package com.stealthyone.mcb.gameginecore.backend.signs;

import com.stealthyone.mcb.gameginecore.Gamegine;
import com.stealthyone.mcb.gameginecore.backend.cooldowns.SignInteractCooldown;
import com.stealthyone.mcb.gameginecore.backend.games.Game;
import com.stealthyone.mcb.gameginecore.config.ConfigHelper;
import com.stealthyone.mcb.stbukkitlib.lib.plugin.LogHelper;
import com.stealthyone.mcb.stbukkitlib.lib.utils.LocationUtils;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.event.player.PlayerInteractEvent;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SignManager {

    private Gamegine plugin;

    private boolean enabled;
    private long signInteractDelay;

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
        enabled = ConfigHelper.SIGNS_ENABLED.get();
        log.log(Level.INFO, "Game signs " + (enabled ? "ENABLED" : "DISABLED") + ".");
        if (enabled) {
            signInteractDelay = ConfigHelper.SIGNS_INTERACT_DELAY.get();
            log.log(Level.INFO, "Limiting sign interactions to 1 time every " + signInteractDelay + " ticks.");
        }
    }

    public long getSignInteractDelay() {
        return signInteractDelay;
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
            file = new GgSignFile(game, game.getOwner().getDataFolder() + File.separator + "GamegineData" + File.separator + game.getName() + File.separator + "signs.yml");
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
            for (Entry<String, Location> entry : file.getAllLocations().entrySet()) {
                Location loc = entry.getValue();
                signIndex.put(loc.getWorld().getName() + "," + loc.getBlockX() + "," + loc.getBlockY() + "," + loc.getBlockZ(), file.getOwner().getUniqueId() + ":" + entry.getKey());
            }
        }
    }

    public GgSign getSign(Location location) {
        Validate.notNull(location, "Location cannot be null");

        String rawId = signIndex.get(LocationUtils.locationToString(location, true));
        if (rawId == null) {
            return null;
        }
        String[] split = rawId.split("$");
        return gameSignFiles.get(split[0]).getSign(split[1]);
    }

    public boolean isSignRegistered(Location location) {
        return signIndex.containsKey(LocationUtils.locationToString(location, true));
    }

    public void playerSignInteract(PlayerInteractEvent e) {
        Block block = e.getClickedBlock();
        if (isSignRegistered(block.getLocation())) {
            if (!plugin.getCooldownManager().isCoolingDown(e.getPlayer(), "signInteractDelay")) {
                plugin.getCooldownManager().registerCooldown(new SignInteractCooldown(e.getPlayer()));

                getSign(block.getLocation()).onPlayerInteract(e);
                e.setCancelled(true);
            }
        }
    }

}