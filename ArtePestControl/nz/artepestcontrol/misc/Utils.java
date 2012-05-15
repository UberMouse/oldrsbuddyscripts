package nz.artepestcontrol.misc;

import com.rsbuddy.script.methods.Calculations;
import com.rsbuddy.script.methods.Widgets;
import nz.artepestcontrol.game.PestControl;
import nz.artepestcontrol.game.Portal;

/**
 * Created by IntelliJ IDEA.
 * User: Taylor
 * Date: 4/9/11
 * Time: 8:17 PM
 * Package: nz.artepestcontrol.threads;
 */
public class Utils extends nz.uberutils.helpers.Utils
{
    public static boolean inLander() {
        return Widgets.getComponent(407, 3) != null && Widgets.getComponent(407, 3).isValid();
    }

    public static boolean inGame() {
        return Widgets.getComponent(408, 1) != null && Widgets.getComponent(408, 1).isValid();
    }

    public static int getKnightHP() {
        try {
            return Integer.parseInt(Widgets.getComponent(408, 1).getText());
        } catch (Exception e) {
            return -1;
        }
    }

    public static Portal getPortal() {
        Portal closest = null;
        int dist = 9999999;
        for (Portal p : PestControl.portals) {
            if (p.isAlive() && p.isOpen() && Calculations.distanceTo(p.getLocation()) < dist) {
                dist = Calculations.distanceTo(p.getLocation());
                closest = p;
            }
        }
        if (closest != null)
            return closest;
        closest = null;
        dist = 9999999;
        for (Portal p : PestControl.portals) {
            if (p.isAlive() && Calculations.distanceTo(p.getLocation()) < dist) {
                dist = Calculations.distanceTo(p.getLocation());
                closest = p;
            }
        }
        return closest;
    }
}
