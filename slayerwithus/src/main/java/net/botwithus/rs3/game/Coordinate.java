package net.botwithus.rs3.game;

public record Coordinate(int x, int y, int z) {
    public int distanceTo(Coordinate other) {
        if (other == null) {
            return Integer.MAX_VALUE;
        }
        return Math.max(Math.abs(x - other.x), Math.abs(y - other.y));
    }
}
