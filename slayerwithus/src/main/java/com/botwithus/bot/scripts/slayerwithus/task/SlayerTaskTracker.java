package com.botwithus.bot.scripts.slayerwithus.task;

import com.botwithus.bot.api.GameAPI;

import java.util.function.Consumer;

public final class SlayerTaskTracker {
    private static final int SLAYER_TASK_VARP = 185;
    private static final int SLAYER_MASTER_VARP = 9072;
    private static final int SLAYER_KILLS_LEFT_VARP = 183;
    private static final int SLAYER_POINTS_VARBIT = 9071;
    private static final int SLAYER_STREAK_VARP = 10077;

    private final GameAPI api;
    private int lastLoggedTaskId = Integer.MIN_VALUE;

    public SlayerTaskTracker(GameAPI api) {
        this.api = api;
    }

    public SlayerTaskSnapshot snapshot() {
        int taskId = getCurrentTaskId();
        SlayerTask task = SlayerTask.fromId(taskId);
        String taskName = task == SlayerTask.UNKNOWN && taskId == 0
                ? SlayerTask.NOTHING.getDisplayName()
                : task.getDisplayName();
        return new SlayerTaskSnapshot(
                taskId,
                task,
                taskName,
                getCurrentMasterValue(),
                getCurrentKillsRemaining(),
                getSlayerPoints(),
                getCurrentStreak()
        );
    }

    public int getCurrentTaskId() {
        return api.getVarp(SLAYER_TASK_VARP);
    }

    public SlayerTask getCurrentTask() {
        return SlayerTask.fromId(getCurrentTaskId());
    }

    public String getCurrentTaskName() {
        return snapshot().taskName();
    }

    public int getCurrentMasterValue() {
        return api.getVarp(SLAYER_MASTER_VARP);
    }

    public int getCurrentKillsRemaining() {
        return api.getVarp(SLAYER_KILLS_LEFT_VARP);
    }

    public int getSlayerPoints() {
        return api.getVarbit(SLAYER_POINTS_VARBIT);
    }

    public int getCurrentStreak() {
        return api.getVarp(SLAYER_STREAK_VARP);
    }

    public boolean hasNoActiveTask() {
        SlayerTask currentTask = getCurrentTask();
        boolean hasAssignedTask = currentTask != SlayerTask.UNKNOWN && currentTask != SlayerTask.NOTHING;
        return !hasAssignedTask || getCurrentKillsRemaining() <= 0;
    }

    public void logTaskIfChanged(Consumer<String> logger) {
        int currentTaskId = getCurrentTaskId();
        if (currentTaskId != lastLoggedTaskId) {
            lastLoggedTaskId = currentTaskId;
            logger.accept("Slayer Task: " + getCurrentTaskName());
        }
    }
}
