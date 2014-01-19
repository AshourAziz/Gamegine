package com.stealthyone.mcb.gamegine.backend.players;

import com.stealthyone.mcb.gamegine.backend.games.Game;
import com.stealthyone.mcb.stbukkitlib.lib.storage.YamlFileManager;
import org.apache.commons.lang.Validate;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class GgPlayerFile extends YamlFileManager {

    private String uuid;
    private long lastAccessed;

    /* Point-related */
    private double availablePoints;
    private double totalPoints;
    private Map<String, Double> earnedPoints = new HashMap<>(); //Game Unique ID, points from game

    public GgPlayerFile(String filePath) {
        super(filePath);
        reloadConfig();
    }

    public GgPlayerFile(File file) {
        super(file);
        reloadConfig();
    }

    @Override
    public void reloadConfig() {
        FileConfiguration config = getConfig();
        earnedPoints.clear();

        uuid = getFile().getName().replace(".yml", "");

        lastAccessed = System.currentTimeMillis();

        availablePoints = config.getDouble("points.availablePoints", 0D);
        totalPoints = config.getDouble("points.totalPoints");
        for (String gameId : config.getConfigurationSection("points.earnedPoints").getKeys(false)) {
            earnedPoints.put(gameId, config.getDouble("points.earnedPoints." + gameId));
        }
    }

    @Override
    public void saveFile() {
        FileConfiguration config = getConfig();

        config.set("points.availablePoints", availablePoints);
        config.set("points.totalPoints", totalPoints);
        for (Entry<String, Double> entry : earnedPoints.entrySet()) {
            config.set("points.earnedPoints." + entry.getKey(), entry.getValue());
        }

        super.saveFile();
    }

    public String getUuid() {
        return uuid;
    }

    public long getLastAccessed() {
        return lastAccessed;
    }

    public void updateAccessed() {
        lastAccessed = System.currentTimeMillis();
    }

    public double getAvailablePoints() {
        updateAccessed();
        return getConfig().getDouble("points.availablePoints");
    }

    public double getTotalPoints() {
        updateAccessed();
        return getConfig().getDouble("points.totalPoints");
    }

    public Map<String, Double> getEarnedPoints() {
        return earnedPoints;
    }

    public void awardPoints(double points, Game game) {
        Validate.notNull(game, "Game cannot be null");
        if (points < 1) throw new IllegalArgumentException("Points must be greater than 0");

        Double currentPoints = earnedPoints.get(game.getUniqueId());
        if (currentPoints == null) currentPoints = 0D;
        earnedPoints.put(game.getUniqueId(), currentPoints + points);
    }

    public boolean unawardPoints(double points, Game game) {
        Validate.notNull(game, "Game cannot be null");
        if (points < 1) throw new IllegalArgumentException("Points must be greater than 0");

        Double currentPoints = earnedPoints.get(game.getUniqueId());
        if (currentPoints == null) currentPoints = 0D;
        double newValue = currentPoints - points;
        if (newValue < 0) {
            return false;
        } else {
            earnedPoints.put(game.getUniqueId(), newValue);
            return true;
        }
    }

    public boolean withdrawPoints(double points) {
        if (points < 1) throw new IllegalArgumentException("Points must be greater than 0");

        double newValue = availablePoints - points;
        if (newValue < 0) {
            return false;
        } else {
            availablePoints = newValue;
            return true;
        }
    }

    public void depositPoints(double points) {
        if (points < 1) throw new IllegalArgumentException("Points must be greater than 0");

        availablePoints += points;
    }

}