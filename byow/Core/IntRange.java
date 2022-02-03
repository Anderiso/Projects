package byow.Core;

import java.util.Random;

public class IntRange {
    int min;
    int max;
    Random r;

    public IntRange(int min, int max, Random r) {
        this.min = min;
        this.max = max;
        this.r = r;
    }

    public int random() {
        return r.nextInt(max - min) + min;
    }
}
