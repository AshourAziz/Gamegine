package com.stealthyone.mcb.gamegine.messages;

import com.stealthyone.mcb.gamegine.GameginePlugin;
import com.stealthyone.mcb.gamegine.api.Gamegine;
import com.stealthyone.mcb.stbukkitlib.messages.MessageRef;

public class GamegineMessageRef extends MessageRef {

    public GamegineMessageRef(String category, String message) {
        super(((GameginePlugin) Gamegine.getInstance()).getMessageManager(), category, message);
    }

}