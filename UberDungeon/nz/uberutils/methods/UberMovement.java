package nz.uberutils.methods;

import com.rsbuddy.script.methods.Calculations;
import com.rsbuddy.script.methods.Walking;
import com.rsbuddy.script.util.Random;
import com.rsbuddy.script.wrappers.GameObject;
import com.rsbuddy.script.wrappers.Locatable;
import com.rsbuddy.script.wrappers.Npc;
import com.rsbuddy.script.wrappers.Tile;
import nz.uberutils.helpers.UberPlayer;
import nz.uberutils.helpers.Utils;



public class UberMovement
{
    /**
     * Turns camera to Locatable and walks to it if off screen still.
     *
     * @param tile the Locatable
     */
    public static void turnTo(Locatable tile) {
        if (tile == null)
            return;
        if (!Calculations.isTileOnScreen(tile.getLocation())) {
            if (Calculations.distanceTo(tile) > 6)
                Walking.getTileOnMap(tile.getLocation()).clickOnMap();
            UberCamera.turnTo(tile.getLocation(), Random.nextInt(10, 25));
            if (!Calculations.isTileOnScreen(tile.getLocation()))
                Walking.getTileOnMap(tile.getLocation()).clickOnMap();
        }
    }

    /**
     * Reverse path.
     *
     * @param other the path to reverse
     * @return the reversed path
     */
    public static Tile[] reversePath(Tile[] other) {
        Tile[] t = new Tile[other.length];
        for (int i = 0; i < t.length; i++) {
            t[i] = other[other.length - i - 1];
        }
        return t;
    }

    public static void walkTo(Tile tile) {
        UberMovement.turnTo(tile);
        if (!UberPlayer.isMoving() && Calculations.isTileOnScreen(tile))
            if(tile.interact("Walk"))
                Utils.waitToStop();
    }

    public static void walkTo(GameObject object) {
        walkTo(object.getLocation());
    }

    public static void walkTo(Npc n) {
        walkTo(n.getLocation());
    }

}
