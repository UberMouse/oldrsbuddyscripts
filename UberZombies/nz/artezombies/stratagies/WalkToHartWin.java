package nz.artezombies.stratagies;

import com.rsbuddy.script.methods.Calculations;
import com.rsbuddy.script.methods.Game;
import com.rsbuddy.script.methods.Objects;
import com.rsbuddy.script.wrappers.GameObject;
import nz.artezombies.misc.GameConstants;
import nz.artezombies.misc.Utils;
import nz.uberutils.helpers.Strategy;
import nz.uberutils.methods.UberMovement;
import nz.uberutils.methods.UberObjects;

/**
 * Created by IntelliJ IDEA.
 * User: Taylor
 * Date: 4/20/11
 * Time: 6:38 PM
 * Package: nz.artezombies.strategies;
 */
public class WalkToHartWin implements Strategy
{
    public void execute() {
        if (Game.getFloorLevel() == 0 && Calculations.distanceTo(GameConstants.DOOR_TILE) > 15) {
            GameConstants.BANK_PATH_REV.traverse();
        }
        if (Calculations.distanceTo(GameConstants.DOOR_TILE) <= 15) {
            if (UberObjects.getTopAt(GameConstants.DOOR_TILE, GameConstants.DOORID) != null) {
                GameObject door = UberObjects.getTopAt(GameConstants.DOOR_TILE, GameConstants.DOORID);
                UberMovement.turnTo(door);
                Utils.waitUntilMoving(5);
                Utils.waitUntilStopped(5);
                door.interact("Open");
                return;
            }
            if (Game.getFloorLevel() == 1 || Game.getFloorLevel() == 0) {
                GameObject stair = Objects.getNearest(GameConstants.STAIRIDS);
                UberMovement.turnTo(stair);
                if (stair != null) {
                    if (stair.interact("Climb-up")) {
                        int level = Game.getFloorLevel();
                        for (int i = 0; i < 15 && Game.getFloorLevel() == level; i++)
                            Utils.sleep(100);
                    }
                }
            }
        }
    }

    public boolean isValid() {
        return true;
    }

    public String getStatus() {
        return "Walking to hartwin";
    }
}
