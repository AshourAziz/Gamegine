package com.stealthyone.mcb.gamegine.backend.signs.types;

import com.stealthyone.mcb.gamegine.api.Gamegine;
import com.stealthyone.mcb.gamegine.api.signs.impl.YamlGSignType;
import com.stealthyone.mcb.gamegine.backend.signs.GgSignManager;
import com.stealthyone.mcb.stbukkitlib.storage.YamlFileManager;
import org.bukkit.configuration.ConfigurationSection;

/**
 * A YAML sign extension that is made to work specifically with GgSignManager.
 */
public abstract class GgYamlGSign extends YamlGSignType {

    public GgYamlGSign(String shortName, boolean useSignVariables, ConfigurationSection config) {
        super(shortName, useSignVariables, config);
    }

    @Override
    public void reload() {
        YamlFileManager file = ((GgSignManager) Gamegine.getInstance().getSignManager()).getSignTypesFile();
        ConfigurationSection config = file.getConfig().getConfigurationSection(shortName);
        if (config == null) config = file.getConfig().createSection(shortName);

        lines.clear();
        lines.addAll(config.getStringList("formats.lines"));
        playerLines.clear();
        playerLines.addAll(config.getStringList("formats.playerLines"));

        loadFrom(config);
    }

}