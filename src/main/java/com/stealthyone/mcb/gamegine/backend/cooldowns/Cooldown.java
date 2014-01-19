package com.stealthyone.mcb.gamegine.backend.cooldowns;

import com.stealthyone.mcb.gamegine.Gamegine;
import com.stealthyone.mcb.gamegine.messages.NoticeMessage;
import com.stealthyone.mcb.stbukkitlib.lib.utils.PlayerUtils;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

public class Cooldown {

    private String cooldownName;
    private String playerUuid;

    protected boolean persistent = false;
    protected long cooldownTicks;
    protected boolean liveCountdown = false;
    protected boolean onlineOnly = true;
    protected boolean alertPlayer = false;
    protected long currentTicks;

    public Cooldown(String cooldownName, Player player, long cooldownTicks) {
        this.cooldownName = cooldownName;
        this.playerUuid = player.getUniqueId().toString();
        this.cooldownTicks = cooldownTicks;
    }

    public Cooldown(ConfigurationSection config) {
        this.cooldownName = config.getString("cooldownName");
        this.playerUuid = config.getString("playerUuid");
        this.cooldownTicks = config.getLong("cooldownTicks");
        this.liveCountdown = config.getBoolean("liveCountdown");
        this.onlineOnly = config.getBoolean("onlineOnly");
        this.alertPlayer = config.getBoolean("alertPlayer");
        this.currentTicks = config.getLong("currentTicks");
    }

    public void save(ConfigurationSection config) {
        if (!persistent) {
            return;
        }

        config.set("cooldownName", cooldownName);
        config.set("playerUuid", playerUuid);
        config.set("cooldownTicks", cooldownTicks);
        config.set("activeCooldown", liveCountdown);
        config.set("onlineOnly", onlineOnly);
        config.set("alertPlayer", alertPlayer);
        config.set("currentTicks", currentTicks);
    }

    public Cooldown setPersistent(boolean value) {
        this.persistent = value;
        return this;
    }

    public Cooldown setActiveCooldown(boolean value) {
        this.liveCountdown = value;
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

    public String getPlayerUuid() {
        return playerUuid;
    }

    public Player getPlayer() {
        return PlayerUtils.getPlayerByUuid(playerUuid);
    }

    public boolean isPlayerOnline() {
        return getPlayer() != null;
    }

    public boolean isPersistent() {
        return persistent;
    }

    public boolean isLiveCountdown() {
        return liveCountdown;
    }

    public void delete() {
        Gamegine.getInstance().getCooldownManager().unregisterCooldown(this);
        if (liveCountdown && isPlayerOnline()) {
            NoticeMessage.COOLDOWN_ENDED.sendTo(getPlayer(), cooldownName);
        }
    }

    public void decrement(long ticks) {
        currentTicks += ticks;
        check();
    }

    public boolean check() {
        if (currentTicks >= cooldownTicks) {
            delete();
            return false;
        } else {
            return true;
        }
    }

    public long getCooldownTicks() {
        return cooldownTicks;
    }

    public long getCurrentTicks() {
        return currentTicks;
    }

}