package com.stealthyone.mcb.gamegine.backend.games;

import com.stealthyone.mcb.gamegine.backend.arenas.Arena;
import com.stealthyone.mcb.gamegine.backend.players.GgPlayer;

import java.util.HashMap;
import java.util.Map;

public abstract class GameInstance {

    private Game owner;
    private Arena arena;

    protected long updateFreq = 20L;

    private Map<String, GgPlayer> players = new HashMap<>();

    public GameInstance(Game owner) {
        this.owner = owner;
    }

    public void init(Game owner) {
        this.owner = owner;
    }

    public abstract void startGame();

}