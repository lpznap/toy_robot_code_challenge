package com.toyrobot.service;

import com.toyrobot.utils.Direction;
import com.toyrobot.utils.Position;
import com.toyrobot.model.Robot;
import com.toyrobot.model.Table;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class RobotService {

    private final Table table;
    private final Robot robot;

    // -------------------------------------------------------------------------
    // Public command API
    // -------------------------------------------------------------------------
    public String execute(String rawLine) {
        if (rawLine == null) return null;

        String line = rawLine.strip();

        if (line.isEmpty()) {
            log.trace("Skipping blank");
            return null;
        }

        log.debug("Executing command: [{}]", line);

        String upper = line.toUpperCase();

        if (upper.startsWith("PLACE")) {
            return executePlaceCommand(line);
        }

        return switch (upper) {
            case "MOVE"   -> { executeMove();   yield null; }
            case "LEFT"   -> { executeLeft();   yield null; }
            case "RIGHT"  -> { executeRight();  yield null; }
            case "REPORT" -> executeReport();
            default       -> {
                log.warn("Unknown command ignored: [{}]", line);
                yield null;
            }
        };
    }

    // -------------------------------------------------------------------------
    // Individual command handlers
    // -------------------------------------------------------------------------
    private String executePlaceCommand(String line) {
        String[] parts = line.split("\\s+", 2);

        if (parts.length < 2) {
            log.warn("PLACE command missing arguments, ignored: [{}]", line);
            return null;
        }

        String[] args = parts[1].split(",");

        if (args.length != 3) {
            log.warn("PLACE command expects X,Y,F — got [{}], ignored", parts[1]);
            return null;
        }

        try {
            int x = Integer.parseInt(args[0].strip());
            int y = Integer.parseInt(args[1].strip());
            Direction dir = Direction.valueOf(args[2].strip().toUpperCase());

            Position target = new Position(x, y);

            if (table.contains(target)) {
                robot.place(target, dir);
                log.info("Robot placed at ({},{}) facing {}", x, y, dir);
            } else {
                log.warn("PLACE ignored — position ({},{}) is outside the {}x{} table",
                        x, y, table.getSize(), table.getSize());
            }

        } catch (IllegalArgumentException e) {
            log.warn("PLACE command has malformed arguments [{}], ignored: {}", line, e.getMessage());
        }

        return null;
    }

    private void executeMove() {
        if (!robot.isPlaced()) {
            log.debug("MOVE ignored — robot has not been placed yet");
            return;
        }

        Position next = table.nextPosition(robot.getPosition(), robot.getFacing());

        if (next != null) {
            log.info("Robot moved from ({},{}) to ({},{})",
                    robot.getPosition().getX(), robot.getPosition().getY(),
                    next.getX(), next.getY());
            robot.moveTo(next);
        } else {
            log.warn("MOVE ignored — robot at ({},{}) facing {} would fall off the table",
                    robot.getPosition().getX(), robot.getPosition().getY(), robot.getFacing());
        }
    }

    private void executeLeft() {
        if (!robot.isPlaced()) {
            log.debug("LEFT ignored — robot has not been placed yet");
            return;
        }

        Direction before = robot.getFacing();
        robot.setFacing(robot.getFacing().turnLeft());
        log.info("Robot turned LEFT: {} -> {}", before, robot.getFacing());
    }

    private void executeRight() {
        if (!robot.isPlaced()) {
            log.debug("RIGHT ignored — robot has not been placed yet");
            return;
        }

        Direction before = robot.getFacing();
        robot.setFacing(robot.getFacing().turnRight());
        log.info("Robot turned RIGHT: {} -> {}", before, robot.getFacing());
    }

    private String executeReport() {
        if (!robot.isPlaced()) {
            log.debug("REPORT ignored — robot has not been placed yet");
            return null;
        }

        String report = robot.report();
        log.info("REPORT: {}", report);
        return report;
    }
}
