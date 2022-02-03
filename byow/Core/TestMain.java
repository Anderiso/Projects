package byow.Core;

import byow.TileEngine.*;
//hi
public class TestMain {
    public static void main(String[] args) {
        TERenderer ter = new TERenderer();
        ter.initialize(80, 45);
        Engine engine = new Engine();
        System.out.println();
        TETile[][] world = engine.interactWithInputString("n912756s");
        ter.renderFrame(world);

        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println();
        System.out.println();
        world = engine.interactWithInputString("n9127564470038628925swa:q");
        ter.renderFrame(world);

        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        world = engine.interactWithInputString("lwasaasswadada:q");
        ter.renderFrame(world);

        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        world = engine.interactWithInputString("ladds");
        ter.renderFrame(world);

    }
}
