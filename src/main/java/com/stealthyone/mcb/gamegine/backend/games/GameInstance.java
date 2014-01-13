package com.stealthyone.mcb.gamegine.backend.games;

import com.stealthyone.mcb.gamegine.backend.arenas.Arena;

public abstract class GameInstance {

    private Game owner;
    private Arena arena;

    protected long updateFreq = 20L;

    public GameInstance(Game owner) {
        this.owner = owner;
    }

    public void init(Game owner) {
        this.owner = owner;
    }

    public abstract void startGame();

}