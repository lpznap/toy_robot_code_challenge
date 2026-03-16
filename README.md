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

### Setup — navigate to the project and prepare the wrapper

```bash
cd path/to/toy-robot
```

On **Mac/Linux**, make the wrapper executable (first time only):
```bash
chmod +x gradlew
```

On **Windows**, use `gradlew.bat` in place of `./gradlew` in all commands below.

---

### Option 1 — Interactive stdin

The simplest way to start. Launches the application and waits for you to type commands directly in the terminal, one per line. Useful for exploring and testing manually.

```bash
./gradlew bootRun
```

You will see:
```
INFO c.t.runner.RobotCommandRunner - Starting interactive stdin session
INFO c.t.runner.RobotCommandRunner - Toy Robot Simulator — enter commands (Ctrl+D / Ctrl+Z to quit)
INFO c.t.runner.RobotCommandRunner -   Commands: PLACE X,Y,F | MOVE | LEFT | RIGHT | REPORT
```

Type your commands:
```
PLACE 0,0,NORTH
MOVE
REPORT
```

Expected output:
```
INFO c.t.runner.RobotCommandRunner - Output: 0,1,NORTH
```

Press **Ctrl+D** (Mac/Linux) or **Ctrl+Z then Enter** (Windows) to end the session.

---

### Option 2 — From a commands file (Gradle)

Pass a text file containing commands via the `-PinputFile` Gradle property. Each line in the file is one command. This is the most convenient option during development as it does not require building a JAR first.

```bash
./gradlew bootRun -PinputFile=src/test/resources/example-a.txt
```

How it works internally: the `bootRun` task in `build.gradle` reads the `inputFile` property and passes it as the `input.file` system property to the JVM. `RobotCommandRunner` then picks this up and reads from the file instead of stdin.

---

### Option 3 — Build and run the JAR

Use this option when you want a standalone executable that can be shared or deployed without the project source.

#### 3a — Build using the terminal

```bash
./gradlew build
```

This compiles all sources, runs all 22 tests, and packages the application. After a successful build you will see:
```
BUILD SUCCESSFUL in Xs
```

#### 3b — Build using IntelliJ IDEA (no terminal needed)

If you are not comfortable with the terminal, you can build the JAR entirely inside IntelliJ IDEA:

**Step 1 — Open the project:**
Open IntelliJ IDEA → click **Open** → select the `toy-robot` folder → click **OK**.
IntelliJ detects `build.gradle` and sets everything up automatically.

**Step 2 — Open the Gradle panel:**
Look on the **right side** of IntelliJ for a panel labelled **Gradle**. Click it to open it.

**Step 3 — Run the build task:**
Expand the folders inside the Gradle panel:
```
toy-robot
  └── Tasks
        └── build
              └── build   ← double-click this
```
Double-click **build**. IntelliJ will compile the code and create the JAR automatically.

**Step 4 — Confirm the build succeeded:**
At the bottom of IntelliJ you will see:
```
BUILD SUCCESSFUL in Xs
```

---

After either build method, two JAR files are produced in `build/libs/`:
```
build/libs/toy-robot-1.0.0.jar        ← executable fat JAR — contains all dependencies (use this)
build/libs/toy-robot-1.0.0-plain.jar  ← plain JAR — no dependencies bundled (do not use directly)
```

The **fat JAR** (`toy-robot-1.0.0.jar`) bundles Spring Boot, Lombok-generated code, and all other libraries into a single file. It can be run on any machine that has Java 17+ — no Gradle, no IntelliJ, no project folder needed.

---

**Once you have the JAR, choose how to run it:**

**Step 1 — Run interactively (stdin):**

Starts the application and waits for commands typed in the terminal.
```bash
java -jar build/libs/toy-robot-1.0.0.jar
```
Type commands and press **Ctrl+D** (Mac/Linux) or **Ctrl+Z then Enter** (Windows) to quit.

**Step 2 — Run with a commands file (pass as CLI argument):**

The first argument after the JAR path is treated as a file path to read commands from.
```bash
java -jar build/libs/toy-robot-1.0.0.jar src/test/resources/example-a.txt
```

**Step 3 — Run with a commands file (pass as system property):**

An alternative to the CLI argument, useful in scripts or CI pipelines where you want to set the file path separately from the JAR path.
```bash
java -Dinput.file=src/test/resources/example-a.txt -jar build/libs/toy-robot-1.0.0.jar
```

Note: `-D` system properties must appear **before** `-jar` in the command.

**Step 4 — Copy the JAR anywhere and run it independently:**

The fat JAR is fully self-contained. You can copy it to any folder or machine and run it with only Java 17+ installed — no Gradle, no `src/` folder, no `build.gradle` needed.
```bash
cp build/libs/toy-robot-1.0.0.jar ~/Desktop/toy-robot.jar
java -jar ~/Desktop/toy-robot.jar path/to/commands.txt
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
