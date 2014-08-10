package com.stealthyone.mcb.gamegine.backend.signs.types;

import com.stealthyone.mcb.gamegine.GameginePlugin;
import com.stealthyone.mcb.gamegine.api.Gamegine;
import com.stealthyone.mcb.gamegine.api.games.modules.GameJoinModule;
import com.stealthyone.mcb.gamegine.api.signs.ActiveGSign;
import com.stealthyone.mcb.gamegine.api.signs.impl.YamlGSignType;
import com.stealthyone.mcb.gamegine.api.signs.modules.GSignInteractModule;
import com.stealthyone.mcb.gamegine.lib.games.instances.GameInstance;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.player.PlayerInteractEvent;

/**
 * A game sign that allows players to join a game.
 */
public class GameJoinSign extends YamlGSignType implements GSignInteractModule {

    public GameJoinSign(ConfigurationSection config) {
        super("join", true, config);
        if (lines.size() != 4) {
            lines.clear();
            lines.add("" + ChatColor.GREEN + ChatColor.BOLD + "{GAME}");
            lines.add(ChatColor.GOLD + "{PLAYERS}")
        }
    }

    @Override
    public void playerInteract(PlayerInteractEvent e, ActiveGSign sign) {
        GameInstance game = sign.getGame();
        if (!(game.getOwner() instanceof GameJoinModule)) {
            ((GameginePlugin) Gamegine.getInstance()).getMessageManager().getMessage("errors.game_not_joinable").sendTo(e.getPlayer());
        }
    }

}