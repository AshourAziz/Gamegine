package com.stealthyone.mcb.gameginecore.backend.games;

import com.stealthyone.mcb.gameginecore.Gamegine;
import com.stealthyone.mcb.gameginecore.backend.arenas.Arena;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public abstract class Game {

    private JavaPlugin owner;
    private String name;
    private String uniqueId;

    private File dataDir;
    private GgGameFile gameConfig;

    public Game(JavaPlugin owner, String name) {
        this.owner = owner;
        gameConfig = new GgGameFile(this);
        dataDir = new File(owner.getDataFolder() + File.separator + "GamegineData");
        dataDir.mkdir();
        this.name = name;
        this.uniqueId = owner.getName() + ":" + name;
    }

    public final JavaPlugin getOwner() {
        return owner;
    }

    public final GgGameFile getGameConfig() {
        return gameConfig;
    }

    public final File getDataDir() {
        return dataDir;
    }

    public final String getName() {
        return name;
    }

    public final String getUniqueId() {
        return uniqueId;
    }

    public Arena getArena(int arenaNum) {
        return Gamegine.getInstance().getArenaManager().getArena(uniqueId + "@" + arenaNum);
    }

    public abstract Arena loadArena(File file);

}