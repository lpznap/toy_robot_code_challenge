package com.toyrobot.service;

import com.toyrobot.model.Robot;
import com.toyrobot.model.Table;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class RobotServiceTest {

    private RobotService service;

    @BeforeEach
    void setUp() {
        Table table = new Table();
        Robot robot = new Robot();
        service = new RobotService(table, robot);
    }

    // =========================================================================
    // Specification examples
    // =========================================================================

    @Nested
    @DisplayName("Specification examples")
    class SpecificationExamples {

        @Test
        @DisplayName("Example a: PLACE 0,0,NORTH → MOVE → REPORT = 0,1,NORTH")
        void exampleA() {
            service.execute("PLACE 0,0,NORTH");
            service.execute("MOVE");
            String report = service.execute("REPORT");
            assertThat(report).isEqualTo("0,1,NORTH");
        }

        @Test
        @DisplayName("Example b: PLACE 0,0,NORTH → LEFT → REPORT = 0,0,WEST")
        void exampleB() {
            service.execute("PLACE 0,0,NORTH");
            service.execute("LEFT");
            String report = service.execute("REPORT");
            assertThat(report).isEqualTo("0,0,WEST");
        }

        @Test
        @DisplayName("Example c: PLACE 1,2,EAST → MOVE MOVE LEFT MOVE → REPORT = 3,3,NORTH")
        void exampleC() {
            service.execute("PLACE 1,2,EAST");
            service.execute("MOVE");
            service.execute("MOVE");
            service.execute("LEFT");
            service.execute("MOVE");
            String report = service.execute("REPORT");
            assertThat(report).isEqualTo("3,3,NORTH");
        }
    }

    // =========================================================================
    // Boundary / fall-off prevention
    // =========================================================================

    @Nested
    @DisplayName("Fall-off prevention")
    class FallOffPrevention {

        @Test
        @DisplayName("MOVE from north edge (y=4) heading NORTH is ignored")
        void moveOffNorthEdge() {
            service.execute("PLACE 2,4,NORTH");
            service.execute("MOVE");
            assertThat(service.execute("REPORT")).isEqualTo("2,4,NORTH");
        }

        @Test
        @DisplayName("MOVE from south edge (y=0) heading SOUTH is ignored")
        void moveOffSouthEdge() {
            service.execute("PLACE 2,0,SOUTH");
            service.execute("MOVE");
            assertThat(service.execute("REPORT")).isEqualTo("2,0,SOUTH");
        }

        @Test
        @DisplayName("MOVE from east edge (x=4) heading EAST is ignored")
        void moveOffEastEdge() {
            service.execute("PLACE 4,2,EAST");
            service.execute("MOVE");
            assertThat(service.execute("REPORT")).isEqualTo("4,2,EAST");
        }

        @Test
        @DisplayName("MOVE from west edge (x=0) heading WEST is ignored")
        void moveOffWestEdge() {
            service.execute("PLACE 0,2,WEST");
            service.execute("MOVE");
            assertThat(service.execute("REPORT")).isEqualTo("0,2,WEST");
        }

        @Test
        @DisplayName("PLACE outside table boundaries is ignored")
        void placeOutsideTable() {
            service.execute("PLACE 5,5,NORTH");  // invalid — out of bounds
            assertThat(service.execute("REPORT")).isNull();
        }

        @Test
        @DisplayName("PLACE at negative coordinates is ignored")
        void placeAtNegativeCoords() {
            service.execute("PLACE -1,0,NORTH");
            assertThat(service.execute("REPORT")).isNull();
        }

        @Test
        @DisplayName("Multiple moves stop at the wall and further moves are allowed")
        void multipleMovesStopAtWall() {
            service.execute("PLACE 3,0,EAST");
            service.execute("MOVE"); // x=4
            service.execute("MOVE"); // ignored (would go to x=5)
            service.execute("MOVE"); // ignored
            assertThat(service.execute("REPORT")).isEqualTo("4,0,EAST");
        }
    }

    // =========================================================================
    // Commands before first valid PLACE
    // =========================================================================

    @Nested
    @DisplayName("Commands before first valid PLACE are ignored")
    class BeforePlaced {

        @Test
        @DisplayName("MOVE before PLACE returns null")
        void moveBeforePlaceIsIgnored() {
            service.execute("MOVE");
            assertThat(service.execute("REPORT")).isNull();
        }

        @Test
        @DisplayName("LEFT before PLACE is ignored")
        void leftBeforePlaceIsIgnored() {
            service.execute("LEFT");
            assertThat(service.execute("REPORT")).isNull();
        }

        @Test
        @DisplayName("RIGHT before PLACE is ignored")
        void rightBeforePlaceIsIgnored() {
            service.execute("RIGHT");
            assertThat(service.execute("REPORT")).isNull();
        }

        @Test
        @DisplayName("REPORT before PLACE returns null")
        void reportBeforePlaceIsIgnored() {
            assertThat(service.execute("REPORT")).isNull();
        }

        @Test
        @DisplayName("Commands before an invalid PLACE are still ignored")
        void commandsBeforeInvalidPlaceThenValidPlace() {
            service.execute("MOVE");
            service.execute("PLACE 9,9,NORTH"); // invalid — out of bounds
            service.execute("MOVE");             // still not placed — ignored
            assertThat(service.execute("REPORT")).isNull();

            service.execute("PLACE 2,2,EAST");  // valid
            assertThat(service.execute("REPORT")).isEqualTo("2,2,EAST");
        }
    }

    // =========================================================================
    // Re-PLACE mid-sequence
    // =========================================================================

    @Nested
    @DisplayName("Re-PLACE mid-sequence")
    class RePlaceMidSequence {

        @Test
        @DisplayName("Second PLACE overwrites the first")
        void secondPlaceOverwritesFirst() {
            service.execute("PLACE 0,0,NORTH");
            service.execute("MOVE");
            service.execute("PLACE 3,3,SOUTH");
            assertThat(service.execute("REPORT")).isEqualTo("3,3,SOUTH");
        }
    }

    // =========================================================================
    // Rotations
    // =========================================================================

    @Nested
    @DisplayName("Rotations — full 360° cycles")
    class Rotations {

        @Test
        @DisplayName("Four LEFT turns bring the robot back to NORTH")
        void fourLeftTurns() {
            service.execute("PLACE 2,2,NORTH");
            service.execute("LEFT");
            service.execute("LEFT");
            service.execute("LEFT");
            service.execute("LEFT");
            assertThat(service.execute("REPORT")).isEqualTo("2,2,NORTH");
        }

        @Test
        @DisplayName("Four RIGHT turns bring the robot back to NORTH")
        void fourRightTurns() {
            service.execute("PLACE 2,2,NORTH");
            service.execute("RIGHT");
            service.execute("RIGHT");
            service.execute("RIGHT");
            service.execute("RIGHT");
            assertThat(service.execute("REPORT")).isEqualTo("2,2,NORTH");
        }

        @Test
        @DisplayName("NORTH → RIGHT = EAST → RIGHT = SOUTH → RIGHT = WEST → RIGHT = NORTH")
        void rightRotationSequence() {
            service.execute("PLACE 2,2,NORTH");
            service.execute("RIGHT");
            assertThat(service.execute("REPORT")).isEqualTo("2,2,EAST");
            service.execute("RIGHT");
            assertThat(service.execute("REPORT")).isEqualTo("2,2,SOUTH");
            service.execute("RIGHT");
            assertThat(service.execute("REPORT")).isEqualTo("2,2,WEST");
            service.execute("RIGHT");
            assertThat(service.execute("REPORT")).isEqualTo("2,2,NORTH");
        }

        @Test
        @DisplayName("NORTH → LEFT = WEST → LEFT = SOUTH → LEFT = EAST → LEFT = NORTH")
        void leftRotationSequence() {
            service.execute("PLACE 2,2,NORTH");
            service.execute("LEFT");
            assertThat(service.execute("REPORT")).isEqualTo("2,2,WEST");
            service.execute("LEFT");
            assertThat(service.execute("REPORT")).isEqualTo("2,2,SOUTH");
            service.execute("LEFT");
            assertThat(service.execute("REPORT")).isEqualTo("2,2,EAST");
            service.execute("LEFT");
            assertThat(service.execute("REPORT")).isEqualTo("2,2,NORTH");
        }
    }

    // =========================================================================
    // Input robustness
    // =========================================================================

    @Nested
    @DisplayName("Input robustness")
    class InputRobustness {

        @Test
        @DisplayName("Null input is handled gracefully")
        void nullInput() {
            assertThat(service.execute(null)).isNull();
        }

        @Test
        @DisplayName("Blank / whitespace-only lines are ignored")
        void blankLines() {
            service.execute("PLACE 1,1,NORTH");
            service.execute("   ");
            service.execute("");
            assertThat(service.execute("REPORT")).isEqualTo("1,1,NORTH");
        }

        @Test
        @DisplayName("Comment lines (starting with #) are ignored")
        void commentLines() {
            service.execute("PLACE 1,1,NORTH");
            service.execute("# This is a comment");
            assertThat(service.execute("REPORT")).isEqualTo("1,1,NORTH");
        }

        @Test
        @DisplayName("Unknown commands are silently ignored")
        void unknownCommands() {
            service.execute("PLACE 1,1,NORTH");
            service.execute("FLY");
            service.execute("JUMP 2,2");
            assertThat(service.execute("REPORT")).isEqualTo("1,1,NORTH");
        }

        @Test
        @DisplayName("PLACE with malformed args is ignored")
        void malformedPlace() {
            service.execute("PLACE abc,def,NORTH");
            assertThat(service.execute("REPORT")).isNull();
        }

        @Test
        @DisplayName("PLACE with wrong number of args is ignored")
        void placeWrongArgCount() {
            service.execute("PLACE 1,2");      // missing direction
            service.execute("PLACE 1,2,NORTH,EXTRA");  // too many
            assertThat(service.execute("REPORT")).isNull();
        }

        @Test
        @DisplayName("PLACE with invalid direction string is ignored")
        void placeInvalidDirection() {
            service.execute("PLACE 1,2,DIAGONAL");
            assertThat(service.execute("REPORT")).isNull();
        }

        @Test
        @DisplayName("Commands are case-insensitive for PLACE direction")
        void placeDirectionCaseInsensitive() {
            service.execute("PLACE 1,1,north");
            assertThat(service.execute("REPORT")).isEqualTo("1,1,NORTH");
        }

        @Test
        @DisplayName("Commands are case-insensitive for MOVE/LEFT/RIGHT/REPORT")
        void commandsCaseInsensitive() {
            service.execute("place 0,0,north");
            service.execute("move");
            service.execute("left");
            String report = service.execute("report");
            assertThat(report).isEqualTo("0,1,WEST");
        }
    }

    // =========================================================================
    // Corner-placement moves
    // =========================================================================

    @Nested
    @DisplayName("Corner placements and movements")
    class CornerPlacements {

        @Test
        @DisplayName("Robot placed in SW corner (0,0) can only move NORTH or EAST")
        void swCorner() {
            service.execute("PLACE 0,0,SOUTH");
            service.execute("MOVE"); // ignored — would fall south
            assertThat(service.execute("REPORT")).isEqualTo("0,0,SOUTH");

            service.execute("RIGHT"); // now facing WEST
            service.execute("MOVE"); // ignored — would fall west
            assertThat(service.execute("REPORT")).isEqualTo("0,0,WEST");
        }

        @Test
        @DisplayName("Robot placed in NE corner (4,4) can only move SOUTH or WEST")
        void neCorner() {
            service.execute("PLACE 4,4,NORTH");
            service.execute("MOVE"); // ignored — would fall north
            assertThat(service.execute("REPORT")).isEqualTo("4,4,NORTH");

            service.execute("RIGHT"); // now facing EAST
            service.execute("MOVE"); // ignored — would fall east
            assertThat(service.execute("REPORT")).isEqualTo("4,4,EAST");
        }
    }
}
