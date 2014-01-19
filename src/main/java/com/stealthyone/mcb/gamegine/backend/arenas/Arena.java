package com.stealthyone.mcb.gamegine.backend.arenas;

import com.stealthyone.mcb.gamegine.backend.selections.Selection;
import com.stealthyone.mcb.stbukkitlib.lib.utils.LocationUtils;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.List;

public class Arena {

	private String name;
	private Selection selection;
	private List<Location> spawnPoints = new ArrayList<>();
	private boolean pvp = false;

	public Arena(String name, Selection selection) {
		this.name = name;
		this.selection = selection;
	}

    public Arena(ConfigurationSection config) {
        this.name = config.getString("name");
        this.pvp = config.getBoolean("pvp");
        this.selection = new Selection(config.getConfigurationSection("selection"));
        for (String rawLoc : config.getStringList("spawnPoints")) {
            spawnPoints.add(LocationUtils.stringToLocation(rawLoc));
        }
    }

    public void save(ConfigurationSection config) {
        config.set("name", name);
        selection.save(config.createSection("selection"));

    }

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Selection getSelection() {
		return selection;
	}

	public void setSelection(Selection selection) {
		this.selection = selection;
	}

	public List<Location> getSpawn() {
		return spawnPoints;
	}

    public void addSpawn(Location location) {
        spawnPoints.add(location);
    }

    public Location removeSpawn(int index) {
        try {
            return spawnPoints.remove(index);
        } catch (IndexOutOfBoundsException ex) {
            return null;
        }
    }

	public boolean isPvp() {
		return pvp;
	}

	public void setPvp(boolean pvp) {
		this.pvp = pvp;
	}
	
}