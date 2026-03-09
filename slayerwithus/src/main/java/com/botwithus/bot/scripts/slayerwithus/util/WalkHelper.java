package com.botwithus.bot.scripts.slayerwithus.util;

import com.botwithus.bot.api.GameAPI;
import com.botwithus.bot.api.inventory.ActionTypes;
import com.botwithus.bot.api.model.GameAction;
import com.botwithus.bot.api.model.LocalPlayer;
import net.botwithus.rs3.game.Coordinate;

public final class WalkHelper {
    private static final int MAX_STEP = 14;

    private WalkHelper() {
    }

    public static boolean isAt(GameAPI api, Coordinate destination, int radius) {
        LocalPlayer player = api.getLocalPlayer();
        if (player == null || destination == null || player.plane() != destination.z()) {
            return false;
        }
        int dx = Math.abs(player.tileX() - destination.x());
        int dy = Math.abs(player.tileY() - destination.y());
        return Math.max(dx, dy) <= Math.max(0, radius);
    }

    public static boolean stepTowards(GameAPI api, Coordinate destination) {
        LocalPlayer player = api.getLocalPlayer();
        if (player == null || destination == null || player.plane() != destination.z()) {
            return false;
        }
        int dx = destination.x() - player.tileX();
        int dy = destination.y() - player.tileY();
        int distance = Math.max(Math.abs(dx), Math.abs(dy));
        if (distance <= 1) {
            return true;
        }
        int step = Math.min(MAX_STEP, distance);
        int targetX = player.tileX() + dx * step / distance;
        int targetY = player.tileY() + dy * step / distance;
        api.queueAction(new GameAction(ActionTypes.WALK, 1, targetX, targetY));
        return true;
    }
}
