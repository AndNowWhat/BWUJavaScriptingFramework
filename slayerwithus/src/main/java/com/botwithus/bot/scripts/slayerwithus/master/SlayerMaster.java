package com.botwithus.bot.scripts.slayerwithus.master;

import net.botwithus.rs3.game.Coordinate;

import java.util.Arrays;

public enum SlayerMaster {
    TURAEL("Turael", new Coordinate(2890, 3547, 0)),
    SPRIA("Spria", new Coordinate(2890, 3547, 0)),
    JACQUELYN("Jacquelyn", new Coordinate(3222, 3224, 0)),
    VANNAKA("Vannaka", new Coordinate(3092, 3478, 0)),
    THE_RAPTOR("The Raptor", new Coordinate(3294, 3543, 0)),
    MAZCHNA("Mazchna", new Coordinate(3510, 3504, 0)),
    ACHTRYN("Achtryn", new Coordinate(3510, 3504, 0)),
    CHAELDAR("Chaeldar", new Coordinate(2446, 4430, 0)),
    SUMONA("Sumona", new Coordinate(3359, 2994, 0)),
    LAPALOK("Lapalok", new Coordinate(2868, 2981, 1)),
    DURADEL("Duradel", new Coordinate(2868, 2981, 1)),
    KURADAL("Kuradal", new Coordinate(1688, 5287, 1)),
    MORVRAN("Morvran", new Coordinate(2197, 3327, 1)),
    LANIAKEA("Laniakea", new Coordinate(5460, 2353, 0)),
    MANDRITH("Mandrith", new Coordinate(3054, 3952, 0));

    private final String displayName;
    private final Coordinate location;

    SlayerMaster(String displayName, Coordinate location) {
        this.displayName = displayName;
        this.location = location;
    }

    public String getDisplayName() {
        return displayName;
    }

    public Coordinate getLocation() {
        return location;
    }

    public static SlayerMaster fromDisplayName(String displayName) {
        return Arrays.stream(values())
                .filter(master -> master.displayName.equals(displayName))
                .findFirst()
                .orElse(TURAEL);
    }

    public static String[] displayNames() {
        return Arrays.stream(values()).map(SlayerMaster::getDisplayName).toArray(String[]::new);
    }
}
