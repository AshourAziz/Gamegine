package com.stealthyone.mcb.gamegine.backend.cooldowns;

import com.stealthyone.mcb.gamegine.Gamegine;
import org.bukkit.entity.Player;

public class SignInteractCooldown extends Cooldown {

    public SignInteractCooldown(Player player) {
        super("signInteractDelay", player, Gamegine.getInstance().getSignManager().getSignInteractDelay());
    }

}