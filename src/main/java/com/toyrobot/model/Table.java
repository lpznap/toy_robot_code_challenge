package com.toyrobot.model;

import com.toyrobot.utils.Direction;
import com.toyrobot.utils.Position;
import lombok.Getter;

@Getter
public class Table {

    private final int size;

    public Table() {
        this(5);
    }

    public Table(int size) {
        if (size <= 0) {
            throw new IllegalArgumentException(
                "Table size must be greater than 0, got: " + size);
        }
        this.size = size;
    }

    public int getMaxCoord() { return size - 1; }

    public boolean contains(Position position) {
        return contains(position.getX(), position.getY());
    }

    public boolean contains(int x, int y) {
        return x >= 0 && x <= getMaxCoord()
            && y >= 0 && y <= getMaxCoord();
    }

    public Position nextPosition(Position current, Direction direction) {
        Position next = current.step(direction);

        return contains(next) ? next : null;
    }

    @Override
    public String toString() {
        return "Table{size=" + size + "x" + size + "}";
    }
}
