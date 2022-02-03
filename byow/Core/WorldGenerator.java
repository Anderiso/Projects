package byow.Core;

import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.util.Random;


public class WorldGenerator {
    private long seed;
    private Random R;

    private int columns;
    private int rows;
    //control the margins for HUD
    private int lowerBlankSpace = 3;
    private int verticalBlankSpace = 5;
    private IntRange numRooms;
    private IntRange roomWidth;
    private IntRange roomHeight;
    private IntRange hallwayLength;
    private IntRange specialDecider;

    private TETile[][] world;
    private TETile[][] trueWorld;
    private TETile[][] generatedWorld;
    private Room[] rooms;
    private Hallway[] hallways;

    public WorldGenerator(int width, int height, String s) {
        seed = Long.parseLong(s);
        R = new Random(seed);

        generatedWorld = new TETile[width][height];
        height = height - verticalBlankSpace;

        columns = width - 2;
        rows = height - 2;
        world = new TETile[columns + 1][rows + 1];
        trueWorld = new TETile[width][height];


        numRooms = new IntRange(50, 55, R);
        roomWidth = new IntRange(4, 8, R);
        roomHeight = new IntRange(4, 8, R);
        hallwayLength = new IntRange(8, 20, R);
        specialDecider = new IntRange(0, 3, R);


        fillWithBlankTiles(world);
        createRoomsAndHallways();
        setRoomTiles();
        setHallwayTiles();
        setTrueWorld();
        buildWall();
        setGeneratedWorld();
    }

    public TETile[][] getWorld() {
        return this.generatedWorld;
    }

    public static void fillWithBlankTiles(TETile[][] tiles) {
        int height = tiles[0].length;
        int width = tiles.length;
        for (int x = 0; x < width; x += 1) {
            for (int y = 0; y < height; y += 1) {
                tiles[x][y] = Tileset.NOTHING;
            }
        }
    }

    public void createRoomsAndHallways() {
        rooms = new Room[numRooms.random()];

        hallways = new Hallway[rooms.length - 1];

        rooms[0] = new Room();
        hallways[0] = new Hallway();

        rooms[0].setUpRoom(roomWidth, roomHeight, columns, rows);
        hallways[0].setUpHallway(rooms[0], hallwayLength,
                roomWidth, roomHeight, columns, rows, true, R);

        for (int i = 1; i < rooms.length; i++) {
            rooms[i] = new Room();
            rooms[i].setUpRoom(roomWidth, roomHeight, columns, rows, hallways[i - 1], R);
            if (i < hallways.length) {
                hallways[i] = new Hallway();
                switch (specialDecider.random()) {
                    case 0:
                        hallways[i].setSpecial(true);
                        break;
                    default:
                        break;
                }
                hallways[i].setUpHallway(rooms[i], hallwayLength,
                        roomWidth, roomHeight, columns, rows, false, R);
            }
        }
    }

    public void setRoomTiles() {
        for (int i = 0; i < rooms.length; i += 1) {
            Room curr = rooms[i];
            for (int j = 0; j < curr.getRoomWidth(); j += 1) {
                int xCoordinate = curr.getxPos() + j;
                for (int k = 0; k < curr.getRoomHeight(); k += 1) {
                    int yCoordinate = curr.getyPos() + k;
                    world[xCoordinate][yCoordinate] = Tileset.FLOOR;
                }
            }
        }
    }

    public void setHallwayTiles() {
        for (int i = 0; i < hallways.length; i++) {
            Hallway currentHallway = hallways[i];
            for (int j = 0; j < currentHallway.getHallLength(); j++) {
                int xHall = currentHallway.getStartXPos();
                int yHall = currentHallway.getStartYPos();

                switch (currentHallway.getDirection()) {
                    case NORTH:
                        yHall += j;
                        break;
                    case EAST:
                        xHall += j;
                        break;
                    case SOUTH:
                        yHall -= j;
                        break;
                    case WEST:
                        xHall -= j;
                        break;
                    default:
                        break;
                }
                if (currentHallway.getSpecial()) {
                    if (currentHallway.getDirection() == Direction.NORTH
                            || currentHallway.getDirection() == Direction.SOUTH) {
                        if (xHall == 0) {
                            world[xHall + 1][yHall] = Tileset.FLOOR;
                        } else {
                            world[xHall - 1][yHall] = Tileset.FLOOR;
                        }
                    } else {
                        if (yHall == 0) {
                            world[xHall][yHall + 1] = Tileset.FLOOR;
                        } else {
                            world[xHall][yHall - 1] = Tileset.FLOOR;
                        }
                    }
                }
                world[xHall][yHall] = Tileset.FLOOR;
            }
        }
    }

    public void setTrueWorld() {
        for (int x = 0; x < trueWorld.length; x += 1) {
            trueWorld[x][0] = Tileset.NOTHING;
        }

        for (int y = 0; y < trueWorld[0].length; y += 1) {
            trueWorld[0][y] = Tileset.NOTHING;
        }

        for (int x = 0; x < world.length; x++) {
            for (int y = 0; y < world[0].length; y++) {
                trueWorld[x + 1][y + 1] = world[x][y];
            }
        }
    }

    public void buildWall() {
        buildPeripheryWall();

        for (int i = 0; i < columns + 1; i += 1) {
            for (int j = 0; j < rows + 1; j += 1) {
                if (surroundHelper(i, j) && !trueWorld[i][j].equals(Tileset.FLOOR)) {
                    trueWorld[i][j] = Tileset.WALL;
                }
            }
        }
    }

    private boolean surroundHelper(int x, int y) {
        if (x == 0 && y == 0) {
            return trueWorld[x + 1][y].equals(Tileset.FLOOR)
                    || trueWorld[x][y + 1].equals(Tileset.FLOOR);
        }
        if (x == 0 && y == rows) {
            return trueWorld[x + 1][y].equals(Tileset.FLOOR)
                    || trueWorld[x][y - 1].equals(Tileset.FLOOR);
        }
        if (x == columns && y == 0) {
            return trueWorld[x][y + 1].equals(Tileset.FLOOR)
                    || trueWorld[x - 1][y].equals(Tileset.FLOOR);
        }
        if (x == columns && y == rows) {
            return trueWorld[x][y - 1].equals(Tileset.FLOOR)
                    || trueWorld[x - 1][y].equals(Tileset.FLOOR);
        }
        if (x == 0) {
            return trueWorld[x + 1][y].equals(Tileset.FLOOR)
                    || trueWorld[x][y + 1].equals(Tileset.FLOOR)
                    || trueWorld[x][y - 1].equals(Tileset.FLOOR);
        }
        if (y == 0) {
            return trueWorld[x + 1][y].equals(Tileset.FLOOR)
                    || trueWorld[x - 1][y].equals(Tileset.FLOOR)
                    || trueWorld[x][y + 1].equals(Tileset.FLOOR);
        }
        if (x == columns) {
            return trueWorld[x][y - 1].equals(Tileset.FLOOR)
                    || trueWorld[x - 1][y].equals(Tileset.FLOOR)
                    || trueWorld[x][y + 1].equals(Tileset.FLOOR);
        }
        if (y == rows) {
            return trueWorld[x + 1][y].equals(Tileset.FLOOR)
                    || trueWorld[x - 1][y].equals(Tileset.FLOOR)
                    || trueWorld[x][y - 1].equals(Tileset.FLOOR);
        }
        return trueWorld[x + 1][y].equals(Tileset.FLOOR)
                || trueWorld[x - 1][y].equals(Tileset.FLOOR)
                || trueWorld[x][y - 1].equals(Tileset.FLOOR)
                || trueWorld[x][y + 1].equals(Tileset.FLOOR);
    }

    public void buildPeripheryWall() {
        for (int x = 0; x < trueWorld.length; x++) {
            if (trueWorld[x][trueWorld[0].length - 2].equals(Tileset.FLOOR)) {
                trueWorld[x][trueWorld[0].length - 1] = Tileset.WALL;
            }
        }
        for (int y = 0; y < trueWorld[0].length; y++) {
            if (trueWorld[trueWorld.length - 2][y].equals(Tileset.FLOOR)) {
                trueWorld[trueWorld.length - 1][y] = Tileset.WALL;
            }
        }
    }

    public void setGeneratedWorld() {
        fillWithBlankTiles(generatedWorld);
        for (int x = 0; x < generatedWorld.length; x++) {
            for (int y = lowerBlankSpace; y < trueWorld[0].length + lowerBlankSpace; y++) {
                generatedWorld[x][y] = trueWorld[x][y - lowerBlankSpace];
            }
        }
    }

}
