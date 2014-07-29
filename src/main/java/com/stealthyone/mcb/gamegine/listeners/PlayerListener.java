package com.stealthyone.mcb.gamegine.listeners;

import com.stealthyone.mcb.gamegine.GameginePlugin;
import com.stealthyone.mcb.gamegine.api.signs.GameSign;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

@RequiredArgsConstructor
public class PlayerListener implements Listener {

    private final GameginePlugin plugin;

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
    public void onPlayerQuit(PlayerQuitEvent e) {
        playerLeave(e.getPlayer());
    }

    @EventHandler
    public void onPlayerKick(PlayerKickEvent e) {
        playerLeave(e.getPlayer());
    }

    private void playerLeave(Player player) {
        plugin.getPlayerManager().playerDisconnect(player);
    }

}