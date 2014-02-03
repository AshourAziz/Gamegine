package com.stealthyone.mcb.gameginecore.config;

import com.stealthyone.mcb.gameginecore.Gamegine;

public class ConfigString {

    private String path;
    private String defValue;

    ConfigString(String path) {
        this(path, null);
    }

    public ConfigString(String path, String defValue) {
        this.path = path;
        this.defValue = defValue;
    }

    public String get() {
        return Gamegine.getInstance().getConfig().getString(path, defValue);
    }

}