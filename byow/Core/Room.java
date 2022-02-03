package byow.Core;

import java.util.Random;

import static byow.Core.Direction.NORTH;

public class Room {
    private int xPos;
    private int yPos;
    private int roomWidth;
    private int roomHeight;
    private boolean special = false;
    private Direction enteringHallway;

    public void setUpRoom(IntRange widthRange, IntRange heightRange, int columns, int rows) {
        roomWidth = widthRange.random();
        roomHeight = heightRange.random();

        xPos = (columns / 2 - roomWidth / 2);
        yPos = (rows / 2 - roomHeight / 2);
    }

    public void setUpRoom(IntRange widthRange, IntRange heightRange,
                          int columns, int rows, Hallway hallway, Random r) {
        if (hallway.getSpecial()) {
            enteringHallway = hallway.getDirection();
            roomHeight = 2;
            roomWidth = 2;
            xPos = hallway.endPositionX();
            yPos = hallway.endPositionY();
            special = true;
            return;
        }
        enteringHallway = hallway.getDirection();

        roomHeight = heightRange.random();
        roomWidth = widthRange.random();

        switch (enteringHallway) {
            case NORTH:
                roomHeight = clampHelper(roomHeight, 1, rows - hallway.endPositionY());
                yPos = hallway.endPositionY();

                int tempMin = hallway.endPositionX() - roomWidth + 1;
                int tempMax = hallway.endPositionX();
                xPos = r.nextInt(tempMax - tempMin) + tempMin;
                xPos = clampHelper(xPos, 0, columns - roomWidth);
                break;
            case EAST:
                roomWidth = clampHelper(roomWidth, 1, columns - hallway.endPositionX());
                xPos = hallway.endPositionX();

                tempMin = hallway.endPositionY() - roomHeight + 1;
                tempMax = hallway.endPositionY();
                yPos = r.nextInt(tempMax - tempMin) + tempMin;
                yPos = clampHelper(yPos, 0, rows - roomHeight);
                break;
            case SOUTH:
                roomHeight = clampHelper(roomHeight, 1, hallway.endPositionY());
                yPos = hallway.endPositionY() - roomHeight + 1;

                tempMin = hallway.endPositionX() - roomWidth + 1;
                tempMax = hallway.endPositionX();
                xPos = r.nextInt(tempMax - tempMin) + tempMin;
                xPos = clampHelper(xPos, 0, columns - roomWidth);
                break;
            case WEST:
                roomWidth = clampHelper(roomWidth, 1, hallway.endPositionX());
                xPos = hallway.endPositionX() - roomWidth + 1;

                tempMin = hallway.endPositionY() - roomHeight + 1;
                tempMax = hallway.endPositionY();
                yPos = r.nextInt(tempMax - tempMin) + tempMin;
                yPos = clampHelper(yPos, 0, rows - roomHeight);
                break;
            default:
                break;
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

    public int getxPos() {
        return this.xPos;
    }

    public int getyPos() {
        return this.yPos;
    }

    public int getRoomWidth() {
        return this.roomWidth;
    }

    public int getRoomHeight() {
        return this.roomHeight;
    }

    public boolean getSpecial() {
        return this.special;
    }

    public Direction getEnteringHallway() {
        return this.enteringHallway;
    }
}
