package com.stealthyone.mcb.gameginecore.backend.cooldowns;

import com.stealthyone.mcb.gameginecore.Gamegine;
import org.bukkit.entity.Player;

public class SignInteractCooldown extends Cooldown {

    public SignInteractCooldown(Player player) {
        super("signInteractDelay", player, Gamegine.getInstance().getSignManager().getSignInteractDelay());
    }

}