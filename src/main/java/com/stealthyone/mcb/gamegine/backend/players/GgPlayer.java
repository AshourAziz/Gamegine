package com.stealthyone.mcb.gamegine.backend.players;

import com.stealthyone.mcb.gamegine.Gamegine;
import com.stealthyone.mcb.gamegine.backend.games.GameInstance;
import com.stealthyone.mcb.stbukkitlib.api.Stbl;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class GgPlayer {

    private String playerName;
    private String uuid;

    private String curGame;

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

    public Player getPlayer() {
        return Bukkit.getPlayerExact(playerName);
    }

    public boolean isOnline() {
        return getPlayer() != null;
    }

    public GameInstance getCurrentGame() {
        return curGame == null ? null : Gamegine.getInstance().getGameManager().getGameInstance(curGame);
    }

}