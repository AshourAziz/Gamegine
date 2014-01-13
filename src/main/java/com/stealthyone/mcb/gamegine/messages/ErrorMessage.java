package com.stealthyone.mcb.gamegine.messages;

import com.stealthyone.mcb.gamegine.Gamegine;
import com.stealthyone.mcb.stbukkitlib.lib.messages.MessageReferencer;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public enum ErrorMessage implements MessageReferencer {

    MUST_BE_PLAYER,
    NO_PERMISSION,
    UNKNOWN_COMMAND,
    UNKNOWN_PLAYER;

    private String path;

    private ErrorMessage() {
        path = "errors." + toString().toLowerCase();
    }

    @Override
    public String getMessagePath() {
        return path;
    }

    @Override
    public String getMessage() {
        return Gamegine.getInstance().getMessageManager().getMessage(this);
    }

    @Override
    public String getMessage(String... replacements) {
        return Gamegine.getInstance().getMessageManager().getMessage(this, replacements);
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