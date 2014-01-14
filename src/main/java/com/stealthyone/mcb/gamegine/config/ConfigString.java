package com.stealthyone.mcb.gamegine.config;

import com.stealthyone.mcb.gamegine.Gamegine;

public enum ConfigString {

    SELECTIONS_WAND("Selections.Wand item");

    private String path;
    private String defValue;

    private ConfigString(String path) {
        this(path, null);
    }

    private ConfigString(String path, String defValue) {
        this.path = path;
        this.defValue = defValue;
    }

    public String get() {
        return Gamegine.getInstance().getConfig().getString(path, defValue);
    }

}