package com.stealthyone.mcb.gameginecore.config;

public class ConfigHelper {

    public final static ConfigLong COOLDOWNS_DECREMENT_FREQ = new ConfigLong("Cooldowns.Decrement frequency");
    public final static ConfigBoolean COOLDOWNS_ENABLED = new ConfigBoolean("Cooldowns.Enabled");

    public final static ConfigInteger PLAYERS_FILES_INACTIVE_CHECK = new ConfigInteger("Players.Files.Inactive check");
    public final static ConfigInteger PLAYERS_FILES_INACTIVE_TIME = new ConfigInteger("Players.Files.Inactive time");

    public final static ConfigBoolean SELECTIONS_USE_WORLDEDIT = new ConfigBoolean("Cooldowns.Use WorldEdit", false);
    public final static ConfigString SELECTIONS_WAND = new ConfigString("Selections.Wand item");

    public final static ConfigBoolean SIGNS_ENABLED = new ConfigBoolean("Signs.Enabled", true);
    public final static ConfigLong SIGNS_INTERACT_DELAY = new ConfigLong("Signs.Interact delay", 5L);

}