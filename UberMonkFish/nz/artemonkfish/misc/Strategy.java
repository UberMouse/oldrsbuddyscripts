package nz.artemonkfish.misc;

import nz.artemonkfish.ArteMonkFish;

/**
 * Created by IntelliJ IDEA.
 * User: Taylor
 * Date: 2/20/11
 * Time: 11:51 AM
 * Package: nz.uberfalconry;
 */
public abstract class Strategy extends Common
{
    public Strategy(ArteMonkFish parent) {
        super(parent);
    }

    public abstract void execute();

    public abstract boolean isValid();

    public abstract String getStatus();
}
