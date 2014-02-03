package com.stealthyone.mcb.gameginecore.messages;

import com.stealthyone.mcb.gameginecore.Gamegine;
import com.stealthyone.mcb.stbukkitlib.lib.messages.MessageReferencer;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public enum NoticeMessage implements MessageReferencer {

    COOLDOWN_ENDED,
    COOLDOWNS_PLAYER_HAS_NONE;

    private String path;

    private NoticeMessage() {
        path = "notices." + toString().toLowerCase();
    }

    @Override
    public String getMessagePath() {
        return path;
    }

    @Override
    public String getMessage() {
        return ChatColor.translateAlternateColorCodes('&', Gamegine.getInstance().getMessageManager().getMessage(this));
    }

    @Override
    public String getMessage(String... replacements) {
        return ChatColor.translateAlternateColorCodes('&', Gamegine.getInstance().getMessageManager().getMessage(this, replacements));
    }

    @Override
    public void sendTo(CommandSender sender) {
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', getMessage()));
    }

    @Override
    public void sendTo(CommandSender sender, String... replacements) {
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', getMessage(replacements)));
    }

}