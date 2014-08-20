package com.stealthyone.mcb.gamegine.backend.signs;

import com.stealthyone.mcb.gamegine.GameginePlugin;
import com.stealthyone.mcb.gamegine.api.signs.ActiveGSign;
import com.stealthyone.mcb.gamegine.api.signs.modules.GSignInteractModule;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

@RequiredArgsConstructor
public class SignListener implements Listener {

    private final GameginePlugin plugin;

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {
        Block block = e.getClickedBlock();
        if (block == null || (!block.getType().equals(Material.WALL_SIGN) && !block.getType().equals(Material.SIGN_POST))) {
            return;
        }

        ActiveGSign gameSign = plugin.getSignManager().getSign(block.getLocation());
        if (gameSign != null && gameSign.getType() instanceof GSignInteractModule) {
            e.setCancelled(true);
            ((GSignInteractModule) gameSign.getType()).playerInteract(e, gameSign);
        }
    }

}