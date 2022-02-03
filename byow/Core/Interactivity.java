package byow.Core;

import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Random;

public class Interactivity implements Serializable {
    private int xPosAvatar;
    private int yPosAvatar;
    private boolean darkWorldMode;
    private int score;
    private int sightRange = 5;
    private int appleX;
    private int appleY;
    private Random R;

    private int columns;
    private int rows;
    private String seed;
    private TETile[][] world;
    private TETile[][] darkWorld;
    private char[] actions;

    public Interactivity(int width, int height, String input) {
        columns = width;
        rows = height;
        darkWorldMode = false;
        score = 0;

        //parse input (set seed and actions)
        parseInput(input);

        //generate world (take in seed)
        WorldGenerator w = new WorldGenerator(width, height, this.seed);
        this.world = w.getWorld();
        this.darkWorld = this.world;

        //spawn avatar (set avatar)
        HashMap<String, Integer> coordinate = getSpawnLocation(this.world);
        xPosAvatar = coordinate.get("X");
        yPosAvatar = coordinate.get("Y");
        R = new Random(Long.parseLong(seed));
        appleX = getAppleX();
        appleY = getAppleY();

        //setAvatar()
        setAvatar();
        //take actions
        executeActions();

    }

    public void parseInput(String input) {
        int seedStartIndex = input.indexOf("n") + input.indexOf("N") + 2;
        int seedEndIndex = input.indexOf("s") + input.indexOf("S") + 1;
        int actionStartIndex = seedEndIndex + 1;
        this.seed = input.substring(seedStartIndex, seedEndIndex);
        this.actions = input.substring(actionStartIndex).toCharArray();

    }

    public void executeAction(char action) {

        switch (action) {
            case 'W':
            case 'w':
                upMove();
                break;
            case 'A':
            case 'a':
                leftMove();
                break;
            case 'S':
            case 's':
                downMove();
                break;
            case 'D':
            case 'd':
                rightMove();
                break;
            case 'M':
            case 'm':
                setDarkWorldMode();
                break;
            default:
                break;
        }
    }

    public void executeActions() {
        for (char action : actions) {
            executeAction(action);
            setAvatar();
        }
    }

    public TETile[][] getWorld() {
        if (darkWorldMode) {
            return this.darkWorld;
        }
        return this.world;
    }

    public void setDarkWorldMode() {
        if (this.darkWorldMode) {
            this.darkWorldMode = false;
        } else {
            this.darkWorldMode = true;
        }
    }

    public void setDarkWorld() {
        for (int x = 0; x < columns; x++) {
            for (int y = 0; y < rows; y++) {
                this.darkWorld[x][y] = Tileset.NOTHING;
                double dist = Math.sqrt(Math.pow(x - xPosAvatar, 2) + Math.pow(y - yPosAvatar, 2));
                if (dist < sightRange) {
                    this.darkWorld[x][y] = this.world[x][y];
                }


            }
        }
    }

    public void upMove() {
        String description = world[xPosAvatar][yPosAvatar + 1].description();
        if (yPosAvatar < rows - 1 && description.equals("the floor, safe to step on")) {
            yPosAvatar += 1;
        } else if (yPosAvatar < rows - 1 && description.equals("a flower! collect it for points")) {
            yPosAvatar += 1;
            changeScoreRespawnFlower();
        }
    }
    public void downMove() {
        String description = world[xPosAvatar][yPosAvatar - 1].description();
        if (yPosAvatar >= 1 && description.equals("the floor, safe to step on")) {
            yPosAvatar -= 1;
        } else if (yPosAvatar >= 1 && description.equals("a flower! collect it for points")) {
            yPosAvatar -= 1;
            changeScoreRespawnFlower();
        }
    }
    public void rightMove() {
        String description = world[xPosAvatar + 1][yPosAvatar].description();
        if (xPosAvatar < columns - 1 && description.equals("the floor, safe to step on")) {
            xPosAvatar += 1;
        } else if (xPosAvatar < columns - 1
                && description.equals("a flower! collect it for points")) {
            xPosAvatar += 1;
            changeScoreRespawnFlower();
        }

    }
    public void leftMove() {
        String description = world[xPosAvatar - 1][yPosAvatar].description();
        if (xPosAvatar >= 1 && description.equals("the floor, safe to step on")) {
            xPosAvatar -= 1;
        } else if (xPosAvatar >= 1 && description.equals("a flower! collect it for points")) {
            xPosAvatar -= 1;
            changeScoreRespawnFlower();
        }
    }
    public HashMap<String, Integer> getSpawnLocation(TETile[][] w) {
        HashMap<String, Integer> result = new HashMap<>();
        int x = 0;
        int y = 0;
        result.put("X", x);
        result.put("Y", y);

        for (x = 0; x < world.length; x++) {
            for (y = 0; y < world[0].length; y++) {
                if (w[x][y] != null && w[x][y].equals(Tileset.FLOOR)) {
                    result.put("X", x);
                    result.put("Y", y);
                    return result;
                }
            }
        }
        return result;
    }

    public void setAvatar() {
        WorldGenerator w = new WorldGenerator(columns, rows, this.seed);
        world = w.getWorld();
        world[xPosAvatar][yPosAvatar] = Tileset.AVATAR;
        while (!world[appleX][appleY].equals(Tileset.FLOOR)) {
            appleX = getAppleX();
            appleY = getAppleY();
        }
        world[appleX][appleY] = Tileset.FLOWER;
        setDarkWorld();
    }

    public int getScore() {
        return score;
    }

    public void changeScoreRespawnFlower() {
        score += 1;
        appleX = getAppleX();
        appleY = getAppleY();
        while (!world[appleX][appleY].equals(Tileset.FLOOR)) {
            appleX = getAppleX();
            appleY = getAppleY();
        }
        world[appleX][appleY] = Tileset.FLOWER;
    }

    public int getAppleX() {
        IntRange x = new IntRange(0, columns, R);
        IntRange y = new IntRange(0, rows, R);
        int xPos = x.random();
        int yPos = y.random();
        while (!world[xPos][yPos].equals(Tileset.FLOOR)) {
            xPos = x.random();
            yPos = y.random();
        }
        return xPos;
    }

    public int getAppleY() {
        IntRange x = new IntRange(0, columns, R);
        IntRange y = new IntRange(0, rows, R);
        int xPos = x.random();
        int yPos = y.random();
        while (!world[xPos][yPos].equals(Tileset.FLOOR)) {
            xPos = x.random();
            yPos = y.random();
        }
        return yPos;
    }

    public void setActions(String input) {
        this.actions = input.toCharArray();
        executeActions();
    }
}
