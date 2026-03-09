package com.botwithus.bot.scripts.slayerwithus.task;

import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Optional;

public final class SlayerTaskLocationRegistry {
    private static final EnumMap<SlayerTask, List<SlayerTaskLocation>> LOCATIONS = loadLocations();

    private SlayerTaskLocationRegistry() {
    }

    public static List<SlayerTaskLocation> getLocations(SlayerTask task) {
        if (task == null) {
            return List.of();
        }
        return LOCATIONS.getOrDefault(task, List.of());
    }

    public static Optional<SlayerTaskLocation> findLocation(SlayerTask task, String key) {
        if (task == null || key == null || key.isBlank()) {
            return Optional.empty();
        }

        return getLocations(task).stream()
                .filter(location -> location.getKey().equals(key))
                .findFirst();
    }

    private static EnumMap<SlayerTask, List<SlayerTaskLocation>> loadLocations() {
        EnumMap<SlayerTask, List<SlayerTaskLocation>> locations = new EnumMap<>(SlayerTask.class);
        for (SlayerTaskLocationDefinition definition : SlayerTaskLocationDefinition.values()) {
            locations.computeIfAbsent(definition.getTask(), ignored -> new java.util.ArrayList<>())
                    .add(definition.getLocation());
        }
        for (SlayerTask task : locations.keySet()) {
            locations.put(task, Collections.unmodifiableList(locations.get(task)));
        }
        return locations;
    }
}
