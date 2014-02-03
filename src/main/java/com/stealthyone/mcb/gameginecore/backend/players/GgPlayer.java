package com.stealthyone.mcb.gameginecore.backend.players;

import com.stealthyone.mcb.gameginecore.Gamegine;
import com.stealthyone.mcb.gameginecore.backend.games.GameInstance;
import com.stealthyone.mcb.stbukkitlib.api.Stbl;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class GgPlayer {

    private String playerName;
    private String uuid;

    public GgPlayer(String uuid) {
        this.uuid = uuid;
        playerName = Stbl.getUuidManager().getName(uuid);
    }

    public GgPlayerFile getFile() {
        return Gamegine.getInstance().getPlayerManager().getFile(uuid, true);
    }

    public String getName() {
        return playerName;
    }

    public String getUuid() {
        return uuid;
    }

    public Player getPlayer() {
        return Bukkit.getPlayerExact(playerName);
    }

    public boolean isOnline() {
        return getPlayer() != null;
    }

    public GameInstance getCurrentGame() {
        return Gamegine.getInstance().getPlayerManager().getPlayerGame(this);
    }

    public boolean addToGame(GameInstance game) {
        return Gamegine.getInstance().getPlayerManager().addPlayerToGame(this, game);
    }

}