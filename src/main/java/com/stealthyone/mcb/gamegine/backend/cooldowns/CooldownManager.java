package com.stealthyone.mcb.gamegine.backend.cooldowns;

import com.stealthyone.mcb.gamegine.Gamegine;
import com.stealthyone.mcb.gamegine.backend.players.GgPlayerFile;
import com.stealthyone.mcb.gamegine.backend.players.PlayerManager;
import com.stealthyone.mcb.gamegine.config.ConfigHelper;
import com.stealthyone.mcb.stbukkitlib.api.Stbl;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
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

    public boolean isEnabled() {
        return enabled;
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
        scheduleCooldownRunnable();
    }

    public void save() {
        if (!enabled) {
            return;
        }

        PlayerManager playerManager = plugin.getPlayerManager();

        for (Entry<String, Map<String, Cooldown>> plCooldowns : cooldowns.entrySet()) {
            GgPlayerFile plFile = playerManager.getFile(plCooldowns.getKey(), false);
            if (plFile == null) continue;

            ConfigurationSection cdSec = plFile.getConfig().createSection("cooldowns");
            for (Entry<String, Cooldown> cdEntry : plCooldowns.getValue().entrySet()) {
                cdEntry.getValue().save(cdSec.createSection(cdEntry.getKey()));
            }
        }
    }

    public void loadCooldowns(String playerUuid) {
        GgPlayerFile file = plugin.getPlayerManager().getFile(playerUuid, false);
        if (file != null) {
            ConfigurationSection cdSec = file.getConfig().getConfigurationSection("cooldowns");
            for (String key : cdSec.getKeys(false)) {
                registerCooldown(new Cooldown(cdSec.getConfigurationSection(key)));
            }
        }
    }

    private void scheduleCooldownRunnable() {
        if (enabled && schedulerId == -1) {
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
        if (!enabled) {
            return false;
        }

        Map<String, Cooldown> plCooldowns = cooldowns.get(cooldown.getPlayerUuid().toLowerCase());
        if (plCooldowns == null) {
            cooldowns.put(cooldown.getPlayerUuid().toLowerCase(), new LinkedHashMap<String, Cooldown>());
            plCooldowns = cooldowns.get(cooldown.getPlayerUuid().toLowerCase());
        }

        if (!plCooldowns.containsKey(cooldown.getCooldownName().toLowerCase())) {
            plCooldowns.put(cooldown.getCooldownName().toLowerCase(), cooldown);
            return true;
        } else {
            return false;
        }
    }

    public boolean unregisterCooldown(Cooldown cooldown) {
        if (!enabled) {
            return false;
        }

        Map<String, Cooldown> plCooldowns = cooldowns.get(cooldown.getPlayerUuid());
        if (plCooldowns.containsKey(cooldown.getCooldownName())) {
            plCooldowns.remove(cooldown.getCooldownName());
            return true;
        }
        return false;
    }

    public boolean isCoolingDown(Player player, String cooldownName) {
        if (!enabled) {
            return false;
        } else {
            try {
                return cooldowns.get(player.getUniqueId().toString()).get(cooldownName).check();
            } catch (NullPointerException ex) {
                return false;
            }
        }
    }

    public Map<String, Cooldown> getCooldowns(Player player) {
        return cooldowns.get(player.getUniqueId().toString());
    }

    public Map<String, Cooldown> getCooldowns(String playerName) {
        return cooldowns.get(Stbl.getUuidManager().getUuid(playerName));
    }

}