package d4d.com.subsidio.utils;

/**
 * Created by jp_leon on 24/10/2016.
 */

public class Sleeper {

    private Sleeper() {
    }

    public static void sleep(int ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
