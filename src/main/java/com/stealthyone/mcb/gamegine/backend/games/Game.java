package com.stealthyone.mcb.gamegine.backend.games;

import com.stealthyone.mcb.gamegine.backend.signs.GgSign;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Game {

    private JavaPlugin owner;
    private String name;

    private Map<String, GameInstance> gameInstances = new HashMap<>(); //Game instance ID, gameInstance

    public Game(JavaPlugin owner, String name) {
        this.owner = owner;
        this.name = name;
    }

    public JavaPlugin getOwner() {
        return owner;
    }

    public String getName() {
        return name;
    }

    public String getUniqueId() {
        return owner.getName() + ":" + name;
    }

    public List<Class<? extends GgSign>> getSignTypes() {
        return new ArrayList<>();
    }

    public GameInstance getGameInstance(String gameInstanceId) {
        return gameInstances.get(gameInstanceId);
    }

    public GameInstance createGameInstance() {
        return null;
    }

}