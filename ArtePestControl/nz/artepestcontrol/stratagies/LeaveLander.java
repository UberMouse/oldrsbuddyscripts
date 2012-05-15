package nz.artepestcontrol.stratagies;

import com.rsbuddy.script.methods.Objects;
import com.rsbuddy.script.methods.Widgets;
import com.rsbuddy.script.wrappers.GameObject;
import nz.artepestcontrol.misc.GameConstants;
import nz.artepestcontrol.misc.Utils;
import nz.uberutils.helpers.Options;
import nz.uberutils.helpers.Strategy;
import nz.uberutils.methods.UberMovement;

/**
 * Created by IntelliJ IDEA.
 * User: Taylor
 * Date: 4/10/11
 * Time: 6:05 PM
 * Package: nz.artepestcontrol.strategies;
 */
public class LeaveLander implements Strategy
{
    public void execute() {
        GameObject ladder = Objects.getNearest(GameConstants.LANDER_LADDERS);
        if (ladder != null) {
            UberMovement.turnTo(ladder);
            if (ladder.interact("Climb")) {
                for (int i = 0; i < 15 && Utils.inLander(); i++)
                    Utils.sleep(100);
            }
        }
    }

    public boolean isValid() {
        return Utils.inLander() &&
               !Widgets.getComponent(407, 3).getText().equals(Options.get("landerText"));
    }

    public String getStatus() {
        return "Leaving lander";
    }
}
