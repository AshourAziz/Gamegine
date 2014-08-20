/*
 * Gamegine - Game compatibility API and creation library for Bukkit
 * Copyright (C) 2013-2014 Stealth2800 <stealth2800@stealthyone.com>
 * Website: <http://stealthyone.com/>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
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