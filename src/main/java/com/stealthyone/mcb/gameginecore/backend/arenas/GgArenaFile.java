package com.stealthyone.mcb.gameginecore.backend.arenas;

import com.stealthyone.mcb.gameginecore.backend.selections.Selection;
import com.stealthyone.mcb.stbukkitlib.lib.storage.YamlFileManager;
import com.stealthyone.mcb.stbukkitlib.lib.utils.LocationUtils;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;
import java.util.List;

public class GgArenaFile extends YamlFileManager {

    private Arena owner;

    /* Configuration values */
    private String nickname;
    private Selection selection;
    private List<Location> spawnPoints;

    public GgArenaFile(Arena arena) {
        super(new File(arena.getOwner().getDataDir() + File.separator + "arenas" + File.separator + "arena_" + arena.getId() + ".yml"));
    }

    public Arena getOwner() {
        return owner;
    }

    @Override
    public final void reloadConfig() {
        super.reloadConfig();

        FileConfiguration config = getConfig();

        /* Load configuration values */
        nickname = config.getString("nickname", "Arena {ID}").replace("{ID}", Integer.toString(owner.getId()));
        selection = new Selection(config.getConfigurationSection("bounds"));
        spawnPoints = LocationUtils.stringListToLocationList(config.getStringList("spawnPoints"));
        loadConfiguration(config);
    }

    @Override
    public final void saveFile() {
        FileConfiguration config = getConfig();

        /* Save configuration values */
        config.set("nickname", nickname);
        selection.save(config.createSection("bounds"));
        config.set("spawnPoints", LocationUtils.locationListToStringList(spawnPoints, true));
        saveConfiguration(config);

        super.saveFile();
    }

    protected void loadConfiguration(FileConfiguration config) { };

    protected void saveConfiguration(FileConfiguration config) { };

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        //TODO: Make sure nickname is not in use by any other arenas for the owning game
        this.nickname = nickname;
    }

    public Selection getSelection() {
        return selection;
    }

    public void setSelection(Selection selection) {
        this.selection = selection;
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

}