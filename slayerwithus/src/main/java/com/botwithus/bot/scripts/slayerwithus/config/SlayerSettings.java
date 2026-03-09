package com.botwithus.bot.scripts.slayerwithus.config;

import com.botwithus.bot.scripts.slayerwithus.master.SlayerMaster;
import com.botwithus.bot.scripts.slayerwithus.task.SlayerTask;
import com.botwithus.bot.scripts.slayerwithus.task.SlayerTaskLocation;
import com.botwithus.bot.scripts.slayerwithus.task.SlayerTaskLocationRegistry;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class SlayerSettings {
    private static final int DEFAULT_TASK_COMBAT_AREA_RADIUS = 20;

    private boolean started;
    private SlayerMaster selectedMaster;
    private boolean pointFarmEnabled;
    private SlayerMaster bonusTaskMaster;
    private boolean priorityListEnabled;
    private boolean useNpcContact;
    private boolean bankAfterTaskCompletion;
    private boolean areaLootEnabled;
    private boolean lootAll;
    private boolean lootAllStackables;
    private boolean lootAllStackablesIgnoreValueThreshold;
    private boolean useLootValueThreshold;
    private int lootValueThreshold;
    private boolean useHerbBag;
    private boolean useUpgradedHerbBag;
    private boolean useGemBag;
    private final SlayerCombatProfile defaultCombatProfile;
    private final List<String> lootItemPatterns;
    private final EnumMap<SlayerTask, Integer> taskPriorities;
    private final EnumMap<SlayerTask, String> taskLocationSelections;
    private final Map<String, Integer> taskCombatAreaRadii;
    private final Map<String, Boolean> taskTeleportUsage;
    private final Map<String, Boolean> taskSurgeUsage;
    private final Map<String, Boolean> taskDiveUsage;
    private final EnumMap<SlayerTask, Boolean> taskCombatOverrideUsage;
    private final EnumMap<SlayerTask, SlayerCombatProfile> taskCombatProfiles;

    public SlayerSettings() {
        this(
                false,
                SlayerMaster.TURAEL,
                false,
                SlayerMaster.TURAEL,
                false,
                false,
                false,
                false,
                false,
                false,
                false,
                false,
                0,
                false,
                false,
                false,
                new SlayerCombatProfile(),
                List.of(),
                new EnumMap<>(SlayerTask.class),
                new EnumMap<>(SlayerTask.class),
                new HashMap<>(),
                new HashMap<>(),
                new HashMap<>(),
                new HashMap<>(),
                new EnumMap<>(SlayerTask.class),
                new EnumMap<>(SlayerTask.class)
        );
    }

    public SlayerSettings(boolean started, SlayerMaster selectedMaster, boolean pointFarmEnabled,
                          SlayerMaster bonusTaskMaster, boolean priorityListEnabled,
                          boolean useNpcContact,
                          boolean bankAfterTaskCompletion,
                          boolean areaLootEnabled, boolean lootAll, boolean lootAllStackables,
                          boolean lootAllStackablesIgnoreValueThreshold,
                          boolean useLootValueThreshold, int lootValueThreshold,
                          boolean useHerbBag, boolean useUpgradedHerbBag, boolean useGemBag,
                          SlayerCombatProfile defaultCombatProfile,
                          List<String> lootItemPatterns,
                          Map<SlayerTask, Integer> taskPriorities, Map<SlayerTask, String> taskLocationSelections,
                          Map<String, Integer> taskCombatAreaRadii,
                          Map<String, Boolean> taskTeleportUsage, Map<String, Boolean> taskSurgeUsage,
                          Map<String, Boolean> taskDiveUsage,
                          Map<SlayerTask, Boolean> taskCombatOverrideUsage,
                          Map<SlayerTask, SlayerCombatProfile> taskCombatProfiles) {
        this.started = started;
        this.selectedMaster = selectedMaster == null ? SlayerMaster.TURAEL : selectedMaster;
        this.pointFarmEnabled = pointFarmEnabled;
        this.bonusTaskMaster = bonusTaskMaster == null ? this.selectedMaster : bonusTaskMaster;
        this.priorityListEnabled = priorityListEnabled;
        this.useNpcContact = useNpcContact;
        this.bankAfterTaskCompletion = bankAfterTaskCompletion;
        this.areaLootEnabled = areaLootEnabled;
        this.lootAll = lootAll;
        this.lootAllStackables = lootAllStackables;
        this.lootAllStackablesIgnoreValueThreshold = lootAllStackablesIgnoreValueThreshold;
        this.useLootValueThreshold = useLootValueThreshold;
        this.lootValueThreshold = Math.max(0, lootValueThreshold);
        this.useHerbBag = useHerbBag;
        this.useUpgradedHerbBag = useUpgradedHerbBag;
        this.useGemBag = useGemBag;
        this.defaultCombatProfile = defaultCombatProfile == null ? new SlayerCombatProfile() : defaultCombatProfile.copy();
        this.lootItemPatterns = new ArrayList<>();
        if (lootItemPatterns != null) {
            lootItemPatterns.stream()
                    .filter(pattern -> pattern != null && !pattern.isBlank())
                    .map(String::trim)
                    .distinct()
                    .forEach(this.lootItemPatterns::add);
        }
        this.taskPriorities = new EnumMap<>(SlayerTask.class);
        this.taskLocationSelections = new EnumMap<>(SlayerTask.class);
        this.taskCombatAreaRadii = new HashMap<>();
        this.taskTeleportUsage = new HashMap<>();
        this.taskSurgeUsage = new HashMap<>();
        this.taskDiveUsage = new HashMap<>();
        this.taskCombatOverrideUsage = new EnumMap<>(SlayerTask.class);
        this.taskCombatProfiles = new EnumMap<>(SlayerTask.class);
        initializeDefaultTaskPriorities();
        initializeDefaultNavigationPreferences();
        initializeDefaultCombatOverrides();
        if (taskPriorities != null) {
            this.taskPriorities.putAll(taskPriorities);
        }
        if (taskLocationSelections != null) {
            this.taskLocationSelections.putAll(taskLocationSelections);
        }
        if (taskCombatAreaRadii != null) {
            this.taskCombatAreaRadii.putAll(taskCombatAreaRadii);
        }
        if (taskTeleportUsage != null) {
            this.taskTeleportUsage.putAll(taskTeleportUsage);
        }
        if (taskSurgeUsage != null) {
            this.taskSurgeUsage.putAll(taskSurgeUsage);
        }
        if (taskDiveUsage != null) {
            this.taskDiveUsage.putAll(taskDiveUsage);
        }
        if (taskCombatOverrideUsage != null) {
            this.taskCombatOverrideUsage.putAll(taskCombatOverrideUsage);
        }
        if (taskCombatProfiles != null) {
            taskCombatProfiles.forEach((task, profile) -> {
                if (task != null && task != SlayerTask.UNKNOWN && profile != null) {
                    this.taskCombatProfiles.put(task, profile.copy());
                }
            });
        }
    }

    public boolean isStarted() {
        return started;
    }

    public SlayerSettings copy() {
        return new SlayerSettings(
                started,
                selectedMaster,
                pointFarmEnabled,
                bonusTaskMaster,
                priorityListEnabled,
                useNpcContact,
                bankAfterTaskCompletion,
                areaLootEnabled,
                lootAll,
                lootAllStackables,
                lootAllStackablesIgnoreValueThreshold,
                useLootValueThreshold,
                lootValueThreshold,
                useHerbBag,
                useUpgradedHerbBag,
                useGemBag,
                defaultCombatProfile.copy(),
                lootItemPatterns,
                taskPriorities,
                taskLocationSelections,
                taskCombatAreaRadii,
                taskTeleportUsage,
                taskSurgeUsage,
                taskDiveUsage,
                taskCombatOverrideUsage,
                taskCombatProfiles
        );
    }

    public void replaceWith(SlayerSettings other) {
        if (other == null) {
            return;
        }

        SlayerSettings normalized = other.copy();
        this.started = normalized.started;
        this.selectedMaster = normalized.selectedMaster;
        this.pointFarmEnabled = normalized.pointFarmEnabled;
        this.bonusTaskMaster = normalized.bonusTaskMaster;
        this.priorityListEnabled = normalized.priorityListEnabled;
        this.useNpcContact = normalized.useNpcContact;
        this.bankAfterTaskCompletion = normalized.bankAfterTaskCompletion;
        this.areaLootEnabled = normalized.areaLootEnabled;
        this.lootAll = normalized.lootAll;
        this.lootAllStackables = normalized.lootAllStackables;
        this.lootAllStackablesIgnoreValueThreshold = normalized.lootAllStackablesIgnoreValueThreshold;
        this.useLootValueThreshold = normalized.useLootValueThreshold;
        this.lootValueThreshold = normalized.lootValueThreshold;
        this.useHerbBag = normalized.useHerbBag;
        this.useUpgradedHerbBag = normalized.useUpgradedHerbBag;
        this.useGemBag = normalized.useGemBag;
        this.defaultCombatProfile.setUseFood(normalized.defaultCombatProfile.isUseFood());
        this.defaultCombatProfile.setHealThresholdPercent(normalized.defaultCombatProfile.getHealThresholdPercent());
        this.defaultCombatProfile.setUsePrayerRestore(normalized.defaultCombatProfile.isUsePrayerRestore());
        this.defaultCombatProfile.setPrayerThresholdPercent(normalized.defaultCombatProfile.getPrayerThresholdPercent());
        this.defaultCombatProfile.setUsePrayerRenewal(normalized.defaultCombatProfile.isUsePrayerRenewal());
        this.defaultCombatProfile.setUseOverloads(normalized.defaultCombatProfile.isUseOverloads());
        this.defaultCombatProfile.setPrayerMode(normalized.defaultCombatProfile.getPrayerMode());
        this.defaultCombatProfile.setUseCurses(normalized.defaultCombatProfile.isUseCurses());
        this.defaultCombatProfile.setAttackStyle(normalized.defaultCombatProfile.getAttackStyle());
        this.defaultCombatProfile.setHybridSoulSplitHealthPercent(
                normalized.defaultCombatProfile.getHybridSoulSplitHealthPercent()
        );

        this.lootItemPatterns.clear();
        this.lootItemPatterns.addAll(normalized.lootItemPatterns);

        this.taskPriorities.clear();
        this.taskPriorities.putAll(normalized.taskPriorities);

        this.taskLocationSelections.clear();
        this.taskLocationSelections.putAll(normalized.taskLocationSelections);

        this.taskCombatAreaRadii.clear();
        this.taskCombatAreaRadii.putAll(normalized.taskCombatAreaRadii);

        this.taskTeleportUsage.clear();
        this.taskTeleportUsage.putAll(normalized.taskTeleportUsage);

        this.taskSurgeUsage.clear();
        this.taskSurgeUsage.putAll(normalized.taskSurgeUsage);

        this.taskDiveUsage.clear();
        this.taskDiveUsage.putAll(normalized.taskDiveUsage);

        this.taskCombatOverrideUsage.clear();
        this.taskCombatOverrideUsage.putAll(normalized.taskCombatOverrideUsage);

        this.taskCombatProfiles.clear();
        normalized.taskCombatProfiles.forEach((task, profile) -> {
            if (task != null && profile != null) {
                this.taskCombatProfiles.put(task, profile.copy());
            }
        });
    }

    public void setStarted(boolean started) {
        this.started = started;
    }

    public SlayerMaster getSelectedMaster() {
        return selectedMaster;
    }

    public void setSelectedMaster(SlayerMaster selectedMaster) {
        this.selectedMaster = selectedMaster == null ? SlayerMaster.TURAEL : selectedMaster;
    }

    public boolean isPointFarmEnabled() {
        return pointFarmEnabled;
    }

    public void setPointFarmEnabled(boolean pointFarmEnabled) {
        this.pointFarmEnabled = pointFarmEnabled;
    }

    public SlayerMaster getBonusTaskMaster() {
        return bonusTaskMaster;
    }

    public void setBonusTaskMaster(SlayerMaster bonusTaskMaster) {
        this.bonusTaskMaster = bonusTaskMaster == null ? selectedMaster : bonusTaskMaster;
    }

    public boolean isPriorityListEnabled() {
        return priorityListEnabled;
    }

    public void setPriorityListEnabled(boolean priorityListEnabled) {
        this.priorityListEnabled = priorityListEnabled;
    }

    public boolean isUseNpcContact() {
        return useNpcContact;
    }

    public void setUseNpcContact(boolean useNpcContact) {
        this.useNpcContact = useNpcContact;
    }

    public boolean isBankAfterTaskCompletion() {
        return bankAfterTaskCompletion;
    }

    public void setBankAfterTaskCompletion(boolean bankAfterTaskCompletion) {
        this.bankAfterTaskCompletion = bankAfterTaskCompletion;
    }

    public boolean isAreaLootEnabled() {
        return areaLootEnabled;
    }

    public void setAreaLootEnabled(boolean areaLootEnabled) {
        this.areaLootEnabled = areaLootEnabled;
    }

    public boolean isLootAll() {
        return lootAll;
    }

    public void setLootAll(boolean lootAll) {
        this.lootAll = lootAll;
    }

    public boolean isLootAllStackables() {
        return lootAllStackables;
    }

    public void setLootAllStackables(boolean lootAllStackables) {
        this.lootAllStackables = lootAllStackables;
    }

    public boolean isLootAllStackablesIgnoreValueThreshold() {
        return lootAllStackablesIgnoreValueThreshold;
    }

    public void setLootAllStackablesIgnoreValueThreshold(boolean lootAllStackablesIgnoreValueThreshold) {
        this.lootAllStackablesIgnoreValueThreshold = lootAllStackablesIgnoreValueThreshold;
    }

    public boolean isUseLootValueThreshold() {
        return useLootValueThreshold;
    }

    public void setUseLootValueThreshold(boolean useLootValueThreshold) {
        this.useLootValueThreshold = useLootValueThreshold;
    }

    public int getLootValueThreshold() {
        return lootValueThreshold;
    }

    public void setLootValueThreshold(int lootValueThreshold) {
        this.lootValueThreshold = Math.max(0, lootValueThreshold);
    }

    public boolean isUseHerbBag() {
        return useHerbBag;
    }

    public void setUseHerbBag(boolean useHerbBag) {
        this.useHerbBag = useHerbBag;
    }

    public boolean isUseUpgradedHerbBag() {
        return useUpgradedHerbBag;
    }

    public void setUseUpgradedHerbBag(boolean useUpgradedHerbBag) {
        this.useUpgradedHerbBag = useUpgradedHerbBag;
    }

    public boolean isUseGemBag() {
        return useGemBag;
    }

    public void setUseGemBag(boolean useGemBag) {
        this.useGemBag = useGemBag;
    }

    public SlayerCombatProfile getDefaultCombatProfile() {
        return defaultCombatProfile;
    }

    public SlayerCombatProfile getResolvedCombatProfile(SlayerTask task) {
        if (task == null || task == SlayerTask.UNKNOWN || !isTaskCombatOverrideEnabled(task)) {
            return defaultCombatProfile;
        }
        return taskCombatProfiles.getOrDefault(task, defaultCombatProfile);
    }

    public boolean isTaskCombatOverrideEnabled(SlayerTask task) {
        if (task == null || task == SlayerTask.UNKNOWN) {
            return false;
        }
        return taskCombatOverrideUsage.getOrDefault(task, false);
    }

    public void setTaskCombatOverrideEnabled(SlayerTask task, boolean enabled) {
        if (task == null || task == SlayerTask.UNKNOWN) {
            return;
        }
        taskCombatOverrideUsage.put(task, enabled);
    }

    public SlayerCombatProfile getTaskCombatProfile(SlayerTask task) {
        if (task == null || task == SlayerTask.UNKNOWN) {
            return defaultCombatProfile;
        }
        return taskCombatProfiles.computeIfAbsent(task, ignored -> defaultCombatProfile.copy());
    }

    public void setTaskCombatProfile(SlayerTask task, SlayerCombatProfile profile) {
        if (task == null || task == SlayerTask.UNKNOWN || profile == null) {
            return;
        }
        taskCombatProfiles.put(task, profile.copy());
    }

    public Map<SlayerTask, Boolean> getTaskCombatOverrideUsage() {
        return taskCombatOverrideUsage;
    }

    public Map<SlayerTask, SlayerCombatProfile> getTaskCombatProfiles() {
        return taskCombatProfiles;
    }

    public List<String> getLootItemPatterns() {
        return List.copyOf(lootItemPatterns);
    }

    public void setLootItemPatterns(List<String> lootItemPatterns) {
        this.lootItemPatterns.clear();
        if (lootItemPatterns == null) {
            return;
        }
        lootItemPatterns.stream()
                .filter(pattern -> pattern != null && !pattern.isBlank())
                .map(String::trim)
                .distinct()
                .forEach(this.lootItemPatterns::add);
    }

    public void addLootItemPattern(String pattern) {
        if (pattern == null) {
            return;
        }
        String normalized = pattern.trim();
        if (!normalized.isBlank() && !lootItemPatterns.contains(normalized)) {
            lootItemPatterns.add(normalized);
        }
    }

    public void removeLootItemPattern(String pattern) {
        if (pattern == null) {
            return;
        }
        lootItemPatterns.remove(pattern.trim());
    }

    public int getTaskPriority(SlayerTask task) {
        if (task == null) {
            return 0;
        }
        return taskPriorities.getOrDefault(task, 0);
    }

    public void setTaskPriority(SlayerTask task, int priority) {
        if (task == null || task == SlayerTask.UNKNOWN) {
            return;
        }
        taskPriorities.put(task, priority);
    }

    public Map<SlayerTask, Integer> getTaskPriorities() {
        return taskPriorities;
    }

    public String getSelectedTaskLocationKey(SlayerTask task) {
        if (task == null || task == SlayerTask.UNKNOWN) {
            return "";
        }

        String selectedKey = taskLocationSelections.get(task);
        if (selectedKey != null && SlayerTaskLocationRegistry.findLocation(task, selectedKey).isPresent()) {
            return selectedKey;
        }

        return SlayerTaskLocationRegistry.getLocations(task).stream()
                .findFirst()
                .map(SlayerTaskLocation::getKey)
                .orElse("");
    }

    public SlayerTaskLocation getSelectedTaskLocation(SlayerTask task) {
        return SlayerTaskLocationRegistry.findLocation(task, getSelectedTaskLocationKey(task))
                .orElse(null);
    }

    public void setSelectedTaskLocationKey(SlayerTask task, String locationKey) {
        if (task == null || task == SlayerTask.UNKNOWN) {
            return;
        }

        if (locationKey == null || locationKey.isBlank()) {
            taskLocationSelections.remove(task);
            return;
        }

        if (SlayerTaskLocationRegistry.findLocation(task, locationKey).isPresent()) {
            taskLocationSelections.put(task, locationKey);
        }
    }

    public Map<SlayerTask, String> getTaskLocationSelections() {
        return taskLocationSelections;
    }

    public int getTaskCombatAreaRadius(SlayerTask task) {
        return getTaskCombatAreaRadius(task, getSelectedTaskLocationKey(task));
    }

    public int getTaskCombatAreaRadius(SlayerTask task, String locationKey) {
        if (task == null || task == SlayerTask.UNKNOWN) {
            return DEFAULT_TASK_COMBAT_AREA_RADIUS;
        }
        return Math.max(1, taskCombatAreaRadii.getOrDefault(navigationPreferenceKey(task, locationKey), DEFAULT_TASK_COMBAT_AREA_RADIUS));
    }

    public void setTaskCombatAreaRadius(SlayerTask task, int radius) {
        setTaskCombatAreaRadius(task, getSelectedTaskLocationKey(task), radius);
    }

    public void setTaskCombatAreaRadius(SlayerTask task, String locationKey, int radius) {
        if (task == null || task == SlayerTask.UNKNOWN) {
            return;
        }
        taskCombatAreaRadii.put(navigationPreferenceKey(task, locationKey), Math.max(1, radius));
    }

    public Map<String, Integer> getTaskCombatAreaRadii() {
        return taskCombatAreaRadii;
    }

    public boolean isTaskTeleportsEnabled(SlayerTask task) {
        return isTaskTeleportsEnabled(task, getSelectedTaskLocationKey(task));
    }

    public boolean isTaskTeleportsEnabled(SlayerTask task, String locationKey) {
        if (task == null || task == SlayerTask.UNKNOWN) {
            return false;
        }
        return taskTeleportUsage.getOrDefault(navigationPreferenceKey(task, locationKey), false);
    }

    public void setTaskTeleportsEnabled(SlayerTask task, boolean enabled) {
        setTaskTeleportsEnabled(task, getSelectedTaskLocationKey(task), enabled);
    }

    public void setTaskTeleportsEnabled(SlayerTask task, String locationKey, boolean enabled) {
        if (task == null || task == SlayerTask.UNKNOWN) {
            return;
        }
        taskTeleportUsage.put(navigationPreferenceKey(task, locationKey), enabled);
    }

    public Map<String, Boolean> getTaskTeleportUsage() {
        return taskTeleportUsage;
    }

    public boolean isTaskSurgeEnabled(SlayerTask task) {
        return isTaskSurgeEnabled(task, getSelectedTaskLocationKey(task));
    }

    public boolean isTaskSurgeEnabled(SlayerTask task, String locationKey) {
        if (task == null || task == SlayerTask.UNKNOWN) {
            return false;
        }
        return taskSurgeUsage.getOrDefault(navigationPreferenceKey(task, locationKey), false);
    }

    public void setTaskSurgeEnabled(SlayerTask task, boolean enabled) {
        setTaskSurgeEnabled(task, getSelectedTaskLocationKey(task), enabled);
    }

    public void setTaskSurgeEnabled(SlayerTask task, String locationKey, boolean enabled) {
        if (task == null || task == SlayerTask.UNKNOWN) {
            return;
        }
        taskSurgeUsage.put(navigationPreferenceKey(task, locationKey), enabled);
    }

    public Map<String, Boolean> getTaskSurgeUsage() {
        return taskSurgeUsage;
    }

    public boolean isTaskDiveEnabled(SlayerTask task) {
        return isTaskDiveEnabled(task, getSelectedTaskLocationKey(task));
    }

    public boolean isTaskDiveEnabled(SlayerTask task, String locationKey) {
        if (task == null || task == SlayerTask.UNKNOWN) {
            return false;
        }
        return taskDiveUsage.getOrDefault(navigationPreferenceKey(task, locationKey), false);
    }

    public void setTaskDiveEnabled(SlayerTask task, boolean enabled) {
        setTaskDiveEnabled(task, getSelectedTaskLocationKey(task), enabled);
    }

    public void setTaskDiveEnabled(SlayerTask task, String locationKey, boolean enabled) {
        if (task == null || task == SlayerTask.UNKNOWN) {
            return;
        }
        taskDiveUsage.put(navigationPreferenceKey(task, locationKey), enabled);
    }

    public Map<String, Boolean> getTaskDiveUsage() {
        return taskDiveUsage;
    }

    private void initializeDefaultTaskPriorities() {
        for (SlayerTask task : SlayerTask.values()) {
            if (task == SlayerTask.UNKNOWN) {
                continue;
            }
            taskPriorities.put(task, Math.max(task.getId(), 0));
        }
    }

    private void initializeDefaultNavigationPreferences() {
        taskCombatAreaRadii.clear();
        taskTeleportUsage.clear();
        taskSurgeUsage.clear();
        taskDiveUsage.clear();
    }

    private void initializeDefaultCombatOverrides() {
        for (SlayerTask task : SlayerTask.values()) {
            if (task == SlayerTask.UNKNOWN) {
                continue;
            }
            taskCombatOverrideUsage.put(task, false);
            taskCombatProfiles.put(task, defaultCombatProfile.copy());
        }
    }

    private String navigationPreferenceKey(SlayerTask task, String locationKey) {
        if (task == null || task == SlayerTask.UNKNOWN) {
            return "";
        }
        String normalizedLocation = locationKey == null ? "" : locationKey.trim();
        if (normalizedLocation.isBlank()) {
            normalizedLocation = getSelectedTaskLocationKey(task);
        }
        return task.name() + "::" + normalizedLocation;
    }
}
