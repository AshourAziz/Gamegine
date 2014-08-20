/*
 * Gamegine - Game compatibility API and creation library for Bukkit
 * Copyright (C) 2013-2014 Stealth2800 <stealth2800@stealthyone.com>
 * Website: <http://stealthyone.com/>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.stealthyone.mcb.gamegine.backend.signs;

import com.stealthyone.mcb.gamegine.GameginePlugin;
import com.stealthyone.mcb.gamegine.api.games.Game;
import com.stealthyone.mcb.gamegine.api.hooks.plugins.defaults.HookInSigns;
import com.stealthyone.mcb.gamegine.api.logging.GamegineLogger;
import com.stealthyone.mcb.gamegine.api.signs.ActiveGSign;
import com.stealthyone.mcb.gamegine.api.signs.GSignType;
import com.stealthyone.mcb.gamegine.api.signs.SignManager;
import com.stealthyone.mcb.gamegine.api.signs.modules.GSignReloadModule;
import com.stealthyone.mcb.gamegine.api.signs.variables.SignVariable;
import com.stealthyone.mcb.gamegine.backend.signs.types.GameJoinSign;
import com.stealthyone.mcb.gamegine.backend.signs.variables.SignGameIDVar;
import com.stealthyone.mcb.gamegine.backend.signs.variables.SignGameNameVar;
import com.stealthyone.mcb.gamegine.backend.signs.variables.SignPlayerCountVar;
import com.stealthyone.mcb.gamegine.backend.signs.variables.SignPlayersVar;
import com.stealthyone.mcb.gamegine.lib.games.InstanceGame;
import com.stealthyone.mcb.gamegine.lib.games.instances.GameInstance;
import com.stealthyone.mcb.gamegine.utils.BlockLocation;
import com.stealthyone.mcb.stbukkitlib.storage.YamlFileManager;
import com.stealthyone.mcb.stbukkitlib.utils.ConfigUtils;
import de.blablubbabc.insigns.SignSendEvent;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Pattern;

@RequiredArgsConstructor
public class GgSignManager implements SignManager {

    private final static Pattern YAML_FILE_PATTERN = Pattern.compile("(.+).yml");

    private final GameginePlugin plugin;

    /* Configuration. */
    private InSignsListener inSignsListener;

    List<String> gameNotFoundFormat;

    /* Sign text variables. */
    Map<String, SignVariable> registeredVariables = new HashMap<>();
    private Map<String, String> variableKeys = new HashMap<>();

    /* Sign types. */
    @Getter
    private YamlFileManager signTypesFile;

    private Map<String, GSignType> registeredSignTypes = new HashMap<>();
    private Map<String, String> typeShortNameIndex = new HashMap<>(); // Short name, Type

    /* Loaded signs. */
    private File activeSignsDir;

    Map<BlockLocation, ActiveGSign> activeSigns = new HashMap<>();
    private Map<String, Set<BlockLocation>> gameActiveSigns = new HashMap<>();

    private Set<BlockLocation> pendingActiveSignLocations = new HashSet<>();
    private Map<String, Set<String>> pendingActiveSigns = new HashMap<>(); // Type name, list of file names.

    /**
     * Registers defaults.
     */
    public void loadDefaults() {
        registerVariable(new SignGameNameVar());
        registerVariable(new SignGameIDVar());
        registerVariable(new SignPlayersVar());
        registerVariable(new SignPlayerCountVar());

        registerSignType(new GameJoinSign(ConfigUtils.getSection(signTypesFile.getConfig(), "join")));
    }

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

        GSignType type = registeredSignTypes.get(typeName);
        ActiveGSign activeGameSign = new ActiveGSign(type, yamlFile);
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
    public void deleteActiveSign(@NonNull ActiveGSign sign) {
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
        // Reload sign types.
        signTypesFile.reloadConfig();

        for (GSignType type : registeredSignTypes.values()) {
            if (type instanceof GSignReloadModule) {
                ((GSignReloadModule) type).reload();
            }
        }

        // Reload plugin configuration.
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
            Bukkit.getPluginManager().registerEvents(inSignsListener = new InSignsListener(plugin), plugin);
            GamegineLogger.info("[SignManager] Enabling InSigns support.");
        } else {
            GamegineLogger.info("[SignManager] InSigns support disabled.");
        }
    }

    /**
     * Checks to see if an {@link com.stealthyone.mcb.gamegine.api.signs.ActiveGSign} object's block is actually a sign or not.
     *
     * @param sign Sign to check.
     * @return True if the block is a sign.
     *         False if the block is not a sign.
     */
    private boolean validateActiveSignBlock(ActiveGSign sign) {
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
    public boolean registerSignType(@NonNull GSignType signType) {
        String name = signType.getClass().getCanonicalName();
        if (registeredSignTypes.containsKey(name)) return false;

        registeredSignTypes.put(name, signType);
        GamegineLogger.info("[SignManager] Registered sign type '" + name + "'");

        // Register short name (optional).
        String shortName = signType.getShortName();
        if (shortName == null || shortName.isEmpty()) {
            GamegineLogger.debug("No short name set for sign type '" + name + "' - will not attempt to register it.");
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
            Set<String> pendingFiles = pendingActiveSigns.remove(name);
            if (pendingFiles != null) {
                for (String filePath : pendingFiles) {
                    loadActiveSign(new File(filePath));
                }
            }
        }
        return true;
    }

    @Override
    public GSignType getSignType(@NonNull String shortName) {
        String typeClazz = typeShortNameIndex.get(shortName.toLowerCase());
        return typeClazz == null ? null : registeredSignTypes.get(typeClazz);
    }

    @Override
    public ActiveGSign getSign(@NonNull Location location) {
        return activeSigns.get(new BlockLocation(location));
    }

    /**
     * Updates a sign's lines.
     *
     * @param sign Sign to update.
     */
    public void updateSign(ActiveGSign sign) {
        if (!validateActiveSignBlock(sign)) return;

        Sign signBlock = (Sign) sign.getLocation().getBlock().getState();
        List<String> newLines;

        GameInstance gameInstance;
        try {
            gameInstance = sign.getGame();
            newLines = new ArrayList<>(sign.getType().getLines(sign));
        } catch (IllegalStateException ex) {
            // Game not loaded.
            newLines = new ArrayList<>();

            String[] gameClassSplit = sign.getGameInstanceRef().split(":")[0].split("\\.");
            String gameName = gameClassSplit[gameClassSplit.length - 1];

            for (int i = 0; i < 4; i++) {
                newLines.set(i, ChatColor.translateAlternateColorCodes('&', gameNotFoundFormat.get(i).replace("{GAME}", gameName)));
            }
            return;
        }

        if (gameInstance != null && sign.getType().useSignVariables()) {
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
    public Collection<ActiveGSign> getActiveSigns(@NonNull Game game) {
        if (!(game instanceof InstanceGame)) throw new IllegalArgumentException("Game cannot have signs - not an instance of InstanceGame.");
        Set<ActiveGSign> signs = new LinkedHashSet<>();
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
    public Collection<GSignType> getRegisteredTypes() {
        return Collections.unmodifiableCollection(registeredSignTypes.values());
    }

    /**
     * Creates a sign.
     *
     * @param block Block that the sign will exist as.
     * @param type The type of sign to create.
     * @param game The game the sign exists for.
     * @param extraData Extra data that the sign needs.<br />
     *                  Can be null if there are no args.
     * @return True if successful.<br />
     *         False if unable to create.
     * @throws java.lang.IllegalArgumentException Thrown if the block is not a sign.
     */
    public boolean createSign(@NonNull Block block, @NonNull GSignType type, @NonNull String gameRef, @NonNull GameInstance game, Map<String, Object> extraData) {
        if (block.getType() != Material.SIGN_POST || block.getType() != Material.WALL_SIGN)
            throw new IllegalArgumentException("Block is not a sign.");

        BlockLocation location = new BlockLocation(block.getLocation());
        YamlFileManager file = new YamlFileManager(activeSignsDir + File.separator + location.toString() + ".yml");
        FileConfiguration config = file.getConfig();
        config.set("type", type.getClass().getCanonicalName());
        config.set("game", gameRef);
        config.set("location", location);
        if (extraData != null && !extraData.isEmpty()) {
            ConfigurationSection dataSec = config.createSection("extraData");
            for (Entry<String, Object> entry : extraData.entrySet()) {
                dataSec.set(entry.getKey(), entry.getValue());
            }
        }
        return loadActiveSign(file.getFile());
    }

    /**
     * Listens for InSigns's SignSendEvent and modifies it accordingly.
     */


}