package com.stealthyone.mcb.gameginecore.backend.games;

import com.stealthyone.mcb.gameginecore.backend.arenas.Arena;

public abstract class GameInstance {

    private Arena owner;

    protected long updateFreq = 20L;

    private GameState state;

    public GameInstance(Arena owner) {
        this.owner = owner;
    }

    public GameState getState() {
        return state;
    }

    public void setState(GameState state) {
        if (state != this.state) {
            this.state = state;
        }
    }

    public boolean canPlayersJoin() {
        return state == GameState.WAITING || state == GameState.STARTING;
    }

    public abstract void startGame();

    public abstract void endGame();

    public abstract void gameTick();

}