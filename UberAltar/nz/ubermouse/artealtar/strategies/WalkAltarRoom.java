package nz.ubermouse.artealtar.strategies;

import com.rsbuddy.script.methods.Calculations;
import com.rsbuddy.script.methods.Objects;
import com.rsbuddy.script.wrappers.GameObject;
import nz.ubermouse.artealtar.ArteAltar;
import nz.ubermouse.artealtar.data.GameConstants;
import nz.ubermouse.artealtar.data.Shared;
import nz.ubermouse.artealtar.utils.Debug;
import nz.ubermouse.artealtar.utils.UberMovement;
import org.rsbuddy.tabs.Inventory;

/**
 * Created by IntelliJ IDEA.
 * User: Taylor
 * Date: 5/23/11
 * Time: 2:48 PM
 * Package: nz.ubermouse.artealtar.strategies;
 */
public class WalkAltarRoom extends Debug implements ArteAltar.Strategy
{

    public void execute() {
        Shared.addedTrip = false;
        GameObject altar = Objects.getNearest(GameConstants.ALTAR_ID);
        UberMovement.turnTo(altar);
        GameObject door = Objects.getNearest(GameConstants.DOORS_IDS);
        if(door != null && Calculations.distanceBetween(door.getLocation(), altar.getLocation()) <= 8)
            door.interact("Open");
        debug("Walking to altar");
    }

    public boolean isValid() {
        GameObject altar = Objects.getNearest(GameConstants.ALTAR_ID);
        return Inventory.getCount(Shared.boneid) != 0 && altar != null && Calculations.distanceTo(altar) > 4;
    }
}
