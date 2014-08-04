package com.stealthyone.mcb.gamegine.listeners;

import com.stealthyone.mcb.gamegine.GameginePlugin;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

@RequiredArgsConstructor
public class PlayerListener implements Listener {

    private final GameginePlugin plugin;

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