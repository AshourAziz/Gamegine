package com.stealthyone.mcb.gamegine.config;

import com.stealthyone.mcb.gamegine.Gamegine;

public enum ConfigBoolean {

    SELECTIONS_USE_WORLDEDIT("Selections.Use WorldEdit"),
    SIGNS_ENABLED("Signs.Enabled");

    private String path;
    private boolean defValue;

    private ConfigBoolean(String path) {
        this(path, false);
    }

    private ConfigBoolean(String path, boolean defValue) {
        this.path = path;
        this.defValue = defValue;
    }

    public boolean get() {
        return Gamegine.getInstance().getConfig().getBoolean(path, defValue);
    }

}