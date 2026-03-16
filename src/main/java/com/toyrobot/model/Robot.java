package com.toyrobot.model;

import com.toyrobot.utils.Direction;
import com.toyrobot.utils.Position;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
public class Robot {

    private Position position;

    @Setter
    private Direction facing;

    private boolean placed;

    // -------------------------------------------------------------------------
    // Placement
    // -------------------------------------------------------------------------
    public void place(Position position, Direction facing) {
        this.position = position;
        this.facing   = facing;
        this.placed   = true;
    }

    // -------------------------------------------------------------------------
    // Movement
    // -------------------------------------------------------------------------
    public void moveTo(Position newPosition) {
        this.position = newPosition;
    }

    // -------------------------------------------------------------------------
    // Report
    // -------------------------------------------------------------------------
    public String report() {
        return position.toString() + "," + facing.name();
    }

    @Override
    public String toString() {
        return placed ? report() : "Robot(not placed)";
    }
}
