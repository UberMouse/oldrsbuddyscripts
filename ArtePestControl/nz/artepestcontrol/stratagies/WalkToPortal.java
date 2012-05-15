package nz.artepestcontrol.stratagies;

import com.rsbuddy.script.methods.Calculations;
import com.rsbuddy.script.methods.Walking;
import com.rsbuddy.script.wrappers.Tile;
import nz.artepestcontrol.game.PestControl;
import nz.artepestcontrol.game.Portal;
import nz.artepestcontrol.misc.Utils;
import nz.uberutils.helpers.Options;
import nz.uberutils.helpers.Strategy;
import nz.uberutils.helpers.UberPlayer;

/**
 * Created by IntelliJ IDEA.
 * User: Taylor
 * Date: 4/13/11
 * Time: 8:40 PM
 * Package: nz.artepestcontrol.strategies;
 */
public class WalkToPortal implements Strategy
{
    private Portal portal = null;

    public void execute() {
        if (portal != null) {
            if (Calculations.distanceTo(portal.getLocation()) > 100)
                PestControl.portals.clear();
            Tile tile = new Tile(portal.getLocation().getX(), portal.getLocation().getY()).randomize(3, 3);
            if (tile != null &&
                (!UberPlayer.isMoving() ||
                 (Walking.getDestination() != null && Calculations.distanceTo(Walking.getDestination()) <= 4))) {
                Walking.findPath(tile).traverse();
            }
        }
    }

    public boolean isValid() {
        if (!Utils.inGame() || !Options.getBoolean("attackPortals"))
            return false;
        Portal closest = Utils.getPortal();
        if (closest != null) {
            portal = closest;
            return Calculations.distanceTo(portal.getLocation()) > 15;
        }
        return false;
    }

    public String getStatus() {
        if (portal != null)
            return "Walking to portal: " + portal.getName();
        return "Walking to portal";
    }
}
