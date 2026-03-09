package com.botwithus.bot.scripts.slayerwithus.config;

import com.botwithus.bot.scripts.slayerwithus.master.SlayerMaster;
import com.botwithus.bot.scripts.slayerwithus.task.SlayerTask;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public final class SlayerConfiguration {
    private static final String STARTED_KEY = "started";
    private static final String SELECTED_MASTER_KEY = "selectedMaster";
    private static final String POINT_FARM_ENABLED_KEY = "pointFarmEnabled";
    private static final String BONUS_TASK_MASTER_KEY = "bonusTaskMaster";
    private static final String PRIORITY_LIST_ENABLED_KEY = "priorityListEnabled";
    private static final String USE_NPC_CONTACT_KEY = "useNpcContact";
    private static final String BANK_AFTER_TASK_COMPLETION_KEY = "bankAfterTaskCompletion";
    private static final String AREA_LOOT_ENABLED_KEY = "areaLootEnabled";
    private static final String LOOT_ALL_KEY = "lootAll";
    private static final String LOOT_ALL_STACKABLES_KEY = "lootAllStackables";
    private static final String LOOT_ALL_STACKABLES_IGNORE_VALUE_THRESHOLD_KEY = "lootAllStackablesIgnoreValueThreshold";
    private static final String USE_LOOT_VALUE_THRESHOLD_KEY = "useLootValueThreshold";
    private static final String LOOT_VALUE_THRESHOLD_KEY = "lootValueThreshold";
    private static final String USE_HERB_BAG_KEY = "useHerbBag";
    private static final String USE_UPGRADED_HERB_BAG_KEY = "useUpgradedHerbBag";
    private static final String USE_GEM_BAG_KEY = "useGemBag";
    private static final String COMBAT_PROFILE_KEY = "defaultCombatProfile";
    private static final String LOOT_ITEM_PATTERNS_KEY = "lootItemPatterns";
    private static final String TASK_PRIORITIES_KEY = "taskPriorities";
    private static final String TASK_LOCATION_SELECTIONS_KEY = "taskLocationSelections";
    private static final String TASK_COMBAT_AREA_RADII_KEY = "taskCombatAreaRadii";
    private static final String TASK_TELEPORT_USAGE_KEY = "taskTeleportUsage";
    private static final String TASK_SURGE_USAGE_KEY = "taskSurgeUsage";
    private static final String TASK_DIVE_USAGE_KEY = "taskDiveUsage";
    private static final String TASK_COMBAT_OVERRIDE_USAGE_KEY = "taskCombatOverrideUsage";
    private static final String TASK_COMBAT_PROFILES_KEY = "taskCombatProfiles";

    private final Path path;

    public SlayerConfiguration() {
        this(Paths.get(System.getProperty("user.home"), "BotWithUs", "settings", "SlayerWithUs.properties"));
    }

    SlayerConfiguration(Path path) {
        this.path = path;
    }

    public SlayerSettings load() {
        SlayerSettings settings = new SlayerSettings();
        if (!Files.exists(path)) {
            return settings;
        }

        Properties properties = new Properties();
        try (InputStream in = Files.newInputStream(path)) {
            properties.load(in);
        } catch (IOException ignored) {
            return settings;
        }

        settings.setStarted(Boolean.parseBoolean(properties.getProperty(STARTED_KEY, "false")));
        settings.setSelectedMaster(parseMaster(properties.getProperty(SELECTED_MASTER_KEY), settings.getSelectedMaster()));
        settings.setPointFarmEnabled(Boolean.parseBoolean(properties.getProperty(POINT_FARM_ENABLED_KEY, "false")));
        settings.setBonusTaskMaster(parseMaster(properties.getProperty(BONUS_TASK_MASTER_KEY), settings.getBonusTaskMaster()));
        settings.setPriorityListEnabled(Boolean.parseBoolean(properties.getProperty(PRIORITY_LIST_ENABLED_KEY, "false")));
        settings.setUseNpcContact(Boolean.parseBoolean(properties.getProperty(USE_NPC_CONTACT_KEY, "false")));
        settings.setBankAfterTaskCompletion(Boolean.parseBoolean(properties.getProperty(BANK_AFTER_TASK_COMPLETION_KEY, "false")));
        settings.setAreaLootEnabled(Boolean.parseBoolean(properties.getProperty(AREA_LOOT_ENABLED_KEY, "false")));
        settings.setLootAll(Boolean.parseBoolean(properties.getProperty(LOOT_ALL_KEY, "false")));
        settings.setLootAllStackables(Boolean.parseBoolean(properties.getProperty(LOOT_ALL_STACKABLES_KEY, "false")));
        settings.setLootAllStackablesIgnoreValueThreshold(Boolean.parseBoolean(
                properties.getProperty(LOOT_ALL_STACKABLES_IGNORE_VALUE_THRESHOLD_KEY, "false")));
        settings.setUseLootValueThreshold(Boolean.parseBoolean(properties.getProperty(USE_LOOT_VALUE_THRESHOLD_KEY, "false")));
        settings.setLootValueThreshold(parseInt(properties.getProperty(LOOT_VALUE_THRESHOLD_KEY), settings.getLootValueThreshold()));
        settings.setUseHerbBag(Boolean.parseBoolean(properties.getProperty(USE_HERB_BAG_KEY, "false")));
        settings.setUseUpgradedHerbBag(Boolean.parseBoolean(properties.getProperty(USE_UPGRADED_HERB_BAG_KEY, "false")));
        settings.setUseGemBag(Boolean.parseBoolean(properties.getProperty(USE_GEM_BAG_KEY, "false")));
        readCombatProfile(settings.getDefaultCombatProfile(), properties.getProperty(COMBAT_PROFILE_KEY));

        settings.setLootItemPatterns(split(properties.getProperty(LOOT_ITEM_PATTERNS_KEY)));
        readTaskIntegerMap(properties.getProperty(TASK_PRIORITIES_KEY), settings::setTaskPriority);
        readTaskStringMap(properties.getProperty(TASK_LOCATION_SELECTIONS_KEY), settings::setSelectedTaskLocationKey);
        readTaskLocationIntegerMap(properties.getProperty(TASK_COMBAT_AREA_RADII_KEY), settings::setTaskCombatAreaRadius);
        readTaskLocationBooleanMap(properties.getProperty(TASK_TELEPORT_USAGE_KEY), settings::setTaskTeleportsEnabled);
        readTaskLocationBooleanMap(properties.getProperty(TASK_SURGE_USAGE_KEY), settings::setTaskSurgeEnabled);
        readTaskLocationBooleanMap(properties.getProperty(TASK_DIVE_USAGE_KEY), settings::setTaskDiveEnabled);
        readTaskBooleanMap(properties.getProperty(TASK_COMBAT_OVERRIDE_USAGE_KEY), settings::setTaskCombatOverrideEnabled);
        readTaskCombatProfiles(properties.getProperty(TASK_COMBAT_PROFILES_KEY), settings);
        return settings;
    }

    public void save(SlayerSettings settings) {
        Properties properties = new Properties();
        properties.setProperty(STARTED_KEY, Boolean.toString(settings.isStarted()));
        properties.setProperty(SELECTED_MASTER_KEY, settings.getSelectedMaster().name());
        properties.setProperty(POINT_FARM_ENABLED_KEY, Boolean.toString(settings.isPointFarmEnabled()));
        properties.setProperty(BONUS_TASK_MASTER_KEY, settings.getBonusTaskMaster().name());
        properties.setProperty(PRIORITY_LIST_ENABLED_KEY, Boolean.toString(settings.isPriorityListEnabled()));
        properties.setProperty(USE_NPC_CONTACT_KEY, Boolean.toString(settings.isUseNpcContact()));
        properties.setProperty(BANK_AFTER_TASK_COMPLETION_KEY, Boolean.toString(settings.isBankAfterTaskCompletion()));
        properties.setProperty(AREA_LOOT_ENABLED_KEY, Boolean.toString(settings.isAreaLootEnabled()));
        properties.setProperty(LOOT_ALL_KEY, Boolean.toString(settings.isLootAll()));
        properties.setProperty(LOOT_ALL_STACKABLES_KEY, Boolean.toString(settings.isLootAllStackables()));
        properties.setProperty(LOOT_ALL_STACKABLES_IGNORE_VALUE_THRESHOLD_KEY,
                Boolean.toString(settings.isLootAllStackablesIgnoreValueThreshold()));
        properties.setProperty(USE_LOOT_VALUE_THRESHOLD_KEY, Boolean.toString(settings.isUseLootValueThreshold()));
        properties.setProperty(LOOT_VALUE_THRESHOLD_KEY, Integer.toString(settings.getLootValueThreshold()));
        properties.setProperty(USE_HERB_BAG_KEY, Boolean.toString(settings.isUseHerbBag()));
        properties.setProperty(USE_UPGRADED_HERB_BAG_KEY, Boolean.toString(settings.isUseUpgradedHerbBag()));
        properties.setProperty(USE_GEM_BAG_KEY, Boolean.toString(settings.isUseGemBag()));
        properties.setProperty(COMBAT_PROFILE_KEY, writeCombatProfile(settings.getDefaultCombatProfile()));
        properties.setProperty(LOOT_ITEM_PATTERNS_KEY, String.join(";", settings.getLootItemPatterns()));
        properties.setProperty(TASK_PRIORITIES_KEY, writeTaskIntMap(settings.getTaskPriorities()));
        properties.setProperty(TASK_LOCATION_SELECTIONS_KEY, writeTaskStringMap(settings.getTaskLocationSelections()));
        properties.setProperty(TASK_COMBAT_AREA_RADII_KEY, writeStringIntMap(settings.getTaskCombatAreaRadii()));
        properties.setProperty(TASK_TELEPORT_USAGE_KEY, writeStringBooleanMap(settings.getTaskTeleportUsage()));
        properties.setProperty(TASK_SURGE_USAGE_KEY, writeStringBooleanMap(settings.getTaskSurgeUsage()));
        properties.setProperty(TASK_DIVE_USAGE_KEY, writeStringBooleanMap(settings.getTaskDiveUsage()));
        properties.setProperty(TASK_COMBAT_OVERRIDE_USAGE_KEY, writeTaskBooleanMap(settings.getTaskCombatOverrideUsage()));
        properties.setProperty(TASK_COMBAT_PROFILES_KEY, writeTaskCombatProfiles(settings));

        try {
            Files.createDirectories(path.getParent());
            try (OutputStream out = Files.newOutputStream(path)) {
                properties.store(out, "SlayerWithUs headless settings");
            }
        } catch (IOException ignored) {
        }
    }

    private static int parseInt(String raw, int fallback) {
        try {
            return raw == null ? fallback : Integer.parseInt(raw);
        } catch (NumberFormatException e) {
            return fallback;
        }
    }

    private static SlayerMaster parseMaster(String raw, SlayerMaster fallback) {
        try {
            return raw == null ? fallback : SlayerMaster.valueOf(raw);
        } catch (IllegalArgumentException e) {
            return fallback;
        }
    }

    private static List<String> split(String raw) {
        if (raw == null || raw.isBlank()) {
            return List.of();
        }
        return List.of(raw.split(";")).stream()
                .map(String::trim)
                .filter(value -> !value.isBlank())
                .distinct()
                .toList();
    }

    private static String writeCombatProfile(SlayerCombatProfile profile) {
        return String.join(",",
                Boolean.toString(profile.isUseFood()),
                Integer.toString(profile.getHealThresholdPercent()),
                Boolean.toString(profile.isUsePrayerRestore()),
                Integer.toString(profile.getPrayerThresholdPercent()),
                Boolean.toString(profile.isUsePrayerRenewal()),
                Boolean.toString(profile.isUseOverloads()),
                profile.getPrayerMode().name(),
                Boolean.toString(profile.isUseCurses()),
                profile.getAttackStyle().name(),
                Integer.toString(profile.getHybridSoulSplitHealthPercent()));
    }

    private static void readCombatProfile(SlayerCombatProfile profile, String raw) {
        if (raw == null || raw.isBlank()) {
            return;
        }
        String[] values = raw.split(",", -1);
        if (values.length != 10) {
            return;
        }
        profile.setUseFood(Boolean.parseBoolean(values[0]));
        profile.setHealThresholdPercent(parseInt(values[1], profile.getHealThresholdPercent()));
        profile.setUsePrayerRestore(Boolean.parseBoolean(values[2]));
        profile.setPrayerThresholdPercent(parseInt(values[3], profile.getPrayerThresholdPercent()));
        profile.setUsePrayerRenewal(Boolean.parseBoolean(values[4]));
        profile.setUseOverloads(Boolean.parseBoolean(values[5]));
        try {
            profile.setPrayerMode(SlayerPrayerMode.valueOf(values[6]));
        } catch (IllegalArgumentException ignored) {
        }
        profile.setUseCurses(Boolean.parseBoolean(values[7]));
        try {
            profile.setAttackStyle(SlayerAttackStyle.valueOf(values[8]));
        } catch (IllegalArgumentException ignored) {
        }
        profile.setHybridSoulSplitHealthPercent(parseInt(values[9], profile.getHybridSoulSplitHealthPercent()));
    }

    private static String writeTaskIntMap(Map<SlayerTask, Integer> values) {
        return values.entrySet().stream()
                .filter(entry -> entry.getKey() != null && entry.getValue() != null)
                .map(entry -> entry.getKey().name() + "=" + entry.getValue())
                .reduce((left, right) -> left + ";" + right)
                .orElse("");
    }

    private static String writeTaskBooleanMap(Map<SlayerTask, Boolean> values) {
        return values.entrySet().stream()
                .filter(entry -> entry.getKey() != null && entry.getValue() != null)
                .map(entry -> entry.getKey().name() + "=" + entry.getValue())
                .reduce((left, right) -> left + ";" + right)
                .orElse("");
    }

    private static String writeTaskStringMap(Map<SlayerTask, String> values) {
        return values.entrySet().stream()
                .filter(entry -> entry.getKey() != null && entry.getValue() != null && !entry.getValue().isBlank())
                .map(entry -> entry.getKey().name() + "=" + entry.getValue())
                .reduce((left, right) -> left + ";" + right)
                .orElse("");
    }

    private static String writeStringIntMap(Map<String, Integer> values) {
        return values.entrySet().stream()
                .filter(entry -> entry.getKey() != null && !entry.getKey().isBlank() && entry.getValue() != null)
                .map(entry -> entry.getKey() + "=" + entry.getValue())
                .reduce((left, right) -> left + ";" + right)
                .orElse("");
    }

    private static String writeStringBooleanMap(Map<String, Boolean> values) {
        return values.entrySet().stream()
                .filter(entry -> entry.getKey() != null && !entry.getKey().isBlank() && entry.getValue() != null)
                .map(entry -> entry.getKey() + "=" + entry.getValue())
                .reduce((left, right) -> left + ";" + right)
                .orElse("");
    }

    private static String writeTaskCombatProfiles(SlayerSettings settings) {
        return settings.getTaskCombatProfiles().entrySet().stream()
                .filter(entry -> entry.getKey() != null && entry.getValue() != null)
                .map(entry -> entry.getKey().name() + "=" + writeCombatProfile(entry.getValue()))
                .reduce((left, right) -> left + ";" + right)
                .orElse("");
    }

    private static void readTaskBooleanMap(String raw, TaskBooleanConsumer consumer) {
        if (raw == null || raw.isBlank()) {
            return;
        }
        for (String entry : raw.split(";")) {
            String[] parts = entry.split("=", 2);
            if (parts.length != 2) {
                continue;
            }
            try {
                consumer.accept(SlayerTask.valueOf(parts[0]), Boolean.parseBoolean(parts[1]));
            } catch (IllegalArgumentException ignored) {
            }
        }
    }

    private static void readTaskIntegerMap(String raw, TaskIntegerConsumer consumer) {
        if (raw == null || raw.isBlank()) {
            return;
        }
        for (String entry : raw.split(";")) {
            String[] parts = entry.split("=", 2);
            if (parts.length != 2) {
                continue;
            }
            try {
                consumer.accept(SlayerTask.valueOf(parts[0]), Integer.parseInt(parts[1]));
            } catch (IllegalArgumentException ignored) {
            }
        }
    }

    private static void readTaskStringMap(String raw, TaskStringConsumer consumer) {
        if (raw == null || raw.isBlank()) {
            return;
        }
        for (String entry : raw.split(";")) {
            String[] parts = entry.split("=", 2);
            if (parts.length != 2) {
                continue;
            }
            try {
                consumer.accept(SlayerTask.valueOf(parts[0]), parts[1]);
            } catch (IllegalArgumentException ignored) {
            }
        }
    }

    private static void readTaskLocationIntegerMap(String raw, TaskLocationIntegerConsumer consumer) {
        if (raw == null || raw.isBlank()) {
            return;
        }
        for (String entry : raw.split(";")) {
            String[] parts = entry.split("=", 2);
            if (parts.length != 2) {
                continue;
            }
            String[] key = parts[0].split("::", 2);
            if (key.length != 2) {
                continue;
            }
            try {
                consumer.accept(SlayerTask.valueOf(key[0]), key[1], Integer.parseInt(parts[1]));
            } catch (IllegalArgumentException ignored) {
            }
        }
    }

    private static void readTaskLocationBooleanMap(String raw, TaskLocationBooleanConsumer consumer) {
        if (raw == null || raw.isBlank()) {
            return;
        }
        for (String entry : raw.split(";")) {
            String[] parts = entry.split("=", 2);
            if (parts.length != 2) {
                continue;
            }
            String[] key = parts[0].split("::", 2);
            if (key.length != 2) {
                continue;
            }
            try {
                consumer.accept(SlayerTask.valueOf(key[0]), key[1], Boolean.parseBoolean(parts[1]));
            } catch (IllegalArgumentException ignored) {
            }
        }
    }

    private static void readTaskCombatProfiles(String raw, SlayerSettings settings) {
        if (raw == null || raw.isBlank()) {
            return;
        }
        for (String entry : raw.split(";")) {
            String[] parts = entry.split("=", 2);
            if (parts.length != 2) {
                continue;
            }
            try {
                SlayerTask task = SlayerTask.valueOf(parts[0]);
                SlayerCombatProfile profile = settings.getTaskCombatProfile(task);
                readCombatProfile(profile, parts[1]);
            } catch (IllegalArgumentException ignored) {
            }
        }
    }

    @FunctionalInterface
    private interface TaskBooleanConsumer {
        void accept(SlayerTask task, boolean value);
    }

    @FunctionalInterface
    private interface TaskIntegerConsumer {
        void accept(SlayerTask task, int value);
    }

    @FunctionalInterface
    private interface TaskStringConsumer {
        void accept(SlayerTask task, String value);
    }

    @FunctionalInterface
    private interface TaskLocationIntegerConsumer {
        void accept(SlayerTask task, String locationKey, int value);
    }

    @FunctionalInterface
    private interface TaskLocationBooleanConsumer {
        void accept(SlayerTask task, String locationKey, boolean value);
    }
}
