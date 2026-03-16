package com.toyrobot.utils;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@AllArgsConstructor
@EqualsAndHashCode
public final class Position {

    private final int x;
    private final int y;

    public Position step(Direction direction) {
        return new Position(x + direction.deltaX(), y + direction.deltaY());
    }

    @Override
    public String toString() {
        return x + "," + y;
    }
}
