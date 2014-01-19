package com.stealthyone.mcb.gamegine.backend.selections;

import com.stealthyone.mcb.stbukkitlib.api.Stbl;
import com.stealthyone.mcb.stbukkitlib.lib.utils.LocationUtils;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

public class Selection {

    private Block block1;
    private Block block2;
    private String playerUuid;

    public Selection(Player player) {
        this.playerUuid = player.getUniqueId().toString();
    }

    public Selection(String playerUuid, ConfigurationSection config) {
        this.playerUuid = playerUuid;

        try {
            block1 = LocationUtils.stringToLocation(config.getString("block1")).getBlock();
        } catch (NullPointerException ex) {
            block1 = null;
        }

        try {
            block2 = LocationUtils.stringToLocation(config.getString("block2")).getBlock();
        } catch (NullPointerException ex) {
            block2 = null;
        }
    }

    public void save(ConfigurationSection config) {
        config.set("block1", LocationUtils.locationToString(block1.getLocation(), true));
        config.set("block2", LocationUtils.locationToString(block2.getLocation(), true));
    }

    public Block getBlock1() {
        return block1;
    }

    public Block getBlock2() {
        return block2;
    }

    public String getPlayerName() {
        return Stbl.getUuidManager().getName(playerUuid);
    }

    public Player getPlayer() {
        return Bukkit.getPlayerExact(getPlayerName());
    }

    public void setBlock1(Block block1) {
        this.block1 = block1;
    }

    public void setBlock2(Block block2) {
        this.block2 = block2;
    }

    public void clearSelection() {
        block1 = null;
        block2 = null;
    }

    public boolean areBothPointsSet() {
        return !(getBlock1() == null || getBlock2() == null);
    }

    public boolean areBlocksInDifferentWorlds() {
        return !getBlock1().getWorld().getName().equals(getBlock2().getWorld().getName());
    }

}