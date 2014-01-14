package com.stealthyone.mcb.gamegine.backend.cooldowns;

import com.stealthyone.mcb.gamegine.Gamegine;
import com.stealthyone.mcb.gamegine.config.ConfigHelper;
import org.bukkit.Bukkit;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CooldownManager {

    private Gamegine plugin;

    private boolean enabled;
    private long decrementTicks;

    private int schedulerId = -1;
    private Map<String, Map<String, Cooldown>> cooldowns = new HashMap<>(); //player uuid, cooldown name, cooldown object

    public CooldownManager(Gamegine plugin) {
        this.plugin = plugin;
        reload();
    }

    public void reload() {
        Logger log = Bukkit.getLogger();

        if (schedulerId != -1) {
            Bukkit.getScheduler().cancelTask(schedulerId);
        }

        log.log(Level.INFO, "");
        log.log(Level.INFO, "-----Gamegine Configuration: Cooldowns-----");
        if (ConfigHelper.COOLDOWNS_ENABLED.get()) {
            log.log(Level.INFO, "Cooldowns ENABLED.");
            enabled = true;
        } else {
            log.log(Level.INFO, "Cooldowns DISABLED.");
            enabled = false;
            return;
        }
        decrementTicks = ConfigHelper.COOLDOWNS_DECREMENT_FREQ.get();
        log.log(Level.INFO, "Decrementing cooldowns every " + decrementTicks + " ticks.");

        log.log(Level.INFO, "Loaded " + reloadCooldowns() + " cooldowns.");
    }

    private int reloadCooldowns() {
        cooldowns.clear();
        //TODO: Reload cooldowns from player files
        return 0;
    }

    private void scheduleCooldownRunnable() {
        if (schedulerId == -1) {
            schedulerId = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
                @Override
                public void run() {
                    decrementCooldowns();
                }
            }, decrementTicks, decrementTicks);
        }
    }

    private void decrementCooldowns() {
        for (Map<String, Cooldown> cdMap : cooldowns.values()) {
            for (Cooldown cooldown : cdMap.values()) {
                cooldown.decrement(decrementTicks);
            }
        }
    }

    public boolean registerCooldown(Cooldown cooldown) {
        Map<String, Cooldown> plCooldowns = cooldowns.get(cooldown.getPlayerName().toLowerCase());
        if (plCooldowns == null) {
            cooldowns.put(cooldown.getPlayerName().toLowerCase(), new HashMap<String, Cooldown>());
            plCooldowns = cooldowns.get(cooldown.getPlayerName().toLowerCase());
        }

        if (!plCooldowns.containsKey(cooldown.getCooldownName().toLowerCase())) {
            plCooldowns.put(cooldown.getCooldownName().toLowerCase(), cooldown);
            return true;
        } else {
            return false;
        }
    }

}