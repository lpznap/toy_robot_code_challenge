package com.toyrobot.utils;

public enum Direction {

    // Enum constants are declared in CLOCKWISE order.
    // This ordering is intentional — it maps directly to indices 0,1,2,3
    // used by the CLOCKWISE array for O(1) rotation.
    NORTH,  // index 0 — faces up (+Y)
    EAST,   // index 1 — faces right (+X)
    SOUTH,  // index 2 — faces down (-Y)
    WEST;   // index 3 — faces left (-X)

    private static final Direction[] CLOCKWISE = {NORTH, EAST, SOUTH, WEST};

    public Direction turnLeft() {
        // ordinal() gives this direction's index in the CLOCKWISE array (0–3)
        // Adding CLOCKWISE.length before subtracting prevents a negative modulo result
        int idx = (ordinal() + CLOCKWISE.length - 1) % CLOCKWISE.length;
        return CLOCKWISE[idx];
    }

    public Direction turnRight() {
        // Adding 1 and mod-ing wraps WEST (3) back to NORTH (0)
        int idx = (ordinal() + 1) % CLOCKWISE.length;
        return CLOCKWISE[idx];
    }

    public int deltaX() {
        // Java 17 switch expression — returns a value directly
        return switch (this) {
            case EAST  ->  1;   // Moving east increases X
            case WEST  -> -1;   // Moving west decreases X
            default    ->  0;   // NORTH and SOUTH have no X movement
        };
    }

    public int deltaY() {
        return switch (this) {
            case NORTH ->  1;   // Moving north increases Y
            case SOUTH -> -1;   // Moving south decreases Y
            default    ->  0;   // EAST and WEST have no Y movement
        };
    }
}
