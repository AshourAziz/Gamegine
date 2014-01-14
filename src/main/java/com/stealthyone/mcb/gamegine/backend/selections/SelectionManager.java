package com.stealthyone.mcb.gamegine.backend.selections;

import com.stealthyone.mcb.gamegine.Gamegine;
import com.stealthyone.mcb.gamegine.config.ConfigBoolean;
import com.stealthyone.mcb.gamegine.config.ConfigString;
import com.stealthyone.mcb.stbukkitlib.lib.utils.MaterialUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SelectionManager {

    private Gamegine plugin;

    private ItemStack selectionWand;

    private Map<String, Selection> selections = new HashMap<>(); //Player name, Selection

    public SelectionManager(Gamegine plugin) {
        this.plugin = plugin;

        Logger log = Bukkit.getLogger();

        /* Check config values */
        log.log(Level.INFO, "");
        log.log(Level.INFO, "-----Gamegine Configuration: Selections-----");
        if (ConfigBoolean.SELECTIONS_USE_WORLDEDIT.get()) {
            //Check dependencies
            log.log(Level.INFO, "Using WorldEdit for selection wand.");
        } else {
            log.log(Level.INFO, "Using built-in wand for selections.");
        }

        String rawWand = ConfigString.SELECTIONS_WAND.get();
        if (rawWand == null) {
            log.log(Level.INFO, "No wand specified in config, wand disabled.");
        } else {
            try {
                selectionWand = MaterialUtils.rawMaterialToItem(rawWand, 1);
            } catch (IllegalArgumentException ex) {
                log.log(Level.INFO, "ERROR: Error loading wand type from config. (" + ex.getMessage() + ") Wand disabled.");
            }
        }
    }

    public ItemStack getSelectionWand() {
        return selectionWand;
    }

    public Selection getPlayerSelection(String playerName) {
        return selections.get(playerName.toLowerCase());
    }

    public Selection getPlayerSelection(Player player) {
        String playerName = player.getName().toLowerCase();
        if (!selections.containsKey(playerName)) {
            selections.put(playerName, new Selection(player));
        }
        return selections.get(playerName);
    }

}