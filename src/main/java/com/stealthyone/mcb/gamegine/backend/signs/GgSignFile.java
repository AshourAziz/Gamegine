package com.stealthyone.mcb.gamegine.backend.signs;

import com.stealthyone.mcb.gamegine.Gamegine;
import com.stealthyone.mcb.gamegine.backend.games.Game;
import com.stealthyone.mcb.stbukkitlib.lib.plugin.LogHelper;
import com.stealthyone.mcb.stbukkitlib.lib.storage.YamlFileManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class GgSignFile extends YamlFileManager {

    private Game owner;
    private Map<String, GgSign> loadedSigns = new HashMap<>(); //Sign ID, sign
    private Map<String, Location> signLocations = new HashMap<>(); //Sign ID, Location

    public GgSignFile(Game owner, String filePath) {
        super(filePath);
        this.owner = owner;
    }

    public GgSignFile(Game owner, File file) {
        super(file);
        this.owner = owner;
    }

    public int reloadSigns() {
        loadedSigns.clear();
        reloadConfig();

        SignManager signManager = Gamegine.getInstance().getSignManager();

        ConfigurationSection signSec = getConfig().getConfigurationSection("signs");
        for (String id : signSec.getKeys(false)) {
            ConfigurationSection newSec = signSec.getConfigurationSection(id);
            String typeName = newSec.getString("type");
            Class<? extends GgSign> type = signManager.getSignType(typeName);
            if (type == null) {
                LogHelper.WARNING(Gamegine.getInstance(), "Unable to load sign for game: '" + owner.getUniqueId() + "' -> invalid type: '" + typeName + "'");
            } else {
                try {
                    GgSign newSign = type.newInstance();
                    newSign.init(owner, newSec);
                    LogHelper.DEBUG(Gamegine.getInstance(), "Loaded sign for game: '" + owner.getUniqueId() + "' with ID: " + id);
                } catch (Exception ex) {
                    LogHelper.WARNING(Gamegine.getInstance(), "Error loading sign with type: '" + typeName + "' (" + ex.getMessage() + ")");
                }
            }
        }

        ConfigurationSection locSec = getConfig().getConfigurationSection("locations");
        for (String worldName : locSec.getKeys(false)) {
            for (String x : locSec.getConfigurationSection(worldName).getKeys(false)) {
                for (String z : locSec.getConfigurationSection(worldName + "." + x).getKeys(false)) {
                    for (String y : locSec.getConfigurationSection(worldName + "." + x + "." + z).getKeys(false)) {
                        String path = worldName + "." + x + "." + z + "." + y;
                        String id = locSec.getString(path);
                        if (!loadedSigns.containsKey(id)) {
                            locSec.set(path, null);
                        } else {
                            signLocations.put(id, new Location(Bukkit.getWorld(worldName), Integer.parseInt(x), Integer.parseInt(y), Integer.parseInt(z)));
                        }
                    }
                }
            }
        }
        return loadedSigns.size();
    }

    public Location getLocation(GgSign sign) {
        return signLocations.get(sign.getId());
    }

}