package nz.artemonkfish.misc;

import com.rsbuddy.script.util.Random;
import nz.artemonkfish.ArteMonkFish;

/**
 * Created by IntelliJ IDEA.
 * User: Taylor
 * Date: 2/20/11
 * Time: 11:56 AM
 * Package: nz.uberfalconry.threads;
 */
public class Common
{
    protected static ArteMonkFish parent;
    public Common(ArteMonkFish parent) {
        this.parent = parent;
    }

    /**
	 * Sleep script.
	 *
	 * @param min the min time
	 * @param max the max time
	 */
	protected static void sleep(int min, int max) {
		parent.sleep(random(min, max));
	}

	/**
	 * Sleep script.
	 *
	 * @param time the time
	 */
	protected static void sleep(int time) {
		parent.sleep(time);
	}

	/**
	 * Generate random number.
	 *
	 * @param min the min value
	 * @param max the max value
	 * @return the random number
	 */
	protected static int random(int min, int max) {
		return Random.nextInt(min, max);
	}
}
