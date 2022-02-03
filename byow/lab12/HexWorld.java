package byow.lab12;
import org.junit.Test;
import static org.junit.Assert.*;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.util.Random;

/**
 * Draws a world consisting of hexagonal regions.
 */
public class HexWorld {

    private static final int WIDTH = 60;
    private static final int HEIGHT = 30;

    public static void main(String[] args) {
        // initialize the tile rendering engine with a window of size WIDTH x HEIGHT
        TERenderer ter = new TERenderer();
        ter.initialize(WIDTH, HEIGHT);

        // initialize tiles
        TETile[][] world = new TETile[WIDTH][HEIGHT];
        for (int x = 0; x < WIDTH; x += 1) {
            for (int y = 0; y < HEIGHT; y += 1) {
                world[x][y] = Tileset.NOTHING;
            }
        }
        addHexagon(5, 15, 15, world);
        ter.renderFrame(world);
    }

    public static void addHexagon(int size, int startPosX, int y, TETile[][] world) {
        int x = startPosX;
        int counter = 0;
        while (counter < size) {
            x += spacePrinter(size, counter);
            tilePrinter(size, counter, x, y, world);
            x = startPosX;
            y += 1;
            counter += 1;
        }
        counter -= 1;
        while (counter >= 0) {
            x += spacePrinter(size, counter);
            tilePrinter(size, counter, x, y, world);
            x = startPosX;
            y += 1;
            counter -= 1;
        }
    }

    public static int spacePrinter(int size, int current) {
        int counter = 0;
        int result = 0;
        while (counter < size - 1 - current) {
            result += 1;
            counter += 1;
        }
        return result;
    }

    public static void tilePrinter(int size, int current, int x, int y, TETile [][] world) {
        int counter = 0;
        while (counter < size + 2 * current) {
            world[x][y] = Tileset.FLOWER;
            x += 1;
            counter += 1;
        }
    }
}
