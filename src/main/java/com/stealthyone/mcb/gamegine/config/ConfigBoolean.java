package com.stealthyone.mcb.gamegine.config;

import com.stealthyone.mcb.gamegine.Gamegine;

public class ConfigBoolean {

    private String path;
    private boolean defValue;

    ConfigBoolean(String path) {
        this(path, false);
    }

    public ConfigBoolean(String path, boolean defValue) {
        this.path = path;
        this.defValue = defValue;
    }

    public boolean get() {
        return Gamegine.getInstance().getConfig().getBoolean(path, defValue);
    }

}