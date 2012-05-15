package nz.ubermouse.artealtar.utils;

import com.rsbuddy.script.methods.Calculations;
import com.rsbuddy.script.methods.Objects;
import com.rsbuddy.script.util.Filter;
import com.rsbuddy.script.wrappers.GameObject;
import com.rsbuddy.script.wrappers.Tile;
import nz.ubermouse.artealtar.data.GameConstants;
import nz.uberutils.helpers.Utils;

/**
 * Created by IntelliJ IDEA.
 * User: Taylor
 * Date: 5/23/11
 * Time: 2:37 PM
 * Package: nz.ubermouse.artealtar.utils;
 */
public class UberMovement extends nz.uberutils.methods.UberMovement
{

    public static GameObject getClosestDoor(Tile tile) {
        try {
            GameObject[] doors = Objects.getLoaded(new Filter<GameObject>()
            {
                public boolean accept(GameObject t) {
                    return Utils.arrayContains(GameConstants.DOORS_IDS, t.getId());
                }
            });
            GameObject oldDoor = null;
            for (GameObject door : doors) {
                if (oldDoor == null) {
                    oldDoor = door;
                }
                else {
                    if (tile == null || door == null) {
                        return null;
                    }
                    if (Calculations.distanceBetween(oldDoor.getLocation(), tile) >
                        Calculations.distanceBetween(door.getLocation(), tile)) {
                        oldDoor = door;
                    }
                }
            }
            if (oldDoor != null)
                return oldDoor;
            else
                return null;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Tile[] reversePath(Tile[] other) {
        Tile[] t = new Tile[other.length];
        for (int i = 0; i < t.length; i++) {
            t[i] = other[other.length - i - 1];
        }
        return t;
    }
}
