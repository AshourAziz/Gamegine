package com.stealthyone.mcb.gamegine.config;

import com.stealthyone.mcb.gamegine.Gamegine;

public class ConfigLong {

    private String path;
    private long defValue = 0L;

    ConfigLong(String path) {
        this(path, 0L);
    }

    public ConfigLong(String path, long defValue) {
        this.path = path;
    }

    public long get() {
        return Gamegine.getInstance().getConfig().getLong(path);
    }

}