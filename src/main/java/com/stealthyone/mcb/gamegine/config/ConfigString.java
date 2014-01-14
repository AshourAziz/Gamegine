package com.stealthyone.mcb.gamegine.config;

import com.stealthyone.mcb.gamegine.Gamegine;

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