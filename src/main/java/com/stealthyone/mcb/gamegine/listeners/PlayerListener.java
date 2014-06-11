package com.stealthyone.mcb.gamegine.listeners;

import com.stealthyone.mcb.gamegine.GameginePlugin;
import com.stealthyone.mcb.gamegine.api.signs.GameSign;
import com.stealthyone.mcb.gamegine.api.signs.GamegineSign;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

public class PlayerListener implements Listener {

    private GameginePlugin plugin;

    public PlayerListener(GameginePlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {
        Block block = e.getClickedBlock();
        if (block == null || (!block.getType().equals(Material.WALL_SIGN) && !block.getType().equals(Material.SIGN_POST))) {
            return;
        }
        GameSign gameSign = plugin.getSignManager().getSign(block.getLocation());
        if (gameSign instanceof GamegineSign) {
            ((GamegineSign) gameSign).playerInteract(e);
        }
    }

}