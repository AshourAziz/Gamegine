package com.stealthyone.mcb.gamegine.backend.cooldowns;

import com.stealthyone.mcb.gamegine.Gamegine;

public class SignInteractCooldown extends Cooldown {

    public SignInteractCooldown(String cooldownName, String playerName) {
        super(cooldownName, playerName, Gamegine.getInstance().getSignManager().getSignInteractDelay());
    }

}