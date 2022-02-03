package byow.Core;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import edu.princeton.cs.introcs.StdDraw;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.awt.*;

public class Engine {
    TERenderer ter = new TERenderer();
    /* Feel free to change the width and height. */
    public static final int WIDTH = 80;
    public static final int HEIGHT = 45;
    public static final File CWD = new File(System.getProperty("user.dir"));
    public static final File BYOW_DIR = join(CWD, "byow");
    public static final File CORE_DIR = join(BYOW_DIR, "Core");
    public static final File ARCHIVE = join(CORE_DIR, "ARCHIVE.txt");

    /**
     * Method used for exploring a fresh world. This method should handle all inputs,
     * including inputs from the main menu.
     */
    public void interactWithKeyboard() {
        try {
            if (!ARCHIVE.exists()) {
                ARCHIVE.createNewFile();
            }
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
        ter.initialize(WIDTH, HEIGHT);
        String inputs = mainMenu();
        Font font = new Font("Monaco", Font.BOLD, 16);
        StdDraw.setFont(font);
        Interactivity activity;
        if (inputs.equals("LOAD")) {
            System.out.println("LOAD");
            activity = loadInteractivity();
        } else {
            activity = new Interactivity(WIDTH, HEIGHT, inputs);
        }
        TETile[][] world = activity.getWorld();
        ter.renderFrame(world);
        boolean pressedColon = false;
        while (true) {
            if (StdDraw.hasNextKeyTyped()) {
                char key = StdDraw.nextKeyTyped();
                switch (key) {
                    case 'W':
                    case 'w':
                    case 'A':
                    case 'a':
                    case 'S':
                    case 's':
                    case 'D':
                    case 'd':
                    case 'M':
                    case 'm':
                        pressedColon = false;
                        activity.executeAction(key);
                        break;
                    case 'Q':
                    case 'q':
                        if (pressedColon) {
                            saveInteractivity(activity);
                            System.exit(0);
                            return;
                        }
                        break;
                    case ':':
                        pressedColon = true;
                        break;
                    default: continue;
                }

            }
            activity.setAvatar();
            world = activity.getWorld();
            ter.renderFrame(world);
            getMouseHoveringDescription(activity);
        }
    }

    public String mainMenu() {
        StdDraw.clear(new Color(0, 0, 0));
        StdDraw.setPenColor(StdDraw.WHITE);
        Font font = new Font("Monaco", Font.BOLD, 48);
        StdDraw.setFont(font);
        StdDraw.text(WIDTH / 2, HEIGHT / 4 * 3, "CS61B: THE GAME");
        font = new Font("Monaco", Font.PLAIN, 28);
        StdDraw.setFont(font);
        StdDraw.text(WIDTH / 2, HEIGHT / 2, "New Game (N)");
        StdDraw.text(WIDTH / 2, HEIGHT / 2 - 3, "Load Game (L)");
        StdDraw.text(WIDTH / 2, HEIGHT / 2 - 6, "Quit (Q)");
        StdDraw.show();

        String inputs = "";
        while (true) {
            if (StdDraw.hasNextKeyTyped()) {
                char key = StdDraw.nextKeyTyped();
                switch (key) {
                    case 'N':
                    case 'n':
                        StdDraw.clear(new Color(0, 0, 0));
                        StdDraw.text(WIDTH / 2, HEIGHT / 4 * 3, "Please enter a seed:");
                        StdDraw.show();
                        return solicitInputs(inputs);
                    case 'L':
                    case 'l':
                        return "LOAD";
                    case 'Q':
                    case 'q':
                        System.exit(0);
                        break;
                    default:
                        continue;
                }
            }

        }
    }

    public String solicitInputs(String inputs) {
        while (true) {
            if (StdDraw.hasNextKeyTyped()) {
                char input = StdDraw.nextKeyTyped();
                if ((input == 'S' || input == 's') && inputs.length() > 0) {
                    inputs = "n" + inputs + "s";
                    return inputs;
                }
                if (input == 8 && inputs.length() > 0) {
                    inputs = inputs.substring(0, inputs.length() - 1);
                }
                if (47 < input && input < 58) {
                    inputs += input;
                }
                StdDraw.setPenColor(StdDraw.BLACK);
                StdDraw.filledRectangle(0, HEIGHT / 4 * 3 - 3, WIDTH, 2);
                StdDraw.setPenColor(StdDraw.WHITE);
                StdDraw.text(WIDTH / 2, HEIGHT / 4 * 3 - 3, inputs);
                StdDraw.show();
            }
        }
    }

    public void getMouseHoveringDescription(Interactivity a) {
        int x = (int) Math.floor(StdDraw.mouseX());
        int y = (int) Math.floor(StdDraw.mouseY());
        StdDraw.setPenColor(StdDraw.BLACK);
        StdDraw.filledRectangle(0, HEIGHT, WIDTH, 2);
        StdDraw.setPenColor(StdDraw.WHITE);
        StdDraw.text(10, HEIGHT - 1, getTileDescription(x, y, a.getWorld()));

        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();

        StdDraw.text(WIDTH - 7, HEIGHT - 1, dateFormat.format(date));
        StdDraw.text(WIDTH - 40, HEIGHT - 1, "Score: " + a.getScore());
        StdDraw.text(WIDTH / 2, 2, "(M): Toggle Vision");

        StdDraw.show();
    }

    public String getTileDescription(int x, int y, TETile[][] world) {
        if (x < world.length && y < world[0].length) {
            if (world[x][y] != null) {
                return world[x][y].description();
            }
        }
        return " ";
    }

    public Interactivity loadInteractivity() {
        if (ARCHIVE.exists()) {
            return readObject(ARCHIVE, Interactivity.class);
        }
        return null;
    }

    public void saveInteractivity(Interactivity activity) {
        writeObject(ARCHIVE, activity);
    }

    /** Returns a byte array containing the serialized contents of OBJ. */
    static byte[] serialize(Serializable obj) {
        try {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            ObjectOutputStream objectStream = new ObjectOutputStream(stream);
            objectStream.writeObject(obj);
            objectStream.close();
            return stream.toByteArray();
        } catch (IOException excp) {
            System.out.println(excp.toString());
            throw error("Internal error serializing commit.");
        }
    }

    /** Return a GitletException whose message is composed from MSG and ARGS as
     *  for the String.format method. */
    static RuntimeException error(String msg, Object... args) {
        return new RuntimeException(String.format(msg, args));
    }

    /** Return the entire contents of FILE as a byte array.  FILE must
     *  be a normal file.  Throws IllegalArgumentException
     *  in case of problems. */
    static byte[] readContents(File file) {
        if (!file.isFile()) {
            throw new IllegalArgumentException("must be a normal file");
        }
        try {
            return Files.readAllBytes(file.toPath());
        } catch (IOException excp) {
            throw new IllegalArgumentException(excp.getMessage());
        }
    }

    /** Return the entire contents of FILE as a String.  FILE must
     *  be a normal file.  Throws IllegalArgumentException
     *  in case of problems. */
    static String readContentsAsString(File file) {
        return new String(readContents(file), StandardCharsets.UTF_8);
    }

    /** Write the result of concatenating the bytes in CONTENTS to FILE,
     *  creating or overwriting it as needed.  Each object in CONTENTS may be
     *  either a String or a byte array.  Throws IllegalArgumentException
     *  in case of problems. */
    static void writeContents(File file, Object... contents) {
        try {
            if (file.isDirectory()) {
                throw
                        new IllegalArgumentException("cannot overwrite directory");
            }
            BufferedOutputStream str =
                    new BufferedOutputStream(Files.newOutputStream(file.toPath()));
            for (Object obj : contents) {
                if (obj instanceof byte[]) {
                    str.write((byte[]) obj);
                } else {
                    str.write(((String) obj).getBytes(StandardCharsets.UTF_8));
                }
            }
            str.close();
        } catch (IOException | ClassCastException excp) {
            throw new IllegalArgumentException(excp.getMessage());
        }
    }

    /** Return an object of type T read from FILE, casting it to EXPECTEDCLASS.
     *  Throws IllegalArgumentException in case of problems. */
    static <T extends Serializable> T readObject(File file,
                                                 Class<T> expectedClass) {
        try {
            ObjectInputStream in =
                    new ObjectInputStream(new FileInputStream(file));
            T result = expectedClass.cast(in.readObject());
            in.close();
            return result;
        } catch (IOException | ClassCastException
                | ClassNotFoundException excp) {
            throw new IllegalArgumentException(excp.getMessage());
        }
    }

    /** Write OBJ to FILE. */
    static void writeObject(File file, Serializable obj) {
        writeContents(file, serialize(obj));
    }

    /** Return the concatentation of FIRST and OTHERS into a File designator */
    static File join(String first, String... others) {
        return Paths.get(first, others).toFile();
    }

    /** Return the concatentation of FIRST and OTHERS into a File designator */
    static File join(File first, String... others) {
        return Paths.get(first.getPath(), others).toFile();
    }

    /**
     * Method used for autograding and testing your code. The input string will be a series
     * of characters (for example, "n123sswwdasdassadwas", "n123sss:q", "lwww". The engine should
     * behave exactly as if the user typed these characters into the engine using
     * interactWithKeyboard.
     *
     * Recall that strings ending in ":q" should cause the game to quite save. For example,
     * if we do interactWithInputString("n123sss:q"), we expect the game to run the first
     * 7 commands (n123sss) and then quit and save. If we then do
     * interactWithInputString("l"), we should be back in the exact same state.
     *
     * In other words, both of these calls:
     *   - interactWithInputString("n123sss:q")
     *   - interactWithInputString("lww")
     *
     * should yield the exact same world state as:
     *   - interactWithInputString("n123sssww")
     *
     * @param input the input string to feed to your program
     * @return the 2D TETile[][] representing the state of the world
     */
    public TETile[][] interactWithInputString(String input) {
        // Fill out this method so that it run the engine using the input
        // passed in as an argument, and return a 2D tile representation of the
        // world that would have been drawn if the same inputs had been given
        // to interactWithKeyboard().
        //
        // See proj3.byow.InputDemo for a demo of how you can make a nice clean interface
        // that works for many different input types.

        TETile[][] finalWorldFrame = null;

        Interactivity activity;
        if (input.charAt(0) == 'l' || input.charAt(0) == 'L') {
            activity = loadInteractivity();
            if (input.length() > 1) {
                activity.setActions(input.substring(1));
            }
        } else {
            activity = new Interactivity(WIDTH, HEIGHT, input);
        }
        finalWorldFrame = activity.getWorld();
        if (input.charAt(input.length() - 1) == 'Q' || input.charAt(input.length() - 1) == 'q') {
            saveInteractivity(activity);
        }
        return finalWorldFrame;
    }
}
