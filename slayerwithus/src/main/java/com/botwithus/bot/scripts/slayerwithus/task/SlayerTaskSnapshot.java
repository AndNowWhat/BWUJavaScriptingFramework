package com.botwithus.bot.scripts.slayerwithus.task;

public record SlayerTaskSnapshot(
        int taskId,
        SlayerTask task,
        String taskName,
        int masterValue,
        int killsRemaining,
        int points,
        int streak
) {
    public String toStatusLine() {
        return taskName + " | master=" + masterValue
                + " | remaining=" + killsRemaining
                + " | points=" + points
                + " | streak=" + streak;
    }
}
