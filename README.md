# Toy Robot Simulator

A Spring Boot console application that simulates a toy robot moving on a 5×5 table top.
Commands are read from standard input or a file, and the robot's current position and facing direction are reported on demand via the `REPORT` command.

---

## Commands

| Command | Syntax | Description |
|---|---|---|
| `PLACE` | `PLACE X,Y,F` | Places the robot at column `X`, row `Y`, facing direction `F`. Valid directions: `NORTH`, `SOUTH`, `EAST`, `WEST`. Must be called before any other command. |
| `MOVE` | `MOVE` | Moves the robot one unit forward in the direction it is currently facing. Ignored if the move would take it off the table. |
| `LEFT` | `LEFT` | Rotates the robot 90° counter-clockwise. Does **not** change its position — only its facing direction. |
| `RIGHT` | `RIGHT` | Rotates the robot 90° clockwise. Does **not** change its position — only its facing direction. |
| `REPORT` | `REPORT` | Outputs the robot's current position and facing in the format `X,Y,FACING` (e.g. `3,2,NORTH`). Ignored if the robot has not been placed yet. |

**Additional input rules:**
- Commands are **case-insensitive** — `place 0,0,north` works the same as `PLACE 0,0,NORTH`.
- **Blank lines** are skipped.
- There is **no space** between `PLACE` and its arguments is allowed only with a single space: `PLACE X,Y,F`.

---

## How to Run

**<mark>Once you have the JAR, choose how to run it:</mark>**
<br>
🔴 <ins>***The JAR file is already provided in the root directory of the project. (build/libs/toy-robot-1.0.0.jar)*** </ins>

**Step 1 — Run interactively (stdin):**

Starts the application and waits for commands typed in the terminal one by one.
```bash
java -jar build/libs/toy-robot-1.0.0.jar
```
Type commands and press **Ctrl+D** (Mac/Linux) or **Ctrl+Z then Enter** (Windows) to quit.

Example session:
```
INFO  c.t.runner.RobotCommandRunner - Starting interactive stdin session
INFO  c.t.runner.RobotCommandRunner - Toy Robot Simulator — enter commands (Ctrl+D / Ctrl+Z to quit)
INFO  c.t.runner.RobotCommandRunner -   Commands: PLACE X,Y,F | MOVE | LEFT | RIGHT | REPORT

PLACE 0,0,NORTH
INFO  c.t.service.RobotService - Robot placed at (0,0) facing NORTH
MOVE
INFO  c.t.service.RobotService - Robot moved from (0,0) to (0,1)
REPORT
INFO  c.t.runner.RobotCommandRunner - Output: 0,1,NORTH
```

**Step 2 — Run with a commands file (pass as CLI argument):**

The first argument after the JAR path is treated as a file path to read commands from.
```bash
java -jar build/libs/toy-robot-1.0.0.jar src/test/resources/example-a.txt
```

Expected output:
```
INFO  c.t.runner.RobotCommandRunner - Input source: CLI argument -> src/test/resources/example-a.txt
INFO  c.t.runner.RobotCommandRunner - Processing commands from file: src/test/resources/example-a.txt
INFO  c.t.service.RobotService      - Robot placed at (0,0) facing NORTH
INFO  c.t.service.RobotService      - Robot moved from (0,0) to (0,1)
INFO  c.t.service.RobotService      - REPORT: 0,1,NORTH
INFO  c.t.runner.RobotCommandRunner - Output: 0,1,NORTH
INFO  c.t.runner.RobotCommandRunner - Processing complete — 3 line(s) read, 1 REPORT output(s) produced
```

**Step 3 — Run with a commands file (pass as system property):**

An alternative to the CLI argument, useful in scripts or CI pipelines where you want to set the file path separately from the JAR path.
```bash
java -Dinput.file=src/test/resources/example-a.txt -jar build/libs/toy-robot-1.0.0.jar
```

Note: `-D` system properties must appear **before** `-jar` in the command. Output is identical to Step 2.

**Step 4 — Copy the JAR anywhere and run it independently:**

The fat JAR is fully self-contained. Copy it to any folder or machine and run it with only Java 17+ installed — no Gradle, no `src/` folder, no `build.gradle` needed.
```bash
cp build/libs/toy-robot-1.0.0.jar ~/Desktop/toy-robot.jar
java -jar ~/Desktop/toy-robot.jar src/test/resources/example-c.txt
```

Expected output:
```
INFO  c.t.runner.RobotCommandRunner - Input source: CLI argument -> src/test/resources/example-c.txt
INFO  c.t.runner.RobotCommandRunner - Processing commands from file: src/test/resources/example-c.txt
INFO  c.t.service.RobotService      - Robot placed at (1,2) facing EAST
INFO  c.t.service.RobotService      - Robot moved from (1,2) to (2,2)
INFO  c.t.service.RobotService      - Robot moved from (2,2) to (3,2)
INFO  c.t.service.RobotService      - Robot turned LEFT: EAST -> NORTH
INFO  c.t.service.RobotService      - Robot moved from (3,2) to (3,3)
INFO  c.t.service.RobotService      - REPORT: 3,3,NORTH
INFO  c.t.runner.RobotCommandRunner - Output: 3,3,NORTH
INFO  c.t.runner.RobotCommandRunner - Processing complete — 6 line(s) read, 1 REPORT output(s) produced
```

---

## Example Files

Four example command files are provided in `src/test/resources/`. Each demonstrates a different scenario.

| File | Commands | Expected Output | What it tests |
|---|---|---|---|
| `example-a.txt` | `PLACE 0,0,NORTH` → `MOVE` → `REPORT` | `0,1,NORTH` | Basic placement and movement northward |
| `example-b.txt` | `PLACE 0,0,NORTH` → `LEFT` → `REPORT` | `0,0,WEST` | Rotation without movement |
| `example-c.txt` | `PLACE 1,2,EAST` → `MOVE` → `MOVE` → `LEFT` → `MOVE` → `REPORT` | `3,3,NORTH` | Combined movement and rotation |
| `example-boundary.txt` | `PLACE 0,0,SOUTH` → `MOVE` → `REPORT` | `0,0,SOUTH` | Fall-off prevention at table edge |

Run any of them directly:
```bash
./gradlew bootRun -PinputFile=src/test/resources/example-c.txt
```

Or with the JAR:
```bash
java -jar build/libs/toy-robot-1.0.0.jar src/test/resources/example-c.txt
```

---
