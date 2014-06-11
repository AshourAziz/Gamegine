package com.stealthyone.mcb.gamegine.signs;

import com.stealthyone.mcb.gamegine.GameginePlugin;
import com.stealthyone.mcb.gamegine.api.signs.GameSign;
import com.stealthyone.mcb.gamegine.api.signs.SignManager;
import org.apache.commons.lang.Validate;
import org.bukkit.Location;

import java.util.HashMap;
import java.util.Map;

public class GgSignManager implements SignManager {

    private GameginePlugin plugin;

    private Map<Location, GameSign> registeredSigns = new HashMap<>();

    public GgSignManager(GameginePlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean registerSign(GameSign sign) {
        Validate.notNull(sign, "Sign cannot be null.");
        Validate.notNull(sign.getLocation(), "Sign location cannot be null.");

        Location loc = sign.getLocation();
        if (registeredSigns.containsKey(loc)) {
            return false;
        }
        registeredSigns.put(loc, sign);
        return true;
    }

    @Override
    public boolean unregisterSign(Location location) {
        Validate.notNull(location, "Location cannot be null.");

        return registeredSigns.remove(location) != null;
    }

    @Override
    public GameSign getSign(Location location) {
        return registeredSigns.get(location);
    }

}