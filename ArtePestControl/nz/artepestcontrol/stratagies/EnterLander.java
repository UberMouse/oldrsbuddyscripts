package nz.artepestcontrol.stratagies;

import com.rsbuddy.script.methods.*;
import com.rsbuddy.script.wrappers.GameObject;
import nz.artepestcontrol.game.PestControl;
import nz.artepestcontrol.misc.Shared;
import nz.artepestcontrol.misc.Utils;
import nz.uberutils.helpers.Options;
import nz.uberutils.helpers.Strategy;
import nz.uberutils.methods.UberMovement;

/**
 * Created by IntelliJ IDEA.
 * User: Taylor
 * Date: 4/9/11
 * Time: 7:00 PM
 * Package: nz.artepestcontrol.strategies;
 */
public class EnterLander implements Strategy
{
    public void execute() {
        if (PestControl.portals.isEmpty())
            PestControl.portals.clear();
        if(Menu.isOpen())
            Mouse.moveRandomly(1000);
        GameObject gangplank = Objects.getNearest(Options.getInt("landerGangplank"));
        if (gangplank != null) {
            UberMovement.turnTo(gangplank);
            Camera.setCompassAngle(Utils.random(150, 210));
            if (gangplank.interact("Cross")) {
                for (int i = 0; i < 15 && !Utils.inLander(); i++)
                    Utils.sleep(100);
            }
        }
        else {
            try {
                if (Walking.findPath(Shared.landerTile) != null) {
                    Walking.findPath(Shared.landerTile).traverse();
                    Utils.waitUntilStopped(15);
                }
            } catch (Exception ignored) {
            }
        }
    }

    public boolean isValid() {
        return !Utils.inLander() && !Utils.inGame();
    }

    public String getStatus() {
        return "Entering lander";
    }
}
