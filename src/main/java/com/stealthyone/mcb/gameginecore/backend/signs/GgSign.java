package com.stealthyone.mcb.gameginecore.backend.signs;

import com.stealthyone.mcb.gameginecore.Gamegine;
import com.stealthyone.mcb.gameginecore.backend.games.Game;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.List;

public abstract class GgSign {

    private Game owner;
    private String id;
    private ConfigurationSection config;

    public final void init(Game owner, ConfigurationSection config) {
        this.owner = owner;
        this.config = config;
        this.id = config.getName();
    }

    public final ConfigurationSection getConfig() {
        return config;
    }

    public final String getId() {
        return id;
    }

    public final Location getLocation() {
        return Gamegine.getInstance().getSignManager().getSignFile(owner).getLocation(this);
    }

    public abstract void updateSign(List<String> lines);
    public abstract void onPlayerInteract(PlayerInteractEvent e);

}