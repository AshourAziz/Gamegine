package com.stealthyone.mcb.gameginecore.config;

import com.stealthyone.mcb.gameginecore.Gamegine;

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