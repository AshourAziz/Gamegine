package com.stealthyone.mcb.gamegine.listeners;

import com.stealthyone.mcb.gamegine.Gamegine;
import com.stealthyone.mcb.stbukkitlib.lib.utils.SignUtils;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

public class PlayerListener implements Listener {

    private Gamegine plugin;

    public PlayerListener(Gamegine plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {
        if (e.getClickedBlock() != null && SignUtils.isBlockSign(e.getClickedBlock())) {
            plugin.getSignManager().playerSignInteract(e);
        }
    }

}