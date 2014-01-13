package com.stealthyone.mcb.gamegine.config;

import com.stealthyone.mcb.gamegine.Gamegine;

public enum ConfigInteger {

    PLAYERS_FILES_INACTIVE_CHECK("Players.Files.Inactive check"),
    PLAYERS_FILES_INACTIVE_TIME("Players.Files.Inactive time");

    private String path;
    private int defValue;

    private ConfigInteger(String path) {
        this(path, 0);
    }

    private ConfigInteger(String path, int defValue) {
        this.path = path;
        this.defValue = defValue;
    }

    public int get() {
        return Gamegine.getInstance().getConfig().getInt(path, defValue);
    }

}