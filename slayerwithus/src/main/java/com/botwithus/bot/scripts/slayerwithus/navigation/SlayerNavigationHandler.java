package com.botwithus.bot.scripts.slayerwithus.navigation;

import com.botwithus.bot.api.GameAPI;
import com.botwithus.bot.scripts.slayerwithus.SlayerWithUs;
import com.botwithus.bot.scripts.slayerwithus.master.SlayerMaster;
import com.botwithus.bot.scripts.slayerwithus.task.SlayerTask;
import com.botwithus.bot.scripts.slayerwithus.task.SlayerTaskLocation;
import com.botwithus.bot.scripts.slayerwithus.util.WalkHelper;
import net.botwithus.rs3.game.Coordinate;

public final class SlayerNavigationHandler {
    private final GameAPI api;

    public SlayerNavigationHandler(GameAPI api) {
        this.api = api;
    }

    public Coordinate resolveMasterLocation(SlayerMaster master, SlayerWithUs script) {
        return master == null ? null : master.getLocation();
    }

    public SlayerTaskLocation resolveTaskLocation(SlayerTask task, SlayerTaskLocation configured, SlayerWithUs script) {
        return configured;
    }

    public boolean isAt(Coordinate coordinate) {
        return WalkHelper.isAt(api, coordinate, 3);
    }

    public boolean isAt(SlayerTaskLocation location, int radius) {
        return location != null && WalkHelper.isAt(api, location.getCoordinate(), radius);
    }

    public boolean moveTo(SlayerTask task, SlayerTaskLocation location, int radius, SlayerWithUs script) {
        if (location == null || location.getCoordinate() == null) {
            return false;
        }
        if (WalkHelper.isAt(api, location.getCoordinate(), radius)) {
            return true;
        }
        return WalkHelper.stepTowards(api, location.getCoordinate());
    }

    public boolean moveTo(Coordinate coordinate, int radius) {
        if (coordinate == null) {
            return false;
        }
        if (WalkHelper.isAt(api, coordinate, radius)) {
            return true;
        }
        return WalkHelper.stepTowards(api, coordinate);
    }
}
