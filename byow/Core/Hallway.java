package byow.Core;

import java.util.Random;

import static byow.Core.Direction.*;

public class Hallway {
    private int startXPos;
    private int startYPos;
    private int hallLength;
    private boolean special = false;
    private Direction direction;

    public void setUpHallway(Room room, IntRange length, IntRange roomWidth, IntRange roomHeight,
                             int columns, int rows, boolean first, Random r) {

        direction = intToDirection(r.nextInt(4));

        Direction oppositeDirection;
        if (room.getEnteringHallway() == null) {
            oppositeDirection = null;
        } else {
            oppositeDirection = intToDirection((directionToInt(room.getEnteringHallway()) + 2) % 4);
        }

        if (!first && direction == oppositeDirection
                || room.getSpecial() && direction == room.getEnteringHallway()) {
            //rotate the direction 90 degrees clockwise
            int directionInt = directionToInt(direction);
            directionInt += 1;
            directionInt = directionInt % 4;
            direction = intToDirection(directionInt);
        }

        hallLength = length.random();
        int maxLength = length.max;

        switch (direction) {
            case NORTH:
                IntRange rN = new IntRange(room.getxPos(),
                        room.getxPos() + room.getRoomWidth() - 1, r);
                startXPos = rN.random();
                startYPos = room.getyPos() + room.getRoomHeight();
                maxLength = rows - startYPos - roomHeight.min;
                break;
            case EAST:
                startXPos = room.getxPos() + room.getRoomWidth();
                IntRange rE = new IntRange(room.getyPos(),
                        room.getyPos() + room.getRoomHeight() - 1, r);
                startYPos = rE.random();
                maxLength = columns - startXPos - roomWidth.min;
                break;
            case SOUTH:
                IntRange rS = new IntRange(room.getxPos(), room.getxPos() + room.getRoomWidth(), r);
                startXPos = rS.random();
                startYPos = room.getyPos();
                maxLength = startYPos - roomHeight.min;
                break;
            case WEST:
                startXPos = room.getxPos();
                IntRange rW = new IntRange(room.getyPos(),
                        room.getyPos() + room.getRoomHeight(), r);
                startYPos = rW.random();
                maxLength = startXPos - roomWidth.min;
                break;
            default:
                break;
        }
        hallLength = clampHelper(hallLength, 2, maxLength); // min equals 2 to accommodate walls
    }

    public int endPositionX() {
        if (direction == SOUTH || direction == NORTH) {
            return startXPos;
        } else if (direction == EAST) {
            return startXPos + hallLength - 1;
        }
        return startXPos - hallLength + 1;
    }

    public int endPositionY() {
        if (direction == EAST || direction == WEST) {
            return startYPos;
        } else if (direction == NORTH) {
            return startYPos + hallLength - 1;
        }
        return startYPos - hallLength + 1;
    }

    private int directionToInt(Direction d) {
        switch (d) {
            case NORTH: return 0;
            case EAST: return 1;
            case SOUTH: return 2;
            case WEST: return 3;
            default: return 3;
        }
    }

    private Direction intToDirection(int directionNumber) {
        switch (directionNumber) {
            case 0: return NORTH;
            case 1: return EAST;
            case 2: return SOUTH;
            case 3: return WEST;
            default: return WEST;
        }
    }

    private int clampHelper(int value, int min, int max) {
        if (value > max) {
            return max;
        } else if (value < min) {
            return min;
        } else {
            return value;
        }
    }

    public int getStartXPos() {
        return this.startXPos;
    }

    public int getStartYPos() {
        return this.startYPos;
    }

    public int getHallLength() {
        return this.hallLength;
    }

    public boolean getSpecial() {
        return this.special;
    }

    public Direction getDirection() {
        return this.direction;
    }

    public void setSpecial(Boolean b) {
        this.special = b;
    }
}
