package com.stealthyone.mcb.gamegine.backend.signs;

import com.stealthyone.mcb.gamegine.GameginePlugin;
import com.stealthyone.mcb.gamegine.api.signs.GameSign;
import com.stealthyone.mcb.gamegine.api.signs.SignManager;
import org.apache.commons.lang.Validate;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.HashMap;
import java.util.Map;

public class GgSignManager implements Listener, SignManager {

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

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {
        Block block = e.getClickedBlock();
        if (block == null || (!block.getType().equals(Material.WALL_SIGN) && !block.getType().equals(Material.SIGN_POST))) {
            return;
        }

        GameSign gameSign = plugin.getSignManager().getSign(block.getLocation());
        if (gameSign != null) {
            gameSign.playerInteract(e);
        }
    }

    @EventHandler
    public void onSignChange(SignChangeEvent e) {

    }

}