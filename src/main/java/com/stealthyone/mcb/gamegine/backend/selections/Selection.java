package com.stealthyone.mcb.gamegine.backend.selections;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class Selection {

    private static final long serialVersionUID = -1655621435227518958L;
    private Block block1;
    private Block block2;
    private String playerName;

    public Selection(Player player) {
        this.playerName = player.getName();
    }

    public Block getBlock1() {
        return block1;
    }

    public Block getBlock2() {
        return block2;
    }

    public String getPlayerName() {
        return playerName;
    }

    public Player getPlayer() {
        return Bukkit.getPlayerExact(playerName);
    }

    public void setBlock1(Block block1) {
        this.block1 = block1;
    }

    public void setBlock2(Block block2) {
        this.block2 = block2;
    }

    public boolean areBothPointsSet() {
        return !(getBlock1() == null || getBlock2() == null);
    }

    public boolean areBlocksInDifferentWorlds() {
        return !getBlock1().getWorld().getName().equals(getBlock2().getWorld().getName());
    }

    public static SerializableSelection toSerializableSelection(Selection sel) {
        String block1 = sel.getBlock1().getLocation().getWorld().getName()
                + "," + sel.getBlock1().getLocation().getBlockX() + ","
                + sel.getBlock1().getLocation().getBlockY() + ","
                + sel.getBlock1().getLocation().getBlockZ();
        String block2 = sel.getBlock2().getLocation().getWorld().getName()
                + "," + sel.getBlock2().getLocation().getBlockX() + ","
                + sel.getBlock2().getLocation().getBlockY() + ","
                + sel.getBlock2().getLocation().getBlockZ();
        return new SerializableSelection(block1, block2);
    }

    public static Block blockFromString(String str) {
        Block block = null;
        String[] stringBlock = str.split(",");
        World world = Bukkit.getWorld(stringBlock[0]);
        block = world.getBlockAt(Integer.parseInt(stringBlock[1]),
                Integer.parseInt(stringBlock[2]),
                Integer.parseInt(stringBlock[3]));
        return block;
    }

    public static Location locationFromString(String str) {
        String[] location = str.split(",");
        return new Location(Bukkit.getWorld(location[0]),
                Double.parseDouble(location[1]),
                Double.parseDouble(location[2]),
                Double.parseDouble(location[3]));
    }

    public static String locationToString(Location loc) {
        return loc.getWorld().getName() + "," + loc.getX() + "," + loc.getY()
                + "," + loc.getZ();
    }

}