package com.stealthyone.mcb.gamegine.backend.signs;

import com.stealthyone.mcb.gamegine.GameginePlugin;
import com.stealthyone.mcb.gamegine.api.games.Game;
import com.stealthyone.mcb.gamegine.api.hooks.plugins.defaults.HookInSigns;
import com.stealthyone.mcb.gamegine.api.logging.GamegineLogger;
import com.stealthyone.mcb.gamegine.api.signs.ActiveGameSign;
import com.stealthyone.mcb.gamegine.api.signs.GameSignType;
import com.stealthyone.mcb.gamegine.api.signs.SignManager;
import com.stealthyone.mcb.gamegine.api.signs.variables.SignVariable;
import com.stealthyone.mcb.gamegine.lib.games.InstanceGame;
import com.stealthyone.mcb.gamegine.lib.games.instances.GameInstance;
import com.stealthyone.mcb.gamegine.utils.BlockLocation;
import com.stealthyone.mcb.stbukkitlib.storage.YamlFileManager;
import de.blablubbabc.insigns.SignSendEvent;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

import java.io.File;
import java.util.*;
import java.util.regex.Pattern;

@RequiredArgsConstructor
public class GgSignManager implements Listener, SignManager {

    private final static Pattern YAML_FILE_PATTERN = Pattern.compile("(.+).yml");

    private final GameginePlugin plugin;

    /* Configuration. */
    private InSignsListener inSignsListener;

    private List<String> gameNotFoundFormat;

    /* Sign text variables. */
    private Map<String, SignVariable> registeredVariables = new HashMap<>();
    private Map<String, String> variableKeys = new HashMap<>();

    /* Sign types. */
    private YamlFileManager signTypesFile;

    private Map<String, GameSignType> registeredSignTypes = new HashMap<>();
    private Map<String, String> typeShortNameIndex = new HashMap<>(); // Short name, Type
    private Map<String, List<String>> configuredFormats = new HashMap<>();

    /* Loaded signs. */
    private File activeSignsDir;

    private Map<BlockLocation, ActiveGameSign> activeSigns = new HashMap<>();
    private Map<String, Set<BlockLocation>> gameActiveSigns = new HashMap<>();

    private Set<BlockLocation> pendingActiveSignLocations = new HashSet<>();
    private Map<String, Set<String>> pendingActiveSigns = new HashMap<>(); // Type name, list of file names.

    /**
     * Load sign manager data.
     */
    public void load() {
        // Load sign types
        signTypesFile = new YamlFileManager(plugin.getDataFolder() + File.separator + "signTypes.yml");

        // Load active signs
        activeSignsDir = new File(plugin.getDataFolder() + File.separator + "data" + File.separator + "active_signs");
        activeSignsDir.mkdirs();

        for (File file : activeSignsDir.listFiles()) {
            if (!YAML_FILE_PATTERN.matcher(file.getName()).matches()) continue;
            loadActiveSign(file);
        }

        reload();
    }

    /**
     * Loads an active sign from its file.
     *
     * @param file File to load from.
     */
    private boolean loadActiveSign(File file) {
        YamlFileManager yamlFile = new YamlFileManager(file);
        FileConfiguration config = yamlFile.getConfig();

        String typeName = config.getString("type");
        if (typeName == null) {
            GamegineLogger.warning("[SignManager] Unable to load active sign from " + file.getPath() + " - no type defined, deleting file.");
            file.delete();
            return false;
        }

        BlockLocation location = (BlockLocation) config.get("location");

        if (!registeredSignTypes.containsKey(typeName)
                && (!pendingActiveSigns.containsKey(typeName) || (pendingActiveSigns.containsKey(typeName) && !pendingActiveSigns.get(typeName).contains(file.getAbsolutePath())))) {
            if (location == null || location.getWorld() == null) {
                GamegineLogger.warning("[SignManager] Unable to load active sign from " + file.getPath() + " - invalid location defined, deleting file.");
                file.delete();
                return false;
            }

            if (pendingActiveSignLocations.contains(location)) {
                GamegineLogger.warning("[SignManager] Unable to load active sign from " + file.getPath() + " - location already in use, deleting file.");
                file.delete();
                return false;
            }

            if (!validateSignBlock(location.getBlock())) {
                GamegineLogger.warning("[SignManager] Unable to load active sign from " + file.getPath() + " - block at location " + location.toString() + " is not a sign, deleting file.");
                file.delete();
                return false;
            }

            Set<String> filePaths = pendingActiveSigns.get(typeName);
            if (filePaths == null) {
                filePaths = new HashSet<>();
                pendingActiveSigns.put(typeName, filePaths);
            }
            filePaths.add(file.getAbsolutePath());
            pendingActiveSignLocations.add(location);
            GamegineLogger.warning("[SignManager] Unable to load active sign from " + file.getPath() + " - type '" + typeName + "' not registered, added to pending active sign list.");
            return false;
        }

        GameSignType type = registeredSignTypes.get(typeName);
        ActiveGameSign activeGameSign = new ActiveGameSign(type, yamlFile);
        try {
            activeGameSign.getGame();
        } catch (IllegalArgumentException ex) {
            // gameInstanceRef is invalid.
            GamegineLogger.warning("[SignManager] Unable to load active sign from " + file.getPath() + " - invalid game instance reference.");
            file.delete();
            return false;
        } catch (UnsupportedOperationException ex) {
            // Game does not implement SingleInstanceGame or MultiInstanceGame.
            GamegineLogger.warning("[SignManager] Unable to load active sign from " + file.getPath() + " - game does not support signs.");
            file.delete();
            return false;
        } catch (IllegalStateException eX) {
            // Game is not loaded. We don't have to worry about this.
        }

        activeSigns.put(location, activeGameSign);

        String[] gameInstanceRef = activeGameSign.getGameInstanceRef().split(":");
        String gameClassName = gameInstanceRef[0];

        Set<BlockLocation> gameLocations = gameActiveSigns.get(gameClassName);
        if (gameLocations == null) {
            gameLocations = new LinkedHashSet<>();
            gameActiveSigns.put(gameClassName, gameLocations);
        }
        gameLocations.add(location);

        pendingActiveSignLocations.remove(location);
        Set<String> fileNames = pendingActiveSigns.get(typeName);
        if (fileNames != null) {
            fileNames.remove(file.getAbsolutePath());
        }
        return true;
    }

    /**
     * Deletes a loaded active sign and deletes its file.
     *
     * @param sign Sign to unload and delete.
     */
    private void deleteActiveSign(ActiveGameSign sign) {
        BlockLocation location = sign.getLocation();
        if (location == null) return;

        activeSigns.remove(location);

        String gameName = sign.getGame().getOwner().getClass().getCanonicalName();
        if (gameActiveSigns.containsKey(gameName)) {
            gameActiveSigns.get(gameName).remove(location);
        }
        sign.getFile().getFile().delete();
    }

    /**
     * Reload the sign manager's configuration.
     */
    public void reload() {
        signTypesFile.reloadConfig();

        FileConfiguration pConfig = plugin.getConfig();

        gameNotFoundFormat = pConfig.getStringList("Signs.Formats.Game not found");
        if (gameNotFoundFormat.size() != 4) {
            gameNotFoundFormat = Arrays.asList(
                    "" + ChatColor.GREEN + ChatColor.BOLD + "\u300AGamegine\u300B",
                    ChatColor.DARK_RED + "GAME NOT FOUND",
                    null,
                    ChatColor.RED + "{GAME}"
            );
            GamegineLogger.warning("[SignManager] Game not found sign format is not 4 lines, using the default format.");
        }

        if (inSignsListener != null) {
            SignSendEvent.getHandlerList().unregister(inSignsListener);
            inSignsListener = null;
        }

        boolean inSignsEnabled = plugin.getConfig().getBoolean("Signs.Enable InSigns", true);
        if (inSignsEnabled && !plugin.getHookManager().isEnabled(HookInSigns.class)) {
            inSignsEnabled = false;
            GamegineLogger.info("[SignManager] InSigns is enabled in the config but not installed on the server!");
        }

        if (inSignsEnabled) {
            Bukkit.getPluginManager().registerEvents(inSignsListener = new InSignsListener(), plugin);
            GamegineLogger.info("[SignManager] Enabling InSigns support.");
        } else {
            GamegineLogger.info("[SignManager] InSigns support disabled.");
        }

        FileConfiguration typeConfig = signTypesFile.getConfig();
        configuredFormats.clear();
        for (GameSignType signType : registeredSignTypes.values()) {
            String typeName = signType.getClass().getCanonicalName();
            if (signType.isFormatConfigurable()) {
                List<String> list = typeConfig.getStringList(typeName + ".format");
                if (list != null) {
                    if (list.size() != 4) {
                        GamegineLogger.warning("[SignManager] Unable to load custom sign type format for '" + typeName + "' - the format in signTypes.yml does not have 4 lines defined.");
                    } else {
                        configuredFormats.put(typeName, list);
                        GamegineLogger.info("[SignManager] Loaded custom sign type format for '" + typeName + "'");
                    }
                }
            }
        }
    }

    /**
     * Checks to see if an {@link com.stealthyone.mcb.gamegine.api.signs.ActiveGameSign} object's block is actually a sign or not.
     *
     * @param sign Sign to check.
     * @return True if the block is a sign.
     *         False if the block is not a sign.
     */
    private boolean validateActiveSignBlock(ActiveGameSign sign) {
        Block block = sign.getLocation().getBlock();
        boolean result = validateSignBlock(block);
        if (!result) {
            GamegineLogger.warning("[SignManager] The block for the active sign at " + sign.getLocation().toString() + " is not a sign, removing active sign.");
        }
        return result;
    }

    /**
     * Checks to see if a block is actually a sign or not.
     *
     * @param block Block to check.
     * @return True if the block is a sign.
     *         False if the block is not a sign.
     */
    private boolean validateSignBlock(Block block) {
        return block != null && (block.getType() == Material.SIGN_POST || block.getType() == Material.WALL_SIGN);
    }

    @Override
    public boolean registerVariable(@NonNull SignVariable variable) {
        String varName = variable.getClass().getCanonicalName();
        if (registeredVariables.containsKey(varName)) return false;

        String key = variable.getKey();
        if (variableKeys.containsKey(key)) return false;

        variableKeys.put(key, varName);

        registeredVariables.put(varName, variable);
        return true;
    }

    @Override
    public boolean registerSignType(@NonNull GameSignType signType) {
        String name = signType.getClass().getCanonicalName();
        if (registeredSignTypes.containsKey(name)) return false;

        if (signType.getDefaultFormat() == null || signType.getDefaultFormat().size() != 4) {
            throw new IllegalArgumentException("Sign type '" + name + "' does not have a valid format.");
        }

        registeredSignTypes.put(name, signType);
        GamegineLogger.info("[SignManager] Registered sign type '" + name + "'");

        String shortName = signType.getShortName();
        if (shortName == null || shortName.isEmpty()) {
            GamegineLogger.debug("No short name set for sign type '" + name + "' - will not attempt to register.");
        } else if (shortName.contains(" ")) {
            GamegineLogger.warning("Unable to register short name for sign type '" + name + "' - name is invalid (contains spaces).");
        } else if (typeShortNameIndex.containsKey(shortName.toLowerCase())) {
            GamegineLogger.warning("Unable to register short name for sign type '" + name + "' - already registered.");
        } else {
            typeShortNameIndex.put(shortName.toLowerCase(), name);
            GamegineLogger.info("Registered short name '" + shortName + "' for sign type '" + name + "'.");
        }

        if (registeredSignTypes.containsKey(name)) {
            GamegineLogger.info("[SignManager] Found pending active signs for this type, loading them now.");
            for (String filePath : pendingActiveSigns.remove(name)) {
                loadActiveSign(new File(filePath));
            }
        }
        return true;
    }

    @Override
    public GameSignType getSignType(@NonNull String shortName) {
        String typeClazz = typeShortNameIndex.get(shortName.toLowerCase());
        return typeClazz == null ? null : registeredSignTypes.get(typeClazz);
    }

    @Override
    public ActiveGameSign getSign(@NonNull Location location) {
        return activeSigns.get(new BlockLocation(location));
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {
        Block block = e.getClickedBlock();
        if (block == null || (!block.getType().equals(Material.WALL_SIGN) && !block.getType().equals(Material.SIGN_POST))) {
            return;
        }

        ActiveGameSign gameSign = plugin.getSignManager().getSign(block.getLocation());
        if (gameSign != null) {
            switch (e.getAction()) {
                case RIGHT_CLICK_BLOCK:
                    gameSign.handleRightClick(e.getPlayer());
                    return;

                case LEFT_CLICK_BLOCK:
                    gameSign.handleLeftClick(e.getPlayer());
                    return;

                default:
                    e.getPlayer().sendMessage(ChatColor.RED + "An unknown error occurred, contact an administrator.");
            }
        }
    }

    /**
     * Returns the current format of a sign type.
     *
     * @param type Type to get format of.
     * @return List of strings that represent the format.
     */
    private List<String> getSignFormat(@NonNull GameSignType type) {
        String typeName = type.getClass().getCanonicalName();
        return configuredFormats.containsKey(typeName) ? configuredFormats.get(typeName) : type.getDefaultFormat();
    }

    /**
     * Updates a sign's lines.
     *
     * @param sign Sign to update.
     */
    public void updateSign(ActiveGameSign sign) {
        if (!validateActiveSignBlock(sign)) return;

        Sign signBlock = (Sign) sign.getLocation().getBlock().getState();
        List<String> newLines = new ArrayList<>(getSignFormat(sign.getType()));

        GameInstance gameInstance;
        try {
            gameInstance = sign.getGame();
        } catch (IllegalStateException ex) {
            // Game not loaded.

            String[] gameClassSplit = sign.getGameInstanceRef().split(":")[0].split("\\.");
            String gameName = gameClassSplit[gameClassSplit.length - 1];

            for (int i = 0; i < 4; i++) {
                newLines.set(i, ChatColor.translateAlternateColorCodes('&', gameNotFoundFormat.get(i).replace("{GAME}", gameName)));
            }
            return;
        }

        for (SignVariable var : registeredVariables.values()) {
            String varName = var.getClass().getCanonicalName();
            String replacement = var.getReplacement(gameInstance);

            for (int i = 0; i < 4; i++) {
                String string = newLines.get(i);
                if (string != null) {
                    newLines.set(i, string.replace(varName, replacement));
                }
            }
        }

        for (int i = 0; i < 4; i++) {
            String line = newLines.get(i);
            if (line == null) continue;
            signBlock.setLine(i + 1, ChatColor.translateAlternateColorCodes('&', newLines.get(i)));
        }
        signBlock.update();
    }

    /**
     * Returns the loaded active signs for a game.
     *
     * @param game The game to get the signs of.
     * @return Collection of signs.
     * @throws java.lang.IllegalArgumentException if the Game is not an instance of {@link com.stealthyone.mcb.gamegine.lib.games.InstanceGame}
     */
    public Collection<ActiveGameSign> getActiveSigns(@NonNull Game game) {
        if (!(game instanceof InstanceGame)) throw new IllegalArgumentException("Game cannot have signs - not an instance of InstanceGame.");
        Set<ActiveGameSign> signs = new LinkedHashSet<>();
        Set<BlockLocation> locations = gameActiveSigns.get(game.getClass().getCanonicalName());
        if (locations != null && !locations.isEmpty()) {
            for (BlockLocation loc : locations) {
                signs.add(activeSigns.get(loc));
            }
        }
        return signs;
    }

    /**
     * Returns a read-only view of all of the registered sign types.
     *
     * @return Read-only collection of registered sign types.
     */
    public Collection<GameSignType> getRegisteredTypes() {
        return Collections.unmodifiableCollection(registeredSignTypes.values());
    }

    /**
     * Creates a sign.
     *
     * @param block Block that the sign will exist as.
     * @param type The type of sign to create.
     * @param game The game the sign exists for.
     * @return True if successful.
     *         False if unable to create.
     * @throws java.lang.IllegalArgumentException Thrown if the block is not a sign.
     */
    public boolean createSign(@NonNull Block block, @NonNull GameSignType type, @NonNull String gameRef, @NonNull GameInstance game) {
        if (block.getType() != Material.SIGN_POST || block.getType() != Material.WALL_SIGN)
            throw new IllegalArgumentException("Block is not a sign.");

        BlockLocation location = new BlockLocation(block.getLocation());
        YamlFileManager file = new YamlFileManager(activeSignsDir + File.separator + location.toString() + ".yml");
        FileConfiguration config = file.getConfig();
        config.set("type", type.getClass().getCanonicalName());
        config.set("game", gameRef);
        config.set("location", location);
        return loadActiveSign(file.getFile());
    }

    /**
     * Listens for InSigns's SignSendEvent and modifies it accordingly.
     */
    public class InSignsListener implements Listener {

        @EventHandler
        public void onSignSend(SignSendEvent e) {
            BlockLocation loc = new BlockLocation(e.getLocation());
            ActiveGameSign sign = activeSigns.get(loc);
            if (sign != null) {
                List<String> newLines = getLines(sign, e.getPlayer());

                for (int i = 0; i < 4; i++) {
                    String line = newLines.get(i);
                    if (line == null) continue;
                    e.setLine(i + 1, ChatColor.translateAlternateColorCodes('&', newLines.get(i)));
                }
            }
        }

        private List<String> getLines(ActiveGameSign sign, Player p) {
            List<String> newLines = new ArrayList<>(getSignFormat(sign.getType()));

            GameInstance gameInstance;
            try {
                gameInstance = sign.getGame();
            } catch (IllegalStateException ex) {
                // Game not loaded.

                String[] gameClassSplit = sign.getGameInstanceRef().split(":")[0].split("\\.");
                String gameName = gameClassSplit[gameClassSplit.length - 1];

                for (int i = 0; i < 4; i++) {
                    newLines.set(i, ChatColor.translateAlternateColorCodes('&', gameNotFoundFormat.get(i).replace("{GAME}", gameName)));
                }
                return newLines;
            }

            for (SignVariable var : registeredVariables.values()) {
                String varName = var.getClass().getCanonicalName();
                String replacement = var.getReplacement(gameInstance);

                for (int i = 0; i < 4; i++) {
                    String string = newLines.get(i);
                    if (string != null) {
                        newLines.set(i, string.replace(varName, replacement));
                    }
                }
            }

            return newLines;
        }

    }

}