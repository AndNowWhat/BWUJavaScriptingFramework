package com.botwithus.bot.scripts.slayerwithus.task;

import net.botwithus.rs3.game.Coordinate;

import java.util.List;

public final class SlayerTaskLocation {
    private final String key;
    private final String displayName;
    private final Coordinate coordinate;
    private final List<String> monsterNames;

    public SlayerTaskLocation(String key, String displayName, Coordinate coordinate) {
        this(key, displayName, coordinate, List.of());
    }

    public SlayerTaskLocation(String key, String displayName, Coordinate coordinate, List<String> monsterNames) {
        this.key = key;
        this.displayName = displayName;
        this.coordinate = coordinate;
        this.monsterNames = monsterNames == null ? List.of() : List.copyOf(monsterNames);
    }

    public String getKey() {
        return key;
    }

    public String getDisplayName() {
        return displayName;
    }

    public Coordinate getCoordinate() {
        return coordinate;
    }

    public List<String> getMonsterNames() {
        return monsterNames;
    }
}
