package com.stealthyone.mcb.gameginecore.backend.selections;

import com.stealthyone.mcb.gameginecore.Gamegine;
import com.stealthyone.mcb.gameginecore.backend.players.GgPlayerFile;
import com.stealthyone.mcb.gameginecore.backend.players.PlayerManager;
import com.stealthyone.mcb.gameginecore.config.ConfigHelper;
import com.stealthyone.mcb.stbukkitlib.api.Stbl;
import com.stealthyone.mcb.stbukkitlib.lib.utils.MaterialUtils;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SelectionManager {

    private Gamegine plugin;

    private ItemStack selectionWand;

    private Map<String, Selection> selections = new HashMap<>(); //Player uuid, SimpleCuboidSelection

    public SelectionManager(Gamegine plugin) {
        this.plugin = plugin;

        Logger log = Bukkit.getLogger();

        /* Check config values */
        log.log(Level.INFO, "");
        log.log(Level.INFO, "-----Gamegine Configuration: Selections-----");
        if (ConfigHelper.SELECTIONS_USE_WORLDEDIT.get()) {
            //Check dependencies
            log.log(Level.INFO, "Using WorldEdit for selection wand.");
        } else {
            log.log(Level.INFO, "Using built-in wand for selections.");
        }

        String rawWand = ConfigHelper.SELECTIONS_WAND.get();
        if (rawWand == null) {
            log.log(Level.INFO, "No wand specified in config, wand DISABLED.");
        } else {
            try {
                selectionWand = MaterialUtils.rawMaterialToItem(rawWand, 1);
            } catch (IllegalArgumentException ex) {
                log.log(Level.INFO, "ERROR: Error loading wand type from config. (" + ex.getMessage() + ") Wand DISABLED.");
            }
        }
    }

    public void save() {
        PlayerManager playerManager = plugin.getPlayerManager();

        for (Entry<String, Selection> plSel : selections.entrySet()) {
            GgPlayerFile plFile = playerManager.getFile(plSel.getKey(), false);
            if (plFile == null) continue;

            ConfigurationSection selSec = plFile.getConfig().createSection("selection");
            plSel.getValue().save(selSec);
        }
    }

    public void loadSelection(String playerUuid) {
        GgPlayerFile plFile = plugin.getPlayerManager().getFile(playerUuid, false);
        if (plFile != null) {
            FileConfiguration plConf = plFile.getConfig();
            if (plConf.isSet("selection")) {
                selections.put(playerUuid, new Selection(plConf.getConfigurationSection("selection")));
            }
        }
    }

    public ItemStack getSelectionWand() {
        return selectionWand;
    }

    public Selection getPlayerSelection(String playerName) {
        return selections.get(Stbl.getUuidManager().getUuid(playerName));
    }

    public Selection getPlayerSelection(Player player) {
        String uuid = player.getUniqueId().toString();
        if (!selections.containsKey(uuid)) {
            selections.put(uuid, new Selection());
        }
        return selections.get(uuid);
    }

}