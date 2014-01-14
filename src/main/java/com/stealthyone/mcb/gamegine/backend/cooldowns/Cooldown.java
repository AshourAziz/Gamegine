package com.stealthyone.mcb.gamegine.backend.cooldowns;

import com.stealthyone.mcb.gamegine.Gamegine;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class Cooldown {

    private String cooldownName;
    private String playerName;
    protected boolean persistent = false;
    protected long cooldownTicks;

    protected boolean activeCountdown = false;
    protected boolean onlineOnly = true;
    protected boolean alertPlayer = false;
    protected long currentTicks;

    public Cooldown(String cooldownName, String playerName, long cooldownTicks) {
        this.cooldownName = cooldownName;
        this.playerName = playerName;
        this.cooldownTicks = cooldownTicks;
    }

    public Cooldown setPersistent(boolean value) {
        this.persistent = value;
        return this;
    }

    public Cooldown setActiveCooldown(boolean value) {
        this.activeCountdown = value;
        return this;
    }

    public Cooldown setOnlineOnly(boolean value) {
        this.onlineOnly = value;
        return this;
    }

    public Cooldown setAlertPlayer(boolean value) {
        this.alertPlayer = value;
        return this;
    }

    public boolean register() {
        if (cooldownTicks > 0L) {
            return Gamegine.getInstance().getCooldownManager().registerCooldown(this);
        }
        return false;
    }

    public String getCooldownName() {
        return cooldownName;
    }

    public String getPlayerName() {
        return playerName;
    }

    public Player getPlayer() {
        return Bukkit.getPlayerExact(playerName);
    }

    public boolean isPlayerOnline() {
        return getPlayer() != null;
    }

    public boolean isPersistent() {
        return persistent;
    }

    public boolean isActiveCountdown() {
        return activeCountdown;
    }

    public void delete() {

    }

    public void decrement(long ticks) {
        if (!activeCountdown) return;

        currentTicks += ticks;
        if (currentTicks >= cooldownTicks) {

        }
    }

}