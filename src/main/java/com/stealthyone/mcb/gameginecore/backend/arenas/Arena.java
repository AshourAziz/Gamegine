package com.stealthyone.mcb.gameginecore.backend.arenas;

import com.stealthyone.mcb.gameginecore.backend.games.Game;
import com.stealthyone.mcb.gameginecore.backend.games.GameInstance;

public abstract class Arena {

    private Game owner;

    private int arenaId;
    private GgArenaFile arenaConfig;
    private GameInstance gameInstance;

    public Arena(Game owner, int arenaId) {
        this.owner = owner;
        this.arenaId = arenaId;
        arenaConfig = new GgArenaFile(this);
        createGameInstance();
    }

    public Game getOwner() {
        return owner;
    }

    public int getId() {
        return arenaId;
    }

    public String getUniqueId() {
        return owner.getUniqueId() + "@" + arenaId;
    }

    public GgArenaFile getArenaConfig() {
        return arenaConfig;
    }

    protected abstract void createGameInstance();

    public GameInstance getGameInstance() {
        return gameInstance;
    }

}